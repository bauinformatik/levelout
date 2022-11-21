package org.opensourcebim.levelout.intermediatemodel;

import java.util.ArrayList;
import java.util.List;

public class TestClass {
	
	public static void main(String[] args) throws Exception
	{
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
		GenericNode n11= new GenericNode(11, 10, 6, 0);
		GenericNode n12= new GenericNode(12, 10, 2, 0);
		// set 4 floor 1 
		
		GenericNode n13 = new GenericNode(13, 0, 0, 3);
		GenericNode n14= new GenericNode(14, 0, 6, 3);
		GenericNode n15 = new GenericNode(15, 6, 6, 3);
		GenericNode n16= new GenericNode(16, 6, 0, 3);
		
		// set 5 floor 1
		GenericNode n17 = new GenericNode(17, 6, 0, 3);
		GenericNode n18 = new GenericNode(18, 6, 2, 3);
		GenericNode n19 = new GenericNode(19, 10, 2, 3);
		GenericNode n20 = new GenericNode(20, 10, 0, 3);
		
		// set 6 floor 1 
		
		GenericNode n21 = new GenericNode(21, 6, 2, 3);
		GenericNode n22 = new GenericNode(22, 6, 6, 3);
		GenericNode n23= new GenericNode(23, 10, 6, 3);
		GenericNode n24= new GenericNode(24, 10, 2, 3);
		
	// set 7 roof 
		
		GenericNode n25 = new GenericNode(25, 0, 0, 6);
		GenericNode n26= new GenericNode(26, 0, 6, 6);
		GenericNode n27= new GenericNode(27, 6, 6, 6);
		GenericNode n28= new GenericNode(28, 6, 0, 6);
	// set 8 roof
		GenericNode n29 = new GenericNode(29, 6, 0, 6);
		GenericNode n30= new GenericNode(30, 6, 2, 6);
		GenericNode n31 = new GenericNode(31, 10, 2, 6);
		GenericNode n32 = new GenericNode(32, 10, 0, 6);
		
		// set 9 roof
		
		GenericNode n33 = new GenericNode(33, 6, 2, 6);
		GenericNode n34 = new GenericNode(34, 6, 6, 6);
		GenericNode n35= new GenericNode(35, 10, 6, 6);
		GenericNode n36= new GenericNode(36, 10, 2, 6);
		
		List <GenericNode> genericnodesList = new ArrayList<GenericNode>();
		genericnodesList.add(n1);
		genericnodesList.add(n2);
		genericnodesList.add(n3);
		genericnodesList.add(n4);
		
		List <GenericNode> genericnodesList2 = new ArrayList<GenericNode>();
		genericnodesList2.add(n5);
		genericnodesList2.add(n6);
		genericnodesList2.add(n7);
		genericnodesList2.add(n8);
		
		List <GenericNode> genericnodesList3 = new ArrayList<GenericNode>();
		genericnodesList3.add(n9);
		genericnodesList3.add(n10);
		genericnodesList3.add(n11);
		genericnodesList3.add(n12);
		
		List <GenericNode> genericnodesList4 = new ArrayList<GenericNode>();
		genericnodesList4.add(n13);
		genericnodesList4.add(n14);
		genericnodesList4.add(n15);
		genericnodesList4.add(n16);
		
		List <GenericNode> genericnodesList5 = new ArrayList<GenericNode>();
		genericnodesList5.add(n17);
		genericnodesList5.add(n18);
		genericnodesList5.add(n19);
		genericnodesList5.add(n20);
		
		List <GenericNode> genericnodesList6 = new ArrayList<GenericNode>();
		genericnodesList6.add(n21);
		genericnodesList6.add(n22);
		genericnodesList6.add(n23);
		genericnodesList6.add(n24);
		
		List <GenericNode> genericnodesList7 = new ArrayList<GenericNode>();
		genericnodesList7.add(n25);
		genericnodesList7.add(n26);
		genericnodesList7.add(n27);
		genericnodesList7.add(n28);
		
		List <GenericNode> genericnodesList8 = new ArrayList<GenericNode>();
		genericnodesList8.add(n29);
		genericnodesList8.add(n30);
		genericnodesList8.add(n31);
		genericnodesList8.add(n32);
		
		List <GenericNode> genericnodesList9 = new ArrayList<GenericNode>();
		genericnodesList9.add(n33);
		genericnodesList9.add(n34);
		genericnodesList9.add(n35);
		genericnodesList9.add(n36);
		
		
		GenericPolygon gp = new GenericPolygon(1, "floor", 3, genericnodesList);
		GenericPolygon gp2 = new GenericPolygon(2, "floor", 3, genericnodesList2);
		GenericPolygon gp3 = new GenericPolygon(3, "floor", 3, genericnodesList3);
		GenericPolygon gp4 = new GenericPolygon(4, "floor", 3, genericnodesList4);
		GenericPolygon gp5 = new GenericPolygon(5, "floor", 3, genericnodesList5);
		GenericPolygon gp6 = new GenericPolygon(6, "floor", 3, genericnodesList6);
		GenericPolygon gp7 = new GenericPolygon(7, "roof", 3, genericnodesList7);
		GenericPolygon gp8 = new GenericPolygon(8, "roof", 3, genericnodesList8);
		GenericPolygon gp9 = new GenericPolygon(9, "roof", 3, genericnodesList9);
		
		List <GenericPolygon> genericPolygonList = new ArrayList<GenericPolygon>();
		genericPolygonList.add(gp);
		genericPolygonList.add(gp2);
		genericPolygonList.add(gp3);
		
		List <GenericPolygon> genericPolygonList2 = new ArrayList<GenericPolygon>();
		genericPolygonList2.add(gp4);
		genericPolygonList2.add(gp5);
		genericPolygonList2.add(gp6);
		
		List <GenericPolygon> genericPolygonList3 = new ArrayList<GenericPolygon>();
		genericPolygonList3.add(gp7);
		genericPolygonList3.add(gp8);
		genericPolygonList3.add(gp9);
		
		FootPrint fp = new FootPrint(0, 1, genericPolygonList);
		FootPrint fp2 = new FootPrint(1, 2, genericPolygonList2);
		FootPrint fp3 = new FootPrint(1, 3, genericPolygonList3);
		
		List <FootPrint> footprintlist = new ArrayList<FootPrint>();
		footprintlist.add(fp);
		footprintlist.add(fp2);
		footprintlist.add(fp3);
		
		GenericBuilding gbld = new GenericBuilding(footprintlist);
		
		System.out.println(footprintlist.isEmpty());
		gbld.createCitygmlBuilding();
		
		//gbld.createOsmBuilding();
		
		//gbld.createIndoorGmlBuilding();
	}
}
