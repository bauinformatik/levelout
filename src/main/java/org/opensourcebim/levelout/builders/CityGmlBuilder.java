package org.opensourcebim.levelout.builders;

import org.citygml4j.core.model.CityGMLVersion;
import org.citygml4j.core.model.building.AbstractBuildingSubdivision;
import org.citygml4j.core.model.building.AbstractBuildingSubdivisionProperty;
import org.citygml4j.core.model.building.BuildingConstructiveElement;
import org.citygml4j.core.model.building.BuildingConstructiveElementProperty;
import org.citygml4j.core.model.building.BuildingRoom;
import org.citygml4j.core.model.building.BuildingRoomProperty;
import org.citygml4j.core.model.construction.*;
import org.citygml4j.core.model.core.AbstractSpaceBoundaryProperty;
import org.citygml4j.core.model.core.AbstractThematicSurface;
import org.citygml4j.core.model.core.SpaceType;
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
import java.util.Arrays;
import java.util.List;

public class CityGmlBuilder {
	private final IdCreator idCreator = DefaultIdCreator.getInstance();
	private final GeometryFactory geometryFactory = GeometryFactory.newInstance().withIdCreator(idCreator);

	public LineString createCitygmlLines(Door door) {
		return geometryFactory.createLineString(door.asCoordinateList(), 3);
	}

	public AbstractSpaceBoundaryProperty createDoorSurface(LineString line) {
		return processBoundarySurface(new DoorSurface(), line);
	}

	public AbstractSpaceBoundaryProperty createWallSurface(LineString line) {
		return processBoundarySurface(new WallSurface(), line);
	}

	public AbstractSpaceBoundaryProperty createInteriorWallsurface(LineString line) {
		return processBoundarySurface(new InteriorWallSurface(), line);

	}

	private AbstractSpaceBoundaryProperty processBoundarySurface(AbstractThematicSurface thematicSurface,
			LineString line) {
		thematicSurface.setId(idCreator.createId());
		thematicSurface.setLod0MultiCurve(new MultiCurveProperty(geometryFactory.createMultiCurve(line)));
		return new AbstractSpaceBoundaryProperty(thematicSurface);
	}

	private Polygon createCitygmlPoly(Room room) {
		return geometryFactory.createPolygon(room.asCoordinateList(), 3);
	}

	private AbstractSpaceBoundaryProperty processBoundarySurface(AbstractThematicSurface thematicSurface,
			Polygon polygon) {
		thematicSurface.setId(idCreator.createId());
		thematicSurface.setLod0MultiSurface(new MultiSurfaceProperty(geometryFactory.createMultiSurface(polygon)));
		return new AbstractSpaceBoundaryProperty(thematicSurface);
	}

	private AbstractSpaceBoundaryProperty createFloorSurface(Polygon polygon) {
		return processBoundarySurface(new FloorSurface(), polygon);
	}

	private AbstractSpaceBoundaryProperty createGroundSurface(Polygon polygon) {
		return processBoundarySurface(new GroundSurface(), polygon);
	}

	private org.citygml4j.core.model.building.Building addBuildingboundary(
			org.citygml4j.core.model.building.Building cityGmlBuilding, List<Double> coordinates) {
		Polygon poly = geometryFactory.createPolygon(coordinates, 2);
		cityGmlBuilding.addBoundary(createGroundSurface(poly));
		return cityGmlBuilding;
	}

