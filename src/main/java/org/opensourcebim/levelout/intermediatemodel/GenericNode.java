package org.opensourcebim.levelout.intermediatemodel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import de.topobyte.osm4j.core.access.OsmOutputStream;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.impl.Node;
import de.topobyte.osm4j.xml.output.OsmXmlOutputStream;

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
	

		OsmNode newnode1 = new Node((id.longValue()*-1),x.longValue(),y.longValue());

		//nodelist.add(newnode);
		//gb.
		
		//osmOutput.wait();
		
		//osmOutput.write(newnode1);
		
		return newnode1;
		
		
	}
	
	public List<Double> createCitygmlnode()
	{
		
		List<Double> doubleList = new ArrayList<Double>();
		doubleList.add(x.doubleValue());                                        // Number cannot be cast to a primitive, use Number.doubleValue()
		doubleList.add(y.doubleValue());
		doubleList.add(z.doubleValue());
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
