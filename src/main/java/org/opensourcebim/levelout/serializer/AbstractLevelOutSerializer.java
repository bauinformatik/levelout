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
import org.eclipse.emf.common.util.EList;
import org.opensourcebim.levelout.intermediatemodel.*;
import org.opensourcebim.levelout.intermediatemodel.geo.CoordinateReference;
import org.opensourcebim.levelout.intermediatemodel.geo.GeodeticOriginCRS;
import org.opensourcebim.levelout.intermediatemodel.geo.GeodeticPoint;
import org.opensourcebim.levelout.samples.IntermediateResidential;

import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.io.OutputStream;
import java.util.*;

public abstract class AbstractLevelOutSerializer implements Serializer {
	boolean extractionMethod;
	Building building;
	CoordinateReference crs;

	AbstractLevelOutSerializer(boolean extractionMethod) {
		this.extractionMethod = extractionMethod;
	}

	@Override
	public void init(IfcModelInterface ifcModelInterface, ProjectInfo projectInfo, boolean b) {
		if (extractionMethod) {
			initStructure(ifcModelInterface);
		} else initSample();
	}


	private void initSample() {
		building = IntermediateResidential.create();
		crs = new GeodeticOriginCRS(new GeodeticPoint(53.320555, -1.729000, 0), -0.13918031137);
	}

