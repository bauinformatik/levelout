package org.opensourcebim.levelout.intermediatemodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.impl.Node;

public class GenericNode {
	
	//private int NumberofNodes;
	private Number id;
	private Number x;
	private Number y;
	private Number z;
	
	public GenericNode(Number id, Number x, Number y, Number z) {
		super();
		this.id = id;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	
	public OsmNode createOsmnode()
	{
		
		//List <OsmNode> nodelist = new ArrayList<OsmNode>();

		OsmNode newnode = new Node((long)id*-1,(long)x,(long)y);

		//nodelist.add(newnode);

		return newnode;
		
	}
	
	public List<Double> createCitygmlnode()
	{
		List<Double> doubleList = new ArrayList<Double>();
		doubleList.add( (double)x);
		doubleList.add( (double)y);
		doubleList.add( (double)z);
		return doubleList;
		
	}
	
	public Number getId() {
		return id;
	}


	public void setId(Number id) {
		this.id = id;
	}
	public Number getX() {
		return x;
	}
	public void setX(Number x) {
		this.x = x;
	}
	public Number getY() {
		return y;
	}
	public void setY(Number y) {
		this.y = y;
	}
	public Number getZ() {
		return z;
	}
	public void setZ(Number z) {
		this.z = z;
	}
}
