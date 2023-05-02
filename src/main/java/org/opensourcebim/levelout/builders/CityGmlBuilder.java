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
import org.xmlobjects.gml.model.feature.BoundingShape;
import org.xmlobjects.gml.model.geometry.Envelope;
import org.xmlobjects.gml.model.geometry.aggregates.MultiCurveProperty;
import org.xmlobjects.gml.model.geometry.aggregates.MultiSurfaceProperty;
import org.xmlobjects.gml.model.geometry.primitives.LineString;
import org.xmlobjects.gml.model.geometry.primitives.Polygon;
import org.xmlobjects.gml.util.id.DefaultIdCreator;
import org.xmlobjects.gml.util.id.IdCreator;

import net.opengis.gml.v_3_2.CartesianCSPropertyType;
import net.opengis.gml.v_3_2.CartesianCSType;
import net.opengis.gml.v_3_2.CoordinateSystemAxisPropertyType;
import net.opengis.gml.v_3_2.CoordinateSystemAxisType;
import net.opengis.gml.v_3_2.EngineeringCRSPropertyType;
import net.opengis.gml.v_3_2.EngineeringCRSType;
import net.opengis.gml.v_3_2.EngineeringDatumType;

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

	public LineString createCitygmlLines(Door door) {
		return geometryFactory.createLineString(door.asCoordinateList(), 3);
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
		for(Corner corner : room.getCorners()){
			envelope.include(corner.getX(), corner.getY());
		}
		return geometryFactory.createPolygon(room.asCoordinateList(), 3);
	}

	private Polygon createDegeneratedDoors(Door door) {
		return geometryFactory.createPolygon(door.asCoordinateList(), 3);
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
				Polygon poly = createCitygmlPoly(room); // to use for shell
				BuildingRoom cityGmlRoom = new BuildingRoom();
				List<AbstractSpaceBoundaryProperty> spaceBoundary = new ArrayList<>();
				spaceBoundary.add(createFloorSurface(poly));
				cityGmlRoom.setBoundaries(spaceBoundary);
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

	private org.citygml4j.core.model.building.Building createBuilding(Building building) throws ParserConfigurationException {
		org.citygml4j.core.model.building.Building cityGmlBuilding = new org.citygml4j.core.model.building.Building();
		// TODO only create groundsurface if building outline is present, check if this is the correct way to represent LOD0 building outline
		if(building.getCorners().size()>=3) {
			addGroundSurface(cityGmlBuilding, building.asCoordinateList());
		}

		for (Storey storey : building.getStoreys()) {
			org.citygml4j.core.model.building.Storey cityGmlStorey = new org.citygml4j.core.model.building.Storey();
			cityGmlStorey.setSortKey((double) storey.getLevel());
			AbstractBuildingSubdivisionProperty buildingSubdivision = new AbstractBuildingSubdivisionProperty(
					cityGmlStorey);
			cityGmlBuilding.getBuildingSubdivisions().add(buildingSubdivision);
			addRoomsAndDoors(storey, cityGmlStorey);
		}
		return cityGmlBuilding;
	}

	private void setEngineeringCRS(CoordinateReference crs) {

		EngineeringCRSPropertyType engineeringCRSPropertyType = new EngineeringCRSPropertyType();
		// engineeringCRSPropertyTypet

		EngineeringCRSType ec = new EngineeringCRSType();

		ec.setId("local-CRS-1");
		ec.withScope("CityGML");
		CartesianCSPropertyType cartesianCS = new CartesianCSPropertyType();
		CartesianCSType cs = new CartesianCSType();
		cs.setId("local-CS-1");

		List<String> axes = Arrays.asList("X", "Y", "Z");

		int i = 0;
		for (String a : axes) {
			CoordinateSystemAxisPropertyType csaxisProp = new CoordinateSystemAxisPropertyType();
			CoordinateSystemAxisType axis = new CoordinateSystemAxisType();
			axis.setId(a.toUpperCase());
			axis.getAxisAbbrev().setCodeSpace(a);
			axis.getAxisDirection().setCodeSpace("XYZ");
			axis.setUom("urn:ogc:def:uom:EPSG::9001");
			csaxisProp.setCoordinateSystemAxis(axis);
			cs.getAxis().get(i).getValue().setCoordinateSystemAxis(axis);
		}

		cartesianCS.setCartesianCS(cs);
		ec.getCartesianCS().setValue(cartesianCS);
		EngineeringDatumType engineeringDatum = new EngineeringDatumType();
		engineeringDatum.setId("local-datum-1");
		engineeringDatum.getAnchorDefinition().getValue().setCodeSpace("urn:ogc:def:crs,crs:EPSG::" + crs.toString());

		// TODO : set anchor coordinates

		ec.getEngineeringDatum().getValue().setEngineeringDatum(engineeringDatum);
		engineeringCRSPropertyType.setEngineeringCRS(ec);

	}

	private EngineeringCRSProperty createEngineeringCrs(CoordinateReference crs) throws ParserConfigurationException {
		GenericXmlBuilder builder = new GenericXmlBuilder();
		Node engCRS = builder.root("EngineeringCRS").attribute("id", "local-CS-1");
		engCRS.node("identifier").attribute("codeSpace", "XYZ").text("urn:ogc:def:crs:local:CRS:1");
		Node cartesianCRS = engCRS.node("cartesianCS").node("CartesianCS").attribute("id", "local-CRS-1");
		Node csAxis = cartesianCRS.node("axis").node("CoordinateSystemAxis").attribute("id", "local-axis-1").attribute("uom", "urn:ogc:def:uom:EPSG::9001");
		// sample https://github.com/opengeospatial/CityGML-3.0Encodings/blob/50af15ffc860f57ba29042844af7e8b40e960851/Moved_to_CITYGML-3.0Encoding_CityGML/Examples/Core/LocalCRS_CityGML3.gml

		EngineeringCRSProperty engineeringCRSProperty = new EngineeringCRSProperty();
		engineeringCRSProperty.setGenericElement(engCRS.generic());
		return engineeringCRSProperty;
	}

	public void write(OutputStream outStream, org.citygml4j.core.model.building.Building cityGmlBuilding) throws CityGMLWriteException, CityGMLContextException, ParserConfigurationException {
		write(outStream, cityGmlBuilding, null);
	}
	public void write(OutputStream outStream, org.citygml4j.core.model.building.Building cityGmlBuilding, EngineeringCRSProperty engineeringCRS)
			throws CityGMLContextException, CityGMLWriteException, ParserConfigurationException {
		CityGMLContext context = CityGMLContext.newInstance(cityGmlBuilding.getClass().getClassLoader());
		CityGMLVersion version = CityGMLVersion.v3_0;
		CityGMLOutputFactory outputFactory = context.createCityGMLOutputFactory(version);
		this.envelope.include(cityGmlBuilding.computeEnvelope());  // will only consider direct building boundaries, not rooms etc.
		try (CityGMLChunkWriter writer = outputFactory.createCityGMLChunkWriter(outStream,
				StandardCharsets.UTF_8.name())) {
			writer.withIndent("  ").withDefaultSchemaLocations().withDefaultPrefixes()
					.withDefaultNamespace(CoreModule.of(version).getNamespaceURI())
					.withHeaderComment("File created with citygml4j");
			//	writer.getCityModelInfo().setEngineeringCRS(ec);
			writer.getCityModelInfo().setBoundedBy(new BoundingShape(this.envelope));
			if(engineeringCRS!=null) writer.getCityModelInfo().setEngineeringCRS(engineeringCRS);
			writer.writeMember(cityGmlBuilding);
		}
	}


}