	private void initStructure(IfcModelInterface ifcModelInterface) {
		List<Storey> loStoreys = new ArrayList<>();
		building = new Building(1, loStoreys);
		List<IfcBuildingStorey> storeys = ifcModelInterface.getAllWithSubTypes(IfcBuildingStorey.class);
		int level = 0; // TODO sort by elevation, 0 is closest elevation to 0, from there increment up and down
		int roomId = 1;
		for (IfcBuildingStorey storey : storeys) {
			List<Room> rooms = new ArrayList<>();
			List<Door> doors = new ArrayList<>();
			Storey loStorey = new Storey(level++, rooms, doors);
			loStoreys.add(loStorey);
			double elevation = storey.getElevation(); // TODO: use for sorting and in intermediate model
			storey.getName(); /* TODO: use in intermediate model (OSM has "name" tag, but also "level:ref" for short keys) */
			Map<IfcSpace, Room> roomsMap = new HashMap<>();
			for (IfcRelAggregates contained : storey.getIsDecomposedBy()) {
				for (IfcSpace space : contained.getRelatedObjects().stream().filter(IfcSpace.class::isInstance).map(IfcSpace.class::cast).toArray(IfcSpace[]::new)) {
					Room room = new Room(roomId++, "floor", getPolygon(space.getGeometry(), elevation));
					rooms.add(room);
					roomsMap.put(space, room); // later needed for assignment to doors
					space.getName(); // TODO: use in intermediate model
				}
			}
			for (IfcRelContainedInSpatialStructure contained : storey.getContainsElements()) {
				for (IfcOpeningElement opening : contained.getRelatedElements().stream().filter(IfcOpeningElement.class::isInstance).map(IfcOpeningElement.class::cast).toArray(IfcOpeningElement[]::new)) {
					if(opening.getHasFillings().isEmpty() || ! (opening.getHasFillings().get(0).getRelatedBuildingElement() instanceof IfcDoor)) continue; // TODO windows
					Door door = new Door(0, "dummy", getPolygon(opening.getGeometry(), elevation));
					doors.add(door);
					EList<IfcRelSpaceBoundary> openingBoundaries = opening.getProvidesBoundaries();
					if (populateConnectedRooms(roomsMap, door, openingBoundaries)) continue;

					// if no space boundary for opening, try fillings
					EList<IfcRelFillsElement> hasFillings = opening.getHasFillings();
					if(hasFillings.isEmpty() || hasFillings.get(0).getRelatedBuildingElement().getProvidesBoundaries().isEmpty()) {
						// TODO warning no space boundaries
					}
					if(hasFillings.size()>1){
						// TODO warning, considering just first filling or check all have the same boundaries
					}
					EList<IfcRelSpaceBoundary> fillingBoundaries = hasFillings.get(0).getRelatedBuildingElement().getProvidesBoundaries();
					if(populateConnectedRooms(roomsMap, door, fillingBoundaries)) continue;

					// fallback - only works for 2nd level space boundaries
					List<IfcRelSpaceBoundary> processed = new ArrayList<>();
					for (IfcRelSpaceBoundary spaceBoundary : opening.getVoidsElements().getRelatingBuildingElement().getProvidesBoundaries()) {
						// TODO these could be more than 2 and affect wall pieces that do not host the particular door
						if(spaceBoundary instanceof IfcRelSpaceBoundary2ndLevel && !processed.contains(spaceBoundary)) {
							processed.add(spaceBoundary);
							IfcRelSpaceBoundary otherSpaceBoundary = ((IfcRelSpaceBoundary2ndLevel)spaceBoundary).getCorrespondingBoundary();
							if(otherSpaceBoundary!= null) {
								processed.add(otherSpaceBoundary);
								populateConnectedRooms(roomsMap, door, List.of(spaceBoundary, otherSpaceBoundary));
								break;
							}
						}
					}
				}
			}
		}
		GeodeticOriginCRS geoCRS = getGeodeticCRS(ifcModelInterface);  // TODO check for map conversion (first, before geo)
		crs = geoCRS != null ? geoCRS : new GeodeticOriginCRS(new GeodeticPoint(0, 0, 0), 0); // TODO use some default location in Weimar, Dresden ..
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

	private GeodeticOriginCRS getGeodeticCRS(IfcModelInterface ifcModelInterface) {
		// TODO common classes for checking and querying
		List<IfcSite> site = ifcModelInterface.getAllWithSubTypes(IfcSite.class);
		List<IfcProject> project = ifcModelInterface.getAllWithSubTypes(IfcProject.class);
		if (!(project.size() == 1)) return null;
		if (!(site.size() == 1)) return null;
		if (!(project.get(0).getIsDecomposedBy().size() == 1)) return null;
		if (!project.get(0).getIsDecomposedBy().get(0).getRelatingObject().getGlobalId().equals(site.get(0).getGlobalId()))
			return null; // or even identity on entity level?
		IfcDirection trueNorth = ((IfcGeometricRepresentationContext) project.get(0).getRepresentationContexts().get(0)).getTrueNorth();
		if (!(trueNorth.getDim() == 2 && trueNorth.getDirectionRatios().size() == 2)) return null;
		EList<Long> refLatitude = site.get(0).getRefLatitude();
		if (!(refLatitude != null && refLatitude.size() >= 3)) return null;
		EList<Long> refLongitude = site.get(0).getRefLongitude();
		if (!(refLongitude != null && refLongitude.size() >= 3)) return null;
		double rotation = Math.atan2(trueNorth.getDirectionRatios().get(1), trueNorth.getDirectionRatios().get(0));
		double latitude = degreesFromMinutes(refLatitude);
		double longitude = degreesFromMinutes(refLongitude);
		return new GeodeticOriginCRS(new GeodeticPoint(latitude, longitude, 0), rotation);
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
		if (geometry == null) return null;
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
		corners.add(new Corner(0, coords[0], coords[1], elevation));
		pathIterator.next();
		while (!pathIterator.isDone() && pathIterator.currentSegment(coords) == PathIterator.SEG_LINETO) {
			corners.add(new Corner(0, coords[0], coords[1], elevation));
			pathIterator.next();
		}
		assert pathIterator.isDone() && pathIterator.currentSegment(coords) == PathIterator.SEG_CLOSE; // TODO warn multisegment path
		return corners;
	}

	private static double degreesFromMinutes(EList<Long> refLatitude) {
		return refLatitude.get(0) + refLatitude.get(1) / 60. + refLatitude.get(2) / 3600.; // TODO consider optional microseconds
	}

    @Override
    public boolean write(OutputStream outputStream) throws SerializerException, BimserverDatabaseException {
        this.writeToOutputStream(outputStream, null);
        return true;
    }
}
