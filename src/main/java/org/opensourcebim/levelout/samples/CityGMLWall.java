
package org.opensourcebim.levelout.samples;

import org.citygml4j.core.model.building.AbstractBuildingSubdivisionProperty;
import org.citygml4j.core.model.building.Building;
import org.citygml4j.core.model.building.BuildingConstructiveElement;
import org.citygml4j.core.model.building.BuildingConstructiveElementProperty;
import org.citygml4j.core.model.building.BuildingRoomProperty;
import org.citygml4j.core.model.building.Storey;
import org.citygml4j.core.model.construction.*;
import org.citygml4j.core.model.core.AbstractSpaceBoundaryProperty;
import org.citygml4j.core.util.geometry.GeometryFactory;
import org.xmlobjects.gml.model.geometry.primitives.*;
import org.xmlobjects.gml.util.id.DefaultIdCreator;
import org.xmlobjects.gml.util.id.IdCreator;

import java.io.FileOutputStream;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.opensourcebim.levelout.builders.CityGmlBuilder;

public class CityGMLWall {

	private IdCreator id;
	private GeometryFactory geom;

	public static void main(String[] args) throws Exception {
		new CityGMLWall().doMain();
	}

	public void doMain() throws Exception {
		String fileName = "output/roomstorey8.gml";

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

		Path output = Paths.get(fileName);
		Files.createDirectories(output.getParent());
		System.out.print(output.getParent());
		Files.createFile(output);

		cityGmlBuilder.write(new FileOutputStream(output.toFile()), building);

	}

}