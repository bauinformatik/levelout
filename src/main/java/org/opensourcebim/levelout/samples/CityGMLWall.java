
package org.opensourcebim.levelout.samples;

import org.citygml4j.core.model.CityGMLVersion;
import org.citygml4j.core.model.building.AbstractBuildingSubdivisionProperty;
import org.citygml4j.core.model.building.Building;
import org.citygml4j.core.model.building.BuildingConstructiveElement;
import org.citygml4j.core.model.building.BuildingConstructiveElementProperty;
import org.citygml4j.core.model.building.BuildingRoom;
import org.citygml4j.core.model.building.BuildingRoomProperty;
import org.citygml4j.core.model.building.Storey;
import org.citygml4j.core.model.construction.*;
import org.citygml4j.core.model.core.AbstractSpaceBoundaryProperty;
import org.citygml4j.core.model.core.AbstractThematicSurface;
import org.citygml4j.core.util.geometry.GeometryFactory;
import org.citygml4j.xml.CityGMLContext;
import org.citygml4j.xml.module.citygml.CoreModule;
import org.citygml4j.xml.writer.CityGMLChunkWriter;
import org.citygml4j.xml.writer.CityGMLOutputFactory;
import org.opensourcebim.levelout.intermediatemodel.Corner;
import org.opensourcebim.levelout.intermediatemodel.Door;
import org.xmlobjects.gml.model.feature.BoundingShape;
import org.xmlobjects.gml.model.geometry.Envelope;
import org.xmlobjects.gml.model.geometry.aggregates.MultiCurve;
import org.xmlobjects.gml.model.geometry.aggregates.MultiCurveProperty;
import org.xmlobjects.gml.model.geometry.aggregates.MultiSurfaceProperty;
import org.xmlobjects.gml.model.geometry.primitives.*;
import org.xmlobjects.gml.util.id.DefaultIdCreator;
import org.xmlobjects.gml.util.id.IdCreator;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.opensourcebim.levelout.builders.CityGmlBuilder;
import org.opensourcebim.levelout.builders.IndoorGmlBuilder;

public class CityGMLWall {

	private IdCreator id;
	private GeometryFactory geom;

	public static void main(String[] args) throws Exception {
		new CityGMLWall().doMain();
	}

	public void doMain() throws Exception {
		String fileName = "output/roomstorey8.gml";

		CityGMLContext context = CityGMLContext.newInstance();

		id = DefaultIdCreator.getInstance();
		geom = GeometryFactory.newInstance().withIdCreator(id);

		Building building = new Building();
		Storey storey = new Storey();
		CityGmlBuilder cityGmlBuilder = new CityGmlBuilder();

		List<BuildingRoomProperty> buildingRooms = new ArrayList<>();
		List<AbstractBuildingSubdivisionProperty> buildingSubdivisions = new ArrayList<>();

		List<Double> wall1 = Arrays.asList(0.0, 0.0, 0.0, 6.0, 0.0, 0.0);
		List<Double> wall2 = Arrays.asList(6.0, 0.0, 0.0, 6.0, 6.0, 0.0);
		List<Double> wall3 = Arrays.asList(6.0, 6.0, 0.0, 0.0, 6.0, 0.0);
		List<Double> wall4 = Arrays.asList(0.0, 6.0, 0.0, 0.0, 0.0, 0.0);

		LineString l1 = geom.createLineString(wall1, 3);
		LineString l2 = geom.createLineString(wall2, 3);
		LineString l3 = geom.createLineString(wall3, 3);
		LineString l4 = geom.createLineString(wall4, 3);

		List<Double> door1 = Arrays.asList(1.0, 0.0, 0.0, 2.0, 0.0, 0.0);
		List<Double> door2 = Arrays.asList(6.0, 5.0, 0.0, 6.0, 6.0, 0.0);
		LineString l5 = geom.createLineString(door1, 3);
		LineString l6 = geom.createLineString(door2, 3);

		List<BuildingConstructiveElementProperty> buildingConstructiveElements = new ArrayList<>();
		BuildingConstructiveElement buildingconsElement = new BuildingConstructiveElement();

		List<AbstractSpaceBoundaryProperty> boundaries = Arrays.asList(cityGmlBuilder.createInteriorWallsurface(l1),
				cityGmlBuilder.createInteriorWallsurface(l2), cityGmlBuilder.createInteriorWallsurface(l3),
				cityGmlBuilder.createInteriorWallsurface(l4));

		buildingconsElement.setBoundaries(boundaries);

		List<AbstractSpaceBoundaryProperty> doorBoundaries = Arrays.asList(cityGmlBuilder.createDoorSurface(l5),
				cityGmlBuilder.createDoorSurface(l6));

		org.citygml4j.core.model.construction.Door doors = new org.citygml4j.core.model.construction.Door();
		doors.setBoundaries(doorBoundaries);

		buildingconsElement.getFillings().add(new AbstractFillingElementProperty(doors));

		buildingConstructiveElements.add(new BuildingConstructiveElementProperty(buildingconsElement));
		storey.setBuildingConstructiveElements(buildingConstructiveElements);

		storey.setBuildingRooms(buildingRooms);
		buildingSubdivisions.add(new AbstractBuildingSubdivisionProperty(storey));
		building.setBuildingSubdivisions(buildingSubdivisions);

		Envelope envelope = building.computeEnvelope();
		CityGMLVersion version = CityGMLVersion.v3_0;
		CityGMLOutputFactory out = context.createCityGMLOutputFactory(version);
		Path output = Paths.get(fileName);
		Files.createDirectories(output.getParent());
		System.out.print(output.getParent());
		Files.createFile(output);

		try (CityGMLChunkWriter writer = out.createCityGMLChunkWriter(output, StandardCharsets.UTF_8.name())) {
			writer.withIndent("  ").withDefaultSchemaLocations().withDefaultPrefixes()
					.withDefaultNamespace(CoreModule.of(version).getNamespaceURI())
					.withHeaderComment("File created with citygml4j");
			writer.getCityModelInfo().setBoundedBy(new BoundingShape(envelope));
			writer.writeMember(building);
		}

	}

}