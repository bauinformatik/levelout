package org.opensourcebim.levelout.builders;

import org.citygml4j.core.model.CityGMLVersion;
import org.citygml4j.core.model.building.AbstractBuildingSubdivisionProperty;
import org.citygml4j.core.model.building.BuildingConstructiveElement;
import org.citygml4j.core.model.building.BuildingConstructiveElementProperty;
import org.citygml4j.core.model.building.BuildingRoom;
import org.citygml4j.core.model.building.BuildingRoomProperty;
import org.citygml4j.core.model.construction.*;
import org.citygml4j.core.model.core.AbstractSpaceBoundaryProperty;
import org.citygml4j.core.model.core.AbstractThematicSurface;
import org.citygml4j.core.model.core.EngineeringCRSProperty;
import org.citygml4j.core.util.geometry.GeometryFactory;
import org.citygml4j.xml.CityGMLContext;
import org.citygml4j.xml.CityGMLContextException;
import org.citygml4j.xml.module.citygml.CoreModule;
import org.citygml4j.xml.writer.CityGMLChunkWriter;
import org.citygml4j.xml.writer.CityGMLOutputFactory;
import org.citygml4j.xml.writer.CityGMLWriteException;
import org.opensourcebim.levelout.builders.GenericXmlBuilder.Node;
import org.opensourcebim.levelout.intermediatemodel.*;
import org.opensourcebim.levelout.intermediatemodel.Door;
import org.opensourcebim.levelout.intermediatemodel.geo.CoordinateReference;
import org.xmlobjects.gml.model.basictypes.Code;
import org.xmlobjects.gml.model.feature.BoundingShape;
import org.xmlobjects.gml.model.geometry.Envelope;
import org.xmlobjects.gml.model.geometry.aggregates.MultiCurveProperty;
import org.xmlobjects.gml.model.geometry.aggregates.MultiSurfaceProperty;
import org.xmlobjects.gml.model.geometry.primitives.LineString;
import org.xmlobjects.gml.model.geometry.primitives.Polygon;
import org.xmlobjects.gml.util.id.DefaultIdCreator;
import org.xmlobjects.gml.util.id.IdCreator;

import net.opengis.gml.v_3_2.CodeType;

import javax.xml.parsers.ParserConfigurationException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CityGmlBuilder {
	private final IdCreator idCreator = DefaultIdCreator.getInstance();
	private final GeometryFactory geometryFactory = GeometryFactory.newInstance().withIdCreator(idCreator);
	private final Envelope envelope = new Envelope();
	private CoordinateReference crs;

	public LineString createCitygmlLines(Door door) {
		return geometryFactory.createLineString(door.asCoordinateList(crs), 3);
	}

	public AbstractSpaceBoundaryProperty createDoorSurface(LineString line) {
		return processBoundarySurface(new DoorSurface(), line);
	}

	private AbstractSpaceBoundaryProperty createDoorSurface(Polygon poly) {
		return processBoundarySurface(new DoorSurface(), poly);
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
		for (Corner corner : room.getCorners()) {
			envelope.include(corner.getX(), corner.getY());
		}
		return geometryFactory.createPolygon(room.asCoordinateList(crs), 3);
	}

	private Polygon createDegeneratedDoors(Door door) {
		return geometryFactory.createPolygon(door.asCoordinateList(crs), 3);
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

	private void addGroundSurface(org.citygml4j.core.model.building.Building cityGmlBuilding,
			List<Double> coordinates) {
		Polygon poly = geometryFactory.createPolygon(coordinates, 3);
		cityGmlBuilding.addBoundary(createGroundSurface(poly));
	}

	private void addRoomsAndDoors(Storey storey, org.citygml4j.core.model.building.Storey cityGmlStorey) {
		for (Room room : storey.getRooms()) {
			if (room.getCorners().size() >= 3) {
				Polygon poly = createCitygmlPoly(room);
				// to use for shell
				BuildingRoom cityGmlRoom = new BuildingRoom();
				List<AbstractSpaceBoundaryProperty> spaceBoundary = new ArrayList<>();
				spaceBoundary.add(createFloorSurface(poly));
				cityGmlRoom.setBoundaries(spaceBoundary);
				cityGmlRoom.setNames(Arrays.asList(new Code(room.getName())));
				BuildingRoomProperty roomProperty = new BuildingRoomProperty(cityGmlRoom);
				cityGmlStorey.getBuildingRooms().add(roomProperty);
			}
		}
		for (Door door : storey.getDoors()) {
			if (door.getCorners().size() >= 3) {
				Polygon poly = createDegeneratedDoors(door);
				org.citygml4j.core.model.construction.Door doors = new org.citygml4j.core.model.construction.Door();
				List<AbstractSpaceBoundaryProperty> doorBoundaries = new ArrayList<>();
				doorBoundaries.add(createDoorSurface(poly));
				doors.setBoundaries(doorBoundaries);
				doors.setNames(Arrays.asList(new Code(door.getName())));
				BuildingConstructiveElement buildingconsElement = new BuildingConstructiveElement();
				buildingconsElement.getFillings().add(new AbstractFillingElementProperty(doors));
				BuildingConstructiveElementProperty constructiveElement = new BuildingConstructiveElementProperty(
						buildingconsElement);
				cityGmlStorey.getBuildingConstructiveElements().add(constructiveElement);
			}
		}

	}

	public void createAndWriteBuilding(Building building, OutputStream outStream)
			throws CityGMLContextException, CityGMLWriteException, ParserConfigurationException {
		;
		write(outStream, createBuilding(building), createEngineeringCrs(building.getCrs()));
	}

	private org.citygml4j.core.model.building.Building createBuilding(Building building)
			throws ParserConfigurationException {
		org.citygml4j.core.model.building.Building cityGmlBuilding = new org.citygml4j.core.model.building.Building();
		this.crs = building.getCrs();
		// TODO only create groundsurface if building outline is present, check if this
		// is the correct way to represent LOD0 building outline
		if (building.getCorners().size() >= 3) {
			addGroundSurface(cityGmlBuilding, building.asCoordinateList(crs));
		}

		for (Storey storey : building.getStoreys()) {
			org.citygml4j.core.model.building.Storey cityGmlStorey = new org.citygml4j.core.model.building.Storey();
			cityGmlStorey.setSortKey((double) storey.getLevel());
			cityGmlStorey.setNames(Arrays.asList(new Code(storey.getName())));
			AbstractBuildingSubdivisionProperty buildingSubdivision = new AbstractBuildingSubdivisionProperty(
					cityGmlStorey);
			cityGmlBuilding.getBuildingSubdivisions().add(buildingSubdivision);
			addRoomsAndDoors(storey, cityGmlStorey);
		}
		return cityGmlBuilding;
	}

	private EngineeringCRSProperty createEngineeringCrs(CoordinateReference crs) throws ParserConfigurationException {
		GenericXmlBuilder builder = new GenericXmlBuilder();
		Node engCRS = builder.root("EngineeringCRS").attribute("id", "local-CRS-1");
		engCRS.node("description").text("engineering");
		engCRS.node("identifier").attribute("codeSpace", "XYZ").text("urn:ogc:def:crs:local:CRS:1");
		engCRS.node("scope").text("CityGML");
		Node cartesianCRS = engCRS.node("cartesianCS").node("CartesianCS").attribute("id", "local-CS-1");
		cartesianCRS.node("description").text("Cartesian 3D CS. Axes: 3 UoM: m.");
		cartesianCRS.node("identifier").attribute("codeSpace", "XYZ").text("urn:ogc:def:crs:local:CS:1");

		List<String> axes = Arrays.asList("X", "Y", "Z");

		int i = 1;
		for (String a : axes) {

			Node csAxis = cartesianCRS.node("axis").node("CoordinateSystemAxis")
					.attribute("id", "local-axis-" + Integer.toString(i))
					.attribute("uom", "urn:ogc:def:uom:EPSG::9001");
			csAxis.node("identifier").attribute("codeSpace", "XYZ").text(a);
			csAxis.node("axisAbbrev").text(a.toLowerCase());
			csAxis.node("axisDirection").attribute("codeSpace", "XYZ").text(a);
			i++;
		}

		Node engineeringDatum = engCRS.node("engineeringDatum").node("EngineeringDatum").attribute("id",
				"local-datum-1");
		engineeringDatum.node("description").text("Cartesian datum");
		engineeringDatum.node("identifier").attribute("codeSpace", "XYZ").text("Datum1");
		engineeringDatum.node("scope").text("CityGML");
		engineeringDatum.node("anchorDefinition").attribute("codeSpace", "urn:ogc:def:crs,crs:EPSG::" + crs.getEpsg())
				.text(crs.getOriginX() + " " + crs.getOriginY() + " ");

		// sample
		// https://github.com/opengeospatial/CityGML-3.0Encodings/blob/50af15ffc860f57ba29042844af7e8b40e960851/Moved_to_CITYGML-3.0Encoding_CityGML/Examples/Core/LocalCRS_CityGML3.gml

		EngineeringCRSProperty engineeringCRSProperty = new EngineeringCRSProperty();
		engineeringCRSProperty.setGenericElement(engCRS.generic());
		return engineeringCRSProperty;
	}

	public void write(OutputStream outStream, org.citygml4j.core.model.building.Building cityGmlBuilding)
			throws CityGMLWriteException, CityGMLContextException, ParserConfigurationException {
		write(outStream, cityGmlBuilding, null);
	}

	public void write(OutputStream outStream, org.citygml4j.core.model.building.Building cityGmlBuilding,
			EngineeringCRSProperty engineeringCRS)
			throws CityGMLContextException, CityGMLWriteException, ParserConfigurationException {
		CityGMLContext context = CityGMLContext.newInstance(cityGmlBuilding.getClass().getClassLoader());
		CityGMLVersion version = CityGMLVersion.v3_0;
		CityGMLOutputFactory outputFactory = context.createCityGMLOutputFactory(version);
		this.envelope.include(cityGmlBuilding.computeEnvelope()); // will only consider direct building boundaries, not
																	// rooms etc.
		try (CityGMLChunkWriter writer = outputFactory.createCityGMLChunkWriter(outStream,
				StandardCharsets.UTF_8.name())) {
			writer.withIndent("  ").withDefaultSchemaLocations().withDefaultPrefixes()
					.withDefaultNamespace(CoreModule.of(version).getNamespaceURI())
					.withHeaderComment("File created with citygml4j");

			// writer.getCityModelInfo().setEngineeringCRS(ec);
			if (engineeringCRS != null)
				writer.getCityModelInfo().setEngineeringCRS(engineeringCRS);
			writer.getCityModelInfo().setBoundedBy(new BoundingShape(this.envelope));
			writer.writeMember(cityGmlBuilding);
		}
	}

}
