package org.opensourcebim.levelout.intermediatemodel;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;

public class TestClass {

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

		// set 7 roof
		Corner n25 = new Corner(25, 0, 0, 6);
		Corner n26 = new Corner(26, 0, 6, 6);
		Corner n27 = new Corner(27, 6, 6, 6);
		Corner n28 = new Corner(28, 6, 0, 6);

		// set 8 roof
		Corner n29 = new Corner(29, 6, 0, 6);
		Corner n30 = new Corner(30, 6, 2, 6);
		Corner n31 = new Corner(31, 10, 2, 6);
		Corner n32 = new Corner(32, 10, 0, 6);

		// set 9 roof
		Corner n33 = new Corner(33, 6, 2, 6);
		Corner n34 = new Corner(34, 6, 6, 6);
		Corner n35 = new Corner(35, 10, 6, 6);
		Corner n36 = new Corner(36, 10, 2, 6);

		List<Corner> nodes1 = Arrays.asList(n1, n2, n3, n4);
		List<Corner> nodes2 = Arrays.asList(n5, n6, n7, n8);
		List<Corner> nodes3 = Arrays.asList(n9, n10, n11, n12);
		List<Corner> nodes4 = Arrays.asList(n13, n14, n15, n16);
		List<Corner> nodes5 = Arrays.asList(n17, n18, n19, n20);
		List<Corner> nodes6 = Arrays.asList(n21, n22, n23, n24);
		List<Corner> nodes7 = Arrays.asList(n25, n26, n27, n28);
		List<Corner> nodes8 = Arrays.asList(n29, n30, n31, n32);
		List<Corner> nodes9 = Arrays.asList(n33, n34, n35, n36);

		Room gp1 = new Room(1, "floor", 3, nodes1);
		Room gp2 = new Room(2, "floor", 3, nodes2);
		Room gp3 = new Room(3, "floor", 3, nodes3);
		Room gp4 = new Room(4, "floor", 3, nodes4);
		Room gp5 = new Room(5, "floor", 3, nodes5);
		Room gp6 = new Room(6, "floor", 3, nodes6);
		Room gp7 = new Room(7, "roof", 3, nodes7);
		Room gp8 = new Room(8, "roof", 3, nodes8);
		Room gp9 = new Room(9, "roof", 3, nodes9);

		List<Room> polygons1 = Arrays.asList(gp1, gp2, gp3);
		List<Room> polygons2 = Arrays.asList(gp4, gp5, gp6);
		List<Room> polygons3 = Arrays.asList(gp7, gp8, gp9);

		Storey fp1 = new Storey(0, 1, polygons1);
		Storey fp2 = new Storey(1, 2, polygons2);
		Storey fp3 = new Storey(1, 3, polygons3);

		List<Storey> footPrints = Arrays.asList(fp1, fp2, fp3);

		if(!new File("output").exists()){
			if(!new File("output").mkdir()){
				return;
			};
		}
		GenericBuilding gbld = new GenericBuilding(footPrints);
		gbld.createCitygmlBuilding(new FileOutputStream("output/test-city.gml"));
		gbld.createOsmBuilding(new FileOutputStream("output/test.osm"));
		gbld.createIndoorGmlBuilding(new FileOutputStream("output/test-indoor.gml", true));
	}
}

