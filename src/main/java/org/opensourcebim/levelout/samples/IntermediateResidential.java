package org.opensourcebim.levelout.samples;


import org.opensourcebim.levelout.intermediatemodel.Building;
import org.opensourcebim.levelout.intermediatemodel.Corner;
import org.opensourcebim.levelout.intermediatemodel.Door;
import org.opensourcebim.levelout.intermediatemodel.MapConversion;
import org.opensourcebim.levelout.intermediatemodel.Room;
import org.opensourcebim.levelout.intermediatemodel.Storey;
import org.opensourcebim.levelout.builders.CityGmlBuilder;
import org.opensourcebim.levelout.builders.IndoorGmlBuilder;
import org.opensourcebim.levelout.builders.OsmBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class IntermediateResidential {

	public static void main(String[] args) throws Exception {
		// set1 ground
		Corner n1 = new Corner(1, 0, 0, 0);
		Corner n2 = new Corner(2, 0, 6, 0);
		Corner n3 = new Corner(3, 6, 6, 0);
		Corner n4 = new Corner(4, 6, 0, 0);

		// set2 ground
		Corner n5 = new Corner(5, 6, 0, 0);
		Corner n6 = new Corner(6, 6, 2, 0);
		Corner n7 = new Corner(7, 10, 2, 0);
		Corner n8 = new Corner(8, 10, 0, 0);

		//set 3 ground
		Corner n9 = new Corner(9, 6, 2, 0);
		Corner n10 = new Corner(10, 6, 6, 0);
		Corner n11 = new Corner(11, 10, 6, 0);
		Corner n12 = new Corner(12, 10, 2, 0);

		// set 4 floor 1
		Corner n13 = new Corner(13, 0, 0, 3);
		Corner n14 = new Corner(14, 0, 6, 3);
		Corner n15 = new Corner(15, 6, 6, 3);
		Corner n16 = new Corner(16, 6, 0, 3);

		// set 5 floor 1
		Corner n17 = new Corner(17, 6, 0, 3);
		Corner n18 = new Corner(18, 6, 2, 3);
		Corner n19 = new Corner(19, 10, 2, 3);
		Corner n20 = new Corner(20, 10, 0, 3);

		// set 6 floor 1
		Corner n21 = new Corner(21, 6, 2, 3);
		Corner n22 = new Corner(22, 6, 6, 3);
		Corner n23 = new Corner(23, 10, 6, 3);
		Corner n24 = new Corner(24, 10, 2, 3);

		// set 10 door
		Corner n37 = new Corner(37, 1, 0, 0);
		Corner n38 = new Corner(38, 2, 0, 0);
		Corner n39 = new Corner(39, 6, 5, 0);
		Corner n40 = new Corner(40, 6, 6, 0);

		List<Corner> nodes1 = Arrays.asList(n1, n2, n3, n4);
		List<Corner> nodes2 = Arrays.asList(n5, n6, n7, n8);
		List<Corner> nodes3 = Arrays.asList(n9, n10, n11, n12);
		List<Corner> nodes4 = Arrays.asList(n13, n14, n15, n16);
		List<Corner> nodes5 = Arrays.asList(n17, n18, n19, n20);
		List<Corner> nodes6 = Arrays.asList(n21, n22, n23, n24);
		List<Corner> corners10 = Arrays.asList(n37, n38);
		List<Corner> corners11 = Arrays.asList(n39, n40);

		Room gp1 = new Room(1, "floor", nodes1);
		Room gp2 = new Room(2, "floor", nodes2);
		Room gp3 = new Room(3, "floor", nodes3);
		Room gp4 = new Room(4, "floor", nodes4);
		Room gp5 = new Room(5, "floor", nodes5);
		Room gp6 = new Room(6, "floor", nodes6);
		Door gp10 = new Door(10, "door", corners10);
		Door gp11 = new Door(11, "door", corners11);

		List<Room> polygons1 = Arrays.asList(gp1, gp2, gp3);
		List<Room> polygons2 = Arrays.asList(gp4, gp5, gp6);
		List<Door>	door = Arrays.asList(gp10,gp11);

		Storey st1 = new Storey(0, polygons1, door);
		Storey st2 = new Storey(1, polygons2, Collections.emptyList());

		List<Storey> storeys = Arrays.asList(st1, st2);

		if(!new File("output").exists()){
			if(!new File("output").mkdir()){
				return;
			};
		}
		int ifcVersion = 4;
		
		Building gbld = new Building(10, storeys);
	//	new CityGmlBuilder().createAndWriteBuilding(gbld, new FileOutputStream("output/test-city.gml"));
		new OsmBuilder().createAndWriteBuilding(gbld, ifcVersion, new FileOutputStream("output/test.osm"));
	//	new IndoorGmlBuilder().createAndWriteBuilding(gbld, new FileOutputStream("output/test-indoor.gml"));
	}
}

