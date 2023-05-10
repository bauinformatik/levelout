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

import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractLevelOutSerializer implements Serializer {
	private final boolean abstractElements;
	private final boolean extractionMethod;
	private final boolean deadRooms;
	Building building;

	AbstractLevelOutSerializer(AbstractLevelOutSerializerPlugin.Options options) {
		this.extractionMethod = options.extractionMethod;
		this.abstractElements = options.abstractElements;
		this.deadRooms = options.deadRooms;
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
		List<Storey> loStoreys = new ArrayList<>();
		List<Corner> outline = new ArrayList<>(); // TODO populate, move area union to spatial analysis util class
		building = new Building(loStoreys, outline, crs);
		List<IfcBuildingStorey> storeys = ifcModelInterface.getAllWithSubTypes(IfcBuildingStorey.class);
		int level = 0;
		storeys.sort(Comparator.comparingDouble(IfcBuildingStorey::getElevation));
		double minDistance = Double.MAX_VALUE;
		while(-level < storeys.size() && Math.abs(storeys.get(-level).getElevation()) < minDistance){
			// this might be the highest negative or the lowest positive whichever is closer to zero
			// actually, walkout-basements should be counted as level 0 in OSM, but we cannot know
			minDistance = Math.abs(storeys.get(-level).getElevation());
			level--;
		}
		for (IfcBuildingStorey storey : storeys) {
			double elevation = storey.getElevation();
			Storey loStorey = new Storey(++level, elevation, storey.getName());
			loStoreys.add(loStorey);
			Map<IfcSpace, Room> roomsMap = new HashMap<>();
			for (IfcRelAggregates aggregation : storey.getIsDecomposedBy()) {
				for (IfcSpace space : aggregation.getRelatedObjects().stream().filter(IfcSpace.class::isInstance).map(IfcSpace.class::cast).toArray(IfcSpace[]::new)) {
					Room room = new Room(space.getName(), getPolygon(space.getGeometry(), elevation));
					roomsMap.put(space, room); // later needed for assignment to doors
				}
			}
			for (IfcRelContainedInSpatialStructure containment : storey.getContainsElements()) {
				// TODO windows ?
				for (IfcDoor ifcDoor : containment.getRelatedElements().stream().filter(IfcDoor.class::isInstance).map(IfcDoor.class::cast).toArray(IfcDoor[]::new)) {
					if (ifcDoor.getFillsVoids().size()!=1) continue; // TODO warning if >1, handle standalone
					IfcOpeningElement opening = ifcDoor.getFillsVoids().get(0).getRelatingOpeningElement();
					Door door = new Door( ifcDoor.getName(), getPolygon(opening.getGeometry(), elevation));
					if(!door.getCorners().isEmpty() || abstractElements) {
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
								Door door = new Door( getPolygon(opening.getGeometry(), elevation));
								if(!door.getCorners().isEmpty() || abstractElements){
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
			if(deadRooms) for (Room room: roomsMap.values())
				loStorey.addRooms(room);
			else for(Door door: loStorey.getDoors()){
				if(door.getRoom1()!=null) loStorey.addRooms(door.getRoom1());
				if(door.getRoom2()!=null) loStorey.addRooms(door.getRoom2());
			}

		}
	}

	private static boolean populateConnectedRooms(Map<IfcSpace, Room> roomsMap, Door door, List<IfcRelSpaceBoundary> openingBoundaries) {
		List<Room> connectedSpaces = new ArrayList<>();
		boolean external = false;
		for (IfcRelSpaceBoundary openingBoundary : openingBoundaries) {
			if (openingBoundary.getRelatingSpace() instanceof IfcSpace) {
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
				// TODO warning more than 2 connected spaces
			}
			door.setInternal(connectedSpaces.get(0), connectedSpaces.get(1));
			return true;
		}
		return false;
	}

	private CoordinateReference getCrs(IfcModelInterface ifcModelInterface) {
		// TODO common classes for checking and querying
		List<IfcSite> site = ifcModelInterface.getAllWithSubTypes(IfcSite.class);
		List<IfcProject> project = ifcModelInterface.getAllWithSubTypes(IfcProject.class);
		if (!(project.size() == 1)) return null;
		if (!(site.size() == 1)) return null;
		if (!(project.get(0).getIsDecomposedBy().size() == 1)) return null;
		if (!(project.get(0).getIsDecomposedBy().get(0).getRelatedObjects().size() == 1)) return null;
		if (!project.get(0).getIsDecomposedBy().get(0).getRelatedObjects().get(0).getGlobalId().equals(site.get(0).getGlobalId()))
			return null; // or even identity on entity level?
		List<IfcRepresentationContext> ifcRepresentationContextStream = project.get(0).getRepresentationContexts().stream().filter(ifcRepresentationContext ->
			"Model".equals(ifcRepresentationContext.getContextType()) && ifcRepresentationContext instanceof IfcGeometricRepresentationContext
		).collect(Collectors.toList());
		if (ifcRepresentationContextStream.isEmpty() || ! ((IfcGeometricRepresentationContext)ifcRepresentationContextStream.get(0)).isSetTrueNorth()) return null;
		IfcGeometricRepresentationContext context = (IfcGeometricRepresentationContext) project.get(0).getRepresentationContexts().get(0);
		double factor = IfcUtils.getLengthUnitPrefix(ifcModelInterface);
		CoordinateReference cr = getProjectedOriginCRS(context, factor);
		return (cr != null) ? cr : getGeodeticOriginCRS(site, context, factor);
	}

	private static GeodeticOriginCRS getGeodeticOriginCRS(List<IfcSite> site, IfcGeometricRepresentationContext context, double scale) {
		IfcDirection trueNorth = context.getTrueNorth();
		if (!(trueNorth.getDirectionRatios()!=null && trueNorth.getDirectionRatios().size() == 2)) return null;
		EList<Long> refLatitude = site.get(0).getRefLatitude();
		if (!(refLatitude != null && refLatitude.size() >= 3)) return null;
		EList<Long> refLongitude = site.get(0).getRefLongitude();
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

	private boolean checkGeodeticCRS(IfcModelInterface ifcModelInterface) {
		// TODO common classes for checking and querying
		List<IfcSite> site = ifcModelInterface.getAllWithSubTypes(IfcSite.class);
		List<IfcProject> project = ifcModelInterface.getAllWithSubTypes(IfcProject.class);
		if (!(project.size() == 1)) return false;
		if (!(site.size() == 1)) return false;
		if (!(project.get(0).getIsDecomposedBy().size() == 1)) return false;
		if (!project.get(0).getIsDecomposedBy().get(0).getRelatingObject().getGlobalId().equals(site.get(0).getGlobalId())) return false; // or even identity on entity level?
		IfcDirection trueNorth = ((IfcGeometricRepresentationContext) project.get(0).getRepresentationContexts().get(0)).getTrueNorth();
		if (!(trueNorth.getDim() == 2 && trueNorth.getDirectionRatios().size() == 2)) return false;
		EList<Long> refLatitude = site.get(0).getRefLatitude();
		if (!(refLatitude != null && refLatitude.size() >= 3)) return false;
		EList<Long> refLongitude = site.get(0).getRefLongitude();
		if (!(refLongitude != null && refLongitude.size() >= 3)) return false;
		return true;
	}

	private List<Corner> getPolygon(GeometryInfo geometry, double elevation) {
		if (geometry == null) return new ArrayList<>(); // TODO log warning or ignore these rooms/doors?
		// new IfcTools2D().get2D(space, 1); // only for IFC 2x3
		int[] indices = GeometryUtils.toIntegerArray(geometry.getData().getIndices().getData());
		double[] vertices = GeometryUtils.toDoubleArray(geometry.getData().getVertices().getData());
		double[] matrix = GeometryUtils.toDoubleArray(geometry.getTransformation());
		int multiplierMillimeters = 1; // TODO necessary? handle units?
		Area area = new Area();
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
		PathIterator pathIterator = area.getPathIterator(null);
		float[] coords = new float[6];
		List<Corner> corners = new ArrayList<>();
		int firstSegment = pathIterator.currentSegment(coords);
		assert !pathIterator.isDone() && firstSegment == PathIterator.SEG_MOVETO;
		corners.add(new Corner(coords[0], coords[1]));
		pathIterator.next();
		while (!pathIterator.isDone() && pathIterator.currentSegment(coords) == PathIterator.SEG_LINETO) {
			corners.add(new Corner(coords[0], coords[1]));
			pathIterator.next();
		}
		assert pathIterator.isDone() && pathIterator.currentSegment(coords) == PathIterator.SEG_CLOSE; // TODO warn multisegment path
		return corners;
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