	private void setLodgeom(org.citygml4j.core.model.building.Building cityGmlBuilding, Storey storey,
			List<BuildingRoomProperty> buildingRooms,
			List<BuildingConstructiveElementProperty> buildingConstructiveElements,
			List<AbstractBuildingSubdivisionProperty> buildingSubdivisions,
			org.citygml4j.core.model.building.Storey cityGmlStorey) {
		List<Polygon> polygonList = new ArrayList<>();
		List<LineString> LineStringList = new ArrayList<>();

		for (Room room : storey.getRooms()) {
			if (room.getCorners().size() >= 3) {
				Polygon poly = createCitygmlPoly(room); // to use for shell
				polygonList.add(poly);
				BuildingRoom br = new BuildingRoom();
				List<AbstractSpaceBoundaryProperty> spaceBoundary = new ArrayList<>();
				BuildingRoomProperty brp = new BuildingRoomProperty();
				spaceBoundary.add(createFloorSurface(poly));
				br.setBoundaries(spaceBoundary);
				brp.setInlineObject(br);
				buildingRooms.add(brp);
			}

		}
		cityGmlStorey.setBuildingRooms(buildingRooms);

		for (Door door : storey.getDoors()) {
			if (door.getCorners().size() >= 2) {
				LineString line = createCitygmlLines(door); // to use for shell
				LineStringList.add(line);
				org.citygml4j.core.model.construction.Door doors = new org.citygml4j.core.model.construction.Door();
				List<AbstractSpaceBoundaryProperty> doorBoundaries = new ArrayList<>();
				doorBoundaries.add(createDoorSurface(line));
				doors.setBoundaries(doorBoundaries);
				BuildingConstructiveElementProperty buildingconselementProp = new BuildingConstructiveElementProperty();
				BuildingConstructiveElement buildingconsElement = new BuildingConstructiveElement();
				List<AbstractFillingElementProperty> fillings = new ArrayList<>();
				AbstractFillingElementProperty absfillingElement = new AbstractFillingElementProperty();
				absfillingElement.setInlineObject(doors);
				fillings.add(absfillingElement);
				buildingconsElement.setFillings(fillings);
				buildingconselementProp.setInlineObject(buildingconsElement);
				buildingConstructiveElements.add(buildingconselementProp);
			}
		}
		cityGmlStorey.setBuildingConstructiveElements(buildingConstructiveElements);
		//
		buildingSubdivisions.add(new AbstractBuildingSubdivisionProperty(cityGmlStorey));
		cityGmlBuilding.setBuildingSubdivisions(buildingSubdivisions);

		List<SurfaceProperty> surfaceMember = new ArrayList<>();
		for (Polygon polygon : polygonList) {
			surfaceMember.add(new SurfaceProperty("#" + polygon.getId()));
		}
		// cityGmlBuilding.setLod0MultiSurface(new MultiSurfaceProperty(new
		// MultiSurface(surfaceMember)));

		List<CurveProperty> curveMember = new ArrayList<>();
		for (LineString line : LineStringList) {
			curveMember.add(new CurveProperty("#" + line.getId()));
		}
		// cityGmlBuilding.setLod0MultiCurve(new MultiCurveProperty(new
		// MultiCurve(curveMember)));*/
	}

	public void createAndWriteBuilding(Building building, OutputStream outStream)
			throws CityGMLContextException, CityGMLWriteException {
		write(outStream, createBuilding(building));
	}

	private org.citygml4j.core.model.building.Building createBuilding(Building building) {
		org.citygml4j.core.model.building.Building cityGmlBuilding = new org.citygml4j.core.model.building.Building();
		List<AbstractBuildingSubdivisionProperty> buildingSubdivisions = new ArrayList<>();
		addBuildingboundary(cityGmlBuilding, Arrays.asList(-0.1, 0.0, 10.0, -0.1, 10.0, 6.0, -0.1, 6.0, -0.1, 0.0));
		for (Storey storey : building.getStoreys()) {
			List<BuildingRoomProperty> buildingRooms = new ArrayList<>();
			List<BuildingConstructiveElementProperty> buildingConstructiveElements = new ArrayList<>();
			org.citygml4j.core.model.building.Storey cityGmlStorey = new org.citygml4j.core.model.building.Storey();
			cityGmlStorey.setSortKey((double) storey.getLevel());
			setLodgeom(cityGmlBuilding, storey, buildingRooms, buildingConstructiveElements, buildingSubdivisions,
					cityGmlStorey);
		}

		return cityGmlBuilding;
	}

	public void write(OutputStream outStream, org.citygml4j.core.model.building.Building cityGmlBuilding)
			throws CityGMLContextException, CityGMLWriteException {
		CityGMLContext context = CityGMLContext.newInstance(cityGmlBuilding.getClass().getClassLoader());
		CityGMLVersion version = CityGMLVersion.v3_0;
		CityGMLOutputFactory outputFactory = context.createCityGMLOutputFactory(version);
		Envelope envelope = cityGmlBuilding.computeEnvelope();
		try (CityGMLChunkWriter writer = outputFactory.createCityGMLChunkWriter(outStream,
				StandardCharsets.UTF_8.name())) {
			writer.withIndent("  ").withDefaultSchemaLocations().withDefaultPrefixes()
					.withDefaultNamespace(CoreModule.of(version).getNamespaceURI())
					.withHeaderComment("File created with citygml4j");
			writer.getCityModelInfo().setBoundedBy(new BoundingShape(envelope));
			writer.writeMember(cityGmlBuilding);
		}
	}

}
