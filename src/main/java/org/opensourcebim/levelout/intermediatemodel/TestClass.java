package org.opensourcebim.levelout.intermediatemodel;

import java.util.ArrayList;
import java.util.List;

public class TestClass {
	
	public static void main(String[] args) throws Exception
	{
		GenericNode n1 = new GenericNode(1, 0, 0, 0);
		GenericNode n2 = new GenericNode(2, 0, 6, 0);
		GenericNode n3 = new GenericNode(3, 10, 6, 0);
		GenericNode n4 = new GenericNode(4, 10, 0, 0);

		
		List <GenericNode> genericnodesList = new ArrayList<GenericNode>();
		genericnodesList.add(n1);
		genericnodesList.add(n2);
		genericnodesList.add(n3);
		genericnodesList.add(n4);
		
		GenericPolygon gp = new GenericPolygon(1, "ground", 3, genericnodesList);
		
		List <GenericPolygon> genericPolygonList = new ArrayList<GenericPolygon>();
		genericPolygonList.add(gp);
		
		FootPrint fp = new FootPrint(1, 1, genericPolygonList);
		
		GenericBuilding gbld = new GenericBuilding(fp);
		//gbld.createCitygmlBuilding();
		
	//	gbld.createOsmBuilding();
		
		gbld.createIndoorGmlBuilding();
	}
}
