package org.opensourcebim.levelout.intermediatemodel;

import java.util.Arrays;
import java.util.List;

public class TestClass {

	public static void main(String[] args) throws Exception {
		// set1 ground
		GenericNode n1 = new GenericNode(1, 0, 0, 0);
		GenericNode n2 = new GenericNode(2, 0, 6, 0);
		GenericNode n3 = new GenericNode(3, 6, 6, 0);
		GenericNode n4 = new GenericNode(4, 6, 0, 0);

		// set2 ground
		GenericNode n5 = new GenericNode(5, 6, 0, 0);
		GenericNode n6 = new GenericNode(6, 6, 2, 0);
		GenericNode n7 = new GenericNode(7, 10, 2, 0);
		GenericNode n8 = new GenericNode(8, 10, 0, 0);

		//set 3 ground
		GenericNode n9 = new GenericNode(9, 6, 2, 0);
		GenericNode n10 = new GenericNode(10, 6, 6, 0);
		GenericNode n11 = new GenericNode(11, 10, 6, 0);
		GenericNode n12 = new GenericNode(12, 10, 2, 0);

		// set 4 floor 1
		GenericNode n13 = new GenericNode(13, 0, 0, 3);
		GenericNode n14 = new GenericNode(14, 0, 6, 3);
		GenericNode n15 = new GenericNode(15, 6, 6, 3);
		GenericNode n16 = new GenericNode(16, 6, 0, 3);

		// set 5 floor 1
		GenericNode n17 = new GenericNode(17, 6, 0, 3);
		GenericNode n18 = new GenericNode(18, 6, 2, 3);
		GenericNode n19 = new GenericNode(19, 10, 2, 3);
		GenericNode n20 = new GenericNode(20, 10, 0, 3);

		// set 6 floor 1
		GenericNode n21 = new GenericNode(21, 6, 2, 3);
		GenericNode n22 = new GenericNode(22, 6, 6, 3);
		GenericNode n23 = new GenericNode(23, 10, 6, 3);
		GenericNode n24 = new GenericNode(24, 10, 2, 3);

		// set 7 roof
		GenericNode n25 = new GenericNode(25, 0, 0, 6);
		GenericNode n26 = new GenericNode(26, 0, 6, 6);
		GenericNode n27 = new GenericNode(27, 6, 6, 6);
		GenericNode n28 = new GenericNode(28, 6, 0, 6);

		// set 8 roof
		GenericNode n29 = new GenericNode(29, 6, 0, 6);
		GenericNode n30 = new GenericNode(30, 6, 2, 6);
		GenericNode n31 = new GenericNode(31, 10, 2, 6);
		GenericNode n32 = new GenericNode(32, 10, 0, 6);

		// set 9 roof
		GenericNode n33 = new GenericNode(33, 6, 2, 6);
		GenericNode n34 = new GenericNode(34, 6, 6, 6);
		GenericNode n35 = new GenericNode(35, 10, 6, 6);
		GenericNode n36 = new GenericNode(36, 10, 2, 6);

		List<GenericNode> nodes1 = Arrays.asList(n1, n2, n3, n4);
		List<GenericNode> nodes2 = Arrays.asList(n5, n6, n7, n8);
		List<GenericNode> nodes3 = Arrays.asList(n9, n10, n11, n12);
		List<GenericNode> nodes4 = Arrays.asList(n13, n14, n15, n16);
		List<GenericNode> nodes5 = Arrays.asList(n17, n18, n19, n20);
		List<GenericNode> nodes6 = Arrays.asList(n21, n22, n23, n24);
		List<GenericNode> nodes7 = Arrays.asList(n25, n26, n27, n28);
		List<GenericNode> nodes8 = Arrays.asList(n29, n30, n31, n32);
		List<GenericNode> nodes9 = Arrays.asList(n33, n34, n35, n36);

		GenericPolygon gp1 = new GenericPolygon(1, "floor", 3, nodes1);
		GenericPolygon gp2 = new GenericPolygon(2, "floor", 3, nodes2);
		GenericPolygon gp3 = new GenericPolygon(3, "floor", 3, nodes3);
		GenericPolygon gp4 = new GenericPolygon(4, "floor", 3, nodes4);
		GenericPolygon gp5 = new GenericPolygon(5, "floor", 3, nodes5);
		GenericPolygon gp6 = new GenericPolygon(6, "floor", 3, nodes6);
		GenericPolygon gp7 = new GenericPolygon(7, "roof", 3, nodes7);
		GenericPolygon gp8 = new GenericPolygon(8, "roof", 3, nodes8);
		GenericPolygon gp9 = new GenericPolygon(9, "roof", 3, nodes9);

		List<GenericPolygon> polygons1 = Arrays.asList(gp1, gp2, gp3);
		List<GenericPolygon> polygons2 = Arrays.asList(gp4, gp5, gp6);
		List<GenericPolygon> polygons3 = Arrays.asList(gp7, gp8, gp9);

		FootPrint fp1 = new FootPrint(0, 1, polygons1);
		FootPrint fp2 = new FootPrint(1, 2, polygons2);
		FootPrint fp3 = new FootPrint(1, 3, polygons3);

		List<FootPrint> footPrints = Arrays.asList(fp1, fp2, fp3);

		GenericBuilding gbld = new GenericBuilding(footPrints, "test");
		gbld.createCitygmlBuilding();
		gbld.createOsmBuilding();
		gbld.createIndoorGmlBuilding();
	}
}

