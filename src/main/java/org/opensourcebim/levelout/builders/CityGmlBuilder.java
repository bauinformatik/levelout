package org.opensourcebim.levelout.builders;

import org.citygml4j.core.model.CityGMLVersion;
import org.citygml4j.core.model.building.AbstractBuildingSubdivisionProperty;
import org.citygml4j.core.model.building.BuildingRoom;
import org.citygml4j.core.model.building.BuildingRoomProperty;
import org.citygml4j.core.model.construction.*;
import org.citygml4j.core.model.core.AbstractSpaceBoundaryProperty;
import org.citygml4j.core.model.core.AbstractThematicSurface;
import org.citygml4j.core.util.geometry.GeometryFactory;
import org.citygml4j.xml.CityGMLContext;
import org.citygml4j.xml.CityGMLContextException;
import org.citygml4j.xml.module.citygml.CoreModule;
import org.citygml4j.xml.writer.CityGMLChunkWriter;
import org.citygml4j.xml.writer.CityGMLOutputFactory;
import org.citygml4j.xml.writer.CityGMLWriteException;
import org.opensourcebim.levelout.intermediatemodel.*;
import org.opensourcebim.levelout.intermediatemodel.Door;
import org.xmlobjects.gml.model.deprecated.StringOrRef;
import org.xmlobjects.gml.model.feature.BoundingShape;
import org.xmlobjects.gml.model.geometry.Envelope;
import org.xmlobjects.gml.model.geometry.aggregates.MultiCurve;
import org.xmlobjects.gml.model.geometry.aggregates.MultiCurveProperty;
import org.xmlobjects.gml.model.geometry.aggregates.MultiSurface;
import org.xmlobjects.gml.model.geometry.aggregates.MultiSurfaceProperty;
import org.xmlobjects.gml.model.geometry.primitives.CurveProperty;
import org.xmlobjects.gml.model.geometry.primitives.LineString;
import org.xmlobjects.gml.model.geometry.primitives.Polygon;
import org.xmlobjects.gml.model.geometry.primitives.SurfaceProperty;
import org.xmlobjects.gml.util.id.DefaultIdCreator;
import org.xmlobjects.gml.util.id.IdCreator;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CityGmlBuilder {
	private final IdCreator idCreator = DefaultIdCreator.getInstance();
	private final GeometryFactory geometryFactory = GeometryFactory.newInstance().withIdCreator(idCreator);

	public LineString createCitygmlLines(Door door) {
		List<Double> coordinates = new ArrayList<>();
		for (Corner corner : door.getCorners()) {
			coordinates.addAll(corner.asCoordinateList());
		}
		return geometryFactory.createLineString(coordinates, 3);
	}

	public AbstractSpaceBoundaryProperty createBoundaryLine(Door door, LineString line) {
		if (door.getType().contains("wall")) {
			return processBoundarySurface(new WallSurface(), line);
		} else if (door.getType().contains("door")) {
			return processBoundarySurface(new DoorSurface(), line);
		} else return null;  // TODO: not nice
	}

	private AbstractSpaceBoundaryProperty processBoundarySurface(AbstractThematicSurface thematicSurface,
																		LineString line) {
		thematicSurface.setId(idCreator.createId());
		thematicSurface.setLod0MultiCurve(new MultiCurveProperty(geometryFactory.createMultiCurve(line)));
		return new AbstractSpaceBoundaryProperty(thematicSurface);
}

	private Polygon createCitygmlPoly(Room room) {
		List<Double> coordinates = new ArrayList<>();
		for (Corner corner : room.getCorners()) {
			coordinates.addAll(corner.asCoordinateList());
		}
		return geometryFactory.createPolygon(coordinates, 3);
	}

	private AbstractSpaceBoundaryProperty processBoundarySurface(AbstractThematicSurface thematicSurface,
																	   Polygon polygon) {
		thematicSurface.setId(idCreator.createId());
		thematicSurface.setLod0MultiSurface(new MultiSurfaceProperty(geometryFactory.createMultiSurface(polygon)));
		return new AbstractSpaceBoundaryProperty(thematicSurface);
	}

	private AbstractSpaceBoundaryProperty createBoundary(Room room, Polygon polygon) {
		if (room.getType().contains("ground")) {
			return processBoundarySurface(new GroundSurface(), polygon);
		} else if (room.getType().contains("wall")) {
			return processBoundarySurface(new WallSurface(), polygon);
		} else if (room.getType().contains("roof")) {
			return processBoundarySurface(new RoofSurface(), polygon);
		} else if (room.getType().contains("ceiling")) {
			return processBoundarySurface(new CeilingSurface(), polygon);
		} else if (room.getType().contains("door")) {
				return processBoundarySurface(new DoorSurface(), polygon);
		} else if (room.getType().contains("floor")) {
			return processBoundarySurface(new FloorSurface(), polygon);
		} else return null;  // TODO: not nice
	}

	private void setLodgeom(org.citygml4j.core.model.building.Building cityGmlBuilding, Storey storey) {
		List<Polygon> polygonList = new ArrayList<>();
		List<LineString> LineStringList = new ArrayList<>();
		for (Room genericPolygon : storey.getRooms()) {
			if(genericPolygon.getCorners().size()>=3){
				Polygon poly = createCitygmlPoly(genericPolygon); // to use for shell
				polygonList.add(poly);
				cityGmlBuilding.addBoundary(createBoundary(genericPolygon, poly));
			}
		}
		for (Door genericPolygon : storey.getDoors()) {
			if(genericPolygon.getCorners().size()>=2){
				LineString line = createCitygmlLines(genericPolygon); // to use for shell
				LineStringList.add(line);
				cityGmlBuilding.addBoundary(createBoundaryLine(genericPolygon, line));
			}
		}

		List<SurfaceProperty> surfaceMember = new ArrayList<>();
		for (Polygon polygon : polygonList) {
			surfaceMember.add(new SurfaceProperty("#" + polygon.getId()));
		}
		cityGmlBuilding.setLod0MultiSurface(new MultiSurfaceProperty(new MultiSurface(surfaceMember)));

		List<CurveProperty> curveMember = new ArrayList<>();
		for (LineString line : LineStringList) {
			curveMember.add(new CurveProperty("#" + line.getId()));
		}
		cityGmlBuilding.setLod0MultiCurve(new MultiCurveProperty(new MultiCurve(curveMember)));
	}

	public void createAndWriteBuilding(Building building, OutputStream outStream) throws CityGMLContextException, CityGMLWriteException {
		write(outStream, createBuilding(building));
	}

	private org.citygml4j.core.model.building.Building createBuilding(Building building) {
		org.citygml4j.core.model.building.Building cityGmlBuilding = new org.citygml4j.core.model.building.Building();
		for (Storey storeys : building.getStoreys()) {
			setLodgeom(cityGmlBuilding, storeys);
			org.citygml4j.core.model.building.Storey cityGmlStorey = new org.citygml4j.core.model.building.Storey();
			cityGmlStorey.setSortKey((double) storeys.getLevel());
			cityGmlBuilding.getBuildingSubdivisions().add(new AbstractBuildingSubdivisionProperty(cityGmlStorey));
			for (Room room: storeys.getRooms()){
				BuildingRoom cityGmlRoom = new BuildingRoom();
				cityGmlRoom.setDescription(new StringOrRef("Room #" + room.getId()));
				cityGmlStorey.getBuildingRooms().add(new BuildingRoomProperty(cityGmlRoom));
			}
		}
		return cityGmlBuilding;
	}

	public void write(OutputStream outStream, org.citygml4j.core.model.building.Building cityGmlBuilding) throws CityGMLContextException, CityGMLWriteException {
		CityGMLContext context = CityGMLContext.newInstance(cityGmlBuilding.getClass().getClassLoader());
		CityGMLVersion version = CityGMLVersion.v3_0;
		CityGMLOutputFactory outputFactory = context.createCityGMLOutputFactory(version);
		Envelope envelope = cityGmlBuilding.computeEnvelope();
		try (CityGMLChunkWriter writer = outputFactory.createCityGMLChunkWriter(outStream, StandardCharsets.UTF_8.name())) {
			writer.withIndent("  ").withDefaultSchemaLocations().withDefaultPrefixes().withDefaultNamespace(CoreModule.of(version).getNamespaceURI()).withHeaderComment("File created with citygml4j");
			writer.getCityModelInfo().setBoundedBy(new BoundingShape(envelope));
			writer.writeMember(cityGmlBuilding);
		}
	}


}
