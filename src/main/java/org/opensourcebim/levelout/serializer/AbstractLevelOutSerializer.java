package org.opensourcebim.levelout.serializer;

import org.bimserver.BimserverDatabaseException;
import org.bimserver.emf.IfcModelInterface;
import org.bimserver.geometry.Matrix;
import org.bimserver.models.geometry.GeometryInfo;
import org.bimserver.models.ifc4.*;
import org.bimserver.plugins.serializers.ProjectInfo;
import org.bimserver.plugins.serializers.Serializer;
import org.bimserver.plugins.serializers.SerializerException;
import org.bimserver.utils.GeometryUtils;
import org.bimserver.utils.IfcUtils;
import org.eclipse.emf.common.util.EList;
import org.opensourcebim.levelout.intermediatemodel.*;
import org.opensourcebim.levelout.intermediatemodel.geo.*;
import org.opensourcebim.levelout.samples.IntermediateResidential;
import org.slf4j.LoggerFactory;

import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.io.OutputStream;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractLevelOutSerializer implements Serializer {
	private final boolean ignoreAbstractElements;
	private final boolean extractionMethod;
	private final boolean ignoreDeadRooms;
	Building building;

	AbstractLevelOutSerializer(AbstractLevelOutSerializerPlugin.Options options) {
		this.extractionMethod = options.extractionMethod;
		this.ignoreAbstractElements = options.ignoreAbstractElements;
		this.ignoreDeadRooms = options.ignoreDeadRooms;
	}

	@Override
	public void init(IfcModelInterface ifcModelInterface, ProjectInfo projectInfo, boolean b) {
		if (extractionMethod) {
			initStructure(ifcModelInterface);
		} else initSample();
	}


	private void initSample() {
		building = IntermediateResidential.create();
	}

	private void initStructure(IfcModelInterface ifcModelInterface) {
		CoordinateReference crsFromIFC = getCrs(ifcModelInterface);
		CoordinateReference crs = crsFromIFC != null ? crsFromIFC : new GeodeticOriginCRS(new GeodeticPoint(0, 0), 0); // TODO use some default location in Weimar, Dresden ..
		List<IfcBuildingStorey> storeys = ifcModelInterface.getAllWithSubTypes(IfcBuildingStorey.class);
		if(storeys.isEmpty()) {
			building = new Building(List.of(), List.of(), crs);
			return;
		}
		int level = 0;
		storeys.sort(Comparator.comparingDouble(IfcBuildingStorey::getElevation));
		double minDistance = Double.MAX_VALUE;
		while(-level < storeys.size() && Math.abs(storeys.get(-level).getElevation()) < minDistance){
			// this might be the highest negative or the lowest positive whichever is closer to zero
			// actually, walkout-basements should be counted as level 0 in OSM, but we cannot know
			minDistance = Math.abs(storeys.get(-level).getElevation());
			level--;
		}
		level++;
		Area storeyFootprint = new Area();
		IfcBuildingStorey groundFloor = storeys.get(-level);
		for(IfcRelContainedInSpatialStructure containment: groundFloor.getContainsElements()){
			for(IfcProduct element : containment.getRelatedElements()){
				Area footprint = getFootprint(element.getGeometry(), groundFloor.getElevation());
				storeyFootprint.add(footprint);
			}
		}
		for(IfcRelAggregates aggregation: groundFloor.getIsDecomposedBy()){
			for(IfcObjectDefinition element: aggregation.getRelatedObjects()){
				if(element instanceof  IfcProduct){
					Area footprint = getFootprint(((IfcProduct) element).getGeometry(), groundFloor.getElevation());
					storeyFootprint.add(footprint);
				}
			}
		}
		List<Storey> loStoreys = new ArrayList<>();
		Area storeyOutline = getOutline(storeyFootprint);
		building = new Building(loStoreys, getCorners(storeyOutline), crs);
		for (IfcBuildingStorey storey : storeys) {
			double elevation = storey.getElevation();
			Storey loStorey = new Storey(level++, elevation, storey.getName());
			loStoreys.add(loStorey);
			Map<IfcSpace, Room> roomsMap = new HashMap<>();
			for (IfcRelAggregates aggregation : storey.getIsDecomposedBy()) {
				for (IfcSpace space : aggregation.getRelatedObjects().stream().filter(IfcSpace.class::isInstance).map(IfcSpace.class::cast).toArray(IfcSpace[]::new)) {
					Room room = new Room(space.getName(), getOuterPolygon(space.getGeometry(), elevation));
					roomsMap.put(space, room); // later needed for assignment to doors
				}
			}
			for (IfcRelContainedInSpatialStructure containment : storey.getContainsElements()) {
				// TODO windows ?
				for (IfcDoor ifcDoor : containment.getRelatedElements().stream().filter(IfcDoor.class::isInstance).map(IfcDoor.class::cast).toArray(IfcDoor[]::new)) {
					if (ifcDoor.getFillsVoids().size()!=1) continue; // TODO warning if >1, handle standalone
					IfcOpeningElement opening = ifcDoor.getFillsVoids().get(0).getRelatingOpeningElement();
					Area openingFootprint = (opening.getVoidsElements()!=null && opening.getVoidsElements().getRelatingBuildingElement()!=null)
						? getIntersectionArea(elevation, opening, opening.getVoidsElements().getRelatingBuildingElement())
						: getFootprint(opening.getGeometry(), elevation);
                    // TODO warn if no host for opening! This is invalid as per IFC4
					Door door = new Door( ifcDoor.getName(), getCorners(openingFootprint));
					if(door.hasGeometry() || !ignoreAbstractElements) {
						loStorey.addDoors(door);
						EList<IfcRelSpaceBoundary> doorBoundaries = ifcDoor.getProvidesBoundaries();
						populateConnectedRooms(roomsMap, door, doorBoundaries); // TODO create door only if successfull?
					}
				}
				for(IfcWall ifcWall : containment.getRelatedElements().stream().filter(IfcWall.class::isInstance).map(IfcWall.class::cast).collect(Collectors.toList())){
					for(IfcRelVoidsElement voids: ifcWall.getHasOpenings()){
						if(voids.getRelatedOpeningElement() instanceof IfcOpeningElement){
							IfcOpeningElement opening = (IfcOpeningElement) voids.getRelatedOpeningElement();
							if(opening.getHasFillings().isEmpty()) {  // doors are already processed, windows ignored, only treat unfilled openings
								// TODO track processed doors above and use this as fallback?
								Area openingFootprint = getIntersectionArea(elevation, opening, ifcWall);
								Door door = new Door( opening.getName(), getCorners(openingFootprint));
								if(door.hasGeometry() || !ignoreAbstractElements ){
									loStorey.addDoors(door);
									populateConnectedRooms(roomsMap, door, opening.getProvidesBoundaries());
								}
							}
						}
					}
					List<IfcRelSpaceBoundary> processed = new ArrayList<>();
					for (IfcRelSpaceBoundary spaceBoundary : ifcWall.getProvidesBoundaries()) {
						// TODO use these for room-wall-room connections possibly without a door
						if(spaceBoundary instanceof IfcRelSpaceBoundary2ndLevel && !processed.contains(spaceBoundary)) {
							processed.add(spaceBoundary);
							IfcRelSpaceBoundary otherSpaceBoundary = ((IfcRelSpaceBoundary2ndLevel)spaceBoundary).getCorrespondingBoundary();
							if(otherSpaceBoundary!= null) {
								processed.add(otherSpaceBoundary);
								// populateConnectedRooms(roomsMap, door, List.of(spaceBoundary, otherSpaceBoundary));
								break;
							}
						}
					}
				}
			}
			if (ignoreDeadRooms) {
				for(Door door: loStorey.getDoors()){
					if(door.getRoom1()!=null) loStorey.addRooms(door.getRoom1());
					if(door.getRoom2()!=null) loStorey.addRooms(door.getRoom2());
				}
			} else {
				for (Room room: roomsMap.values())
					loStorey.addRooms(room);
			}

		}
	}

	private Area getIntersectionArea(double elevation, IfcOpeningElement opening, IfcElement voidedElement) {
		Area voidedElementFootprint = getFootprint(voidedElement.getGeometry(), elevation); // TODO outline needed?
		Area openingFootprint = getFootprint(opening.getGeometry(), elevation);
		openingFootprint.intersect(voidedElementFootprint); // destructive method
		return openingFootprint;
	}

	private static boolean populateConnectedRooms(Map<IfcSpace, Room> roomsMap, Door door, List<IfcRelSpaceBoundary> openingBoundaries) {
		List<Room> connectedSpaces = new ArrayList<>();
		boolean external = false;
		List<IfcSpace> uniqueSpaces = new ArrayList<>();
		for (IfcRelSpaceBoundary openingBoundary : openingBoundaries) {
			if (openingBoundary.getRelatingSpace() instanceof IfcSpace && ! uniqueSpaces.contains(openingBoundary.getRelatingSpace())) {
				uniqueSpaces.add((IfcSpace) openingBoundary.getRelatingSpace());
				connectedSpaces.add(roomsMap.get((IfcSpace) openingBoundary.getRelatingSpace()));
			} else if (openingBoundary.getRelatingSpace() instanceof IfcExternalSpatialElement) {
				external = true;
			}
		}
		if(connectedSpaces.size() == 1){
			if(!external){
				// TODO warning, just 1 space no external
			}
			door.setExternal(connectedSpaces.get(0));
			return true;
		}
		if(connectedSpaces.size() >= 2){
			if(connectedSpaces.size()>2){
				LoggerFactory.getLogger(AbstractLevelOutSerializer.class).warn("Door or opening connected to  more than two spaces: " + door.getName());
			}
			door.setInternal(connectedSpaces.get(0), connectedSpaces.get(1));
			return true;
		}
		return false;
	}

	private CoordinateReference getCrs(IfcModelInterface ifcModelInterface) {
		// TODO common classes for checking and querying
		List<IfcProject> project = ifcModelInterface.getAllWithSubTypes(IfcProject.class);
		if (!(project.size() == 1)) return null;
		if (!(project.get(0).getIsDecomposedBy().size() == 1)) return null;
		if (!(project.get(0).getIsDecomposedBy().get(0).getRelatedObjects().size() == 1)) return null;
		if (!(project.get(0).getIsDecomposedBy().get(0).getRelatedObjects().get(0) instanceof IfcSite)) return null;
		IfcSite site = (IfcSite) project.get(0).getIsDecomposedBy().get(0).getRelatedObjects().get(0);
		List<IfcRepresentationContext> ifcRepresentationContextStream = project.get(0).getRepresentationContexts().stream().filter(ifcRepresentationContext ->
			"Model".equals(ifcRepresentationContext.getContextType()) && ifcRepresentationContext instanceof IfcGeometricRepresentationContext
		).collect(Collectors.toList());
		if (ifcRepresentationContextStream.isEmpty() || ! ((IfcGeometricRepresentationContext)ifcRepresentationContextStream.get(0)).isSetTrueNorth()) return null;
		IfcGeometricRepresentationContext context = (IfcGeometricRepresentationContext) project.get(0).getRepresentationContexts().get(0);
		double factor = IfcUtils.getLengthUnitPrefix(ifcModelInterface);
		CoordinateReference cr = getProjectedOriginCRS(context, factor);
		return (cr != null) ? cr : getGeodeticOriginCRS(site, context, factor);
	}

	private static GeodeticOriginCRS getGeodeticOriginCRS(IfcSite site, IfcGeometricRepresentationContext context, double scale) {
		IfcDirection trueNorth = context.getTrueNorth();
		if (!(trueNorth.getDirectionRatios()!=null && trueNorth.getDirectionRatios().size() == 2)) return null;
		EList<Long> refLatitude = site.getRefLatitude();
		if (!(refLatitude != null && refLatitude.size() >= 3)) return null;
		EList<Long> refLongitude = site.getRefLongitude();
		if (!(refLongitude != null && refLongitude.size() >= 3)) return null;
		double rotation = - (Math.atan2(trueNorth.getDirectionRatios().get(1), trueNorth.getDirectionRatios().get(0)) - Math.PI/2);
		double latitude = degreesFromMinutes(refLatitude);
		double longitude = degreesFromMinutes(refLongitude);
		return new GeodeticOriginCRS(new GeodeticPoint(latitude, longitude), rotation, scale);
	}

	private static CoordinateReference getProjectedOriginCRS(IfcGeometricRepresentationContext context, double scale) {
		Optional<IfcCoordinateOperation> first = context.getHasCoordinateOperation().stream().filter(co ->
			co instanceof IfcMapConversion && co.getTargetCRS() != null
		).findFirst();
		if(first.isEmpty()) return null;
		IfcMapConversion crs = ((IfcMapConversion) first.get());
		crs.getScale(); // TODO consider this as well
		return new ProjectedOriginCRS(new ProjectedPoint(crs.getEastings(), crs.getNorthings()), crs.getXAxisAbscissa(), crs.getXAxisOrdinate(), scale, crs.getTargetCRS().getName());
	}


	private List<Corner> getOuterPolygon(GeometryInfo geometry, double elevation) {
		// new IfcTools2D().get2D(space, 1); // only for IFC 2x3
		Area area = getOutline(getFootprint(geometry, elevation));
		return getCorners(area);
	}

	private List<Corner> getCorners(Area area) {
		if(! area.isSingular()){
			// TODO warn multiple disconnected areas?
		}
		List<Corner> corners = new ArrayList<>();
		if( area.isEmpty()) {
			return corners; // TODO log warning or ignore these rooms/doors?
		}
		PathIterator pathIterator = area.getPathIterator(null);
		float[] coords = new float[6];
		int firstSegment = pathIterator.currentSegment(coords);
		assert !pathIterator.isDone() && firstSegment == PathIterator.SEG_MOVETO;
		corners.add(new Corner(coords[0], coords[1]));
		pathIterator.next();
		while (!pathIterator.isDone() && pathIterator.currentSegment(coords) == PathIterator.SEG_LINETO) {
			corners.add(new Corner(coords[0], coords[1]));
			pathIterator.next();
		}
		assert pathIterator.isDone() && pathIterator.currentSegment(coords) == PathIterator.SEG_CLOSE; // eqaul to outerArea.isSingular() check above
		return corners;
	}

	private Area getOutline(Area area) {
		// TODO move area union to spatial analysis util class
		Area outline = new Area();
		double[] coords = new double[6];
		GeneralPath path = new GeneralPath();
		PathIterator pathIterator = area.getPathIterator(null);
		while(!pathIterator.isDone()){
			int type = pathIterator.currentSegment(coords);
			if(type == PathIterator.SEG_MOVETO){
				path.reset();
				path.moveTo(coords[0], coords[1]);
			} else if (type== PathIterator.SEG_LINETO) {
				path.lineTo(coords[0], coords[1]);
			} else if (type== PathIterator.SEG_CLOSE){
				path.closePath();
				outline.add(new Area(path));
			} else {
				// TODO unhandled segment type (cubic etc.), should not be the case
			}
			pathIterator.next();
		}
		return outline;
	}

	private Area getFootprint(GeometryInfo geometry, double elevation) {
		Area area = new Area();
		if(geometry == null) return area;
		int[] indices = GeometryUtils.toIntegerArray(geometry.getData().getIndices().getData());
		double[] vertices = GeometryUtils.toDoubleArray(geometry.getData().getVertices().getData());
		double[] matrix = GeometryUtils.toDoubleArray(geometry.getTransformation());
		int multiplierMillimeters = 1; // TODO necessary? handle units?
		for (int i = 0; i < indices.length; i += 3) {
			Path2D.Float path = new Path2D.Float();
			for (int j = 0; j < 3; j++) {
				int idx = indices[i + j];
				double[] point = new double[]{vertices[idx * 3], vertices[idx * 3 + 1], vertices[idx * 3 + 2]};
				double[] res = new double[4];
				Matrix.multiplyMV(res, 0, matrix, 0, new double[]{point[0], point[1], point[2], 1}, 0);
				double x = res[0] * multiplierMillimeters;
				double y = res[1] * multiplierMillimeters;
				double z = res[2] * multiplierMillimeters;
				assert z >= elevation;
				if (j == 0) {
					path.moveTo(x, y);
				} else {
					path.lineTo(x, y);
				}
			}
			path.closePath();
			area.add(new Area(path));
		}
		return area;
	}

	private static double degreesFromMinutes(EList<Long> refLatitude) {
		double fraction = refLatitude.size()>3 ? refLatitude.get(3) / (3600*1000000.) : 0;
		return refLatitude.get(0) + refLatitude.get(1) / 60. + refLatitude.get(2) / 3600. + fraction;
	}

    @Override
    public boolean write(OutputStream outputStream) throws SerializerException, BimserverDatabaseException {
        this.writeToOutputStream(outputStream, null);
        return true;
    }
}
