package org.opensourcebim.levelout.intermediatemodel;

import java.util.List;

import org.citygml4j.core.util.geometry.GeometryFactory;
import org.xmlobjects.gml.util.id.DefaultIdCreator;
import org.xmlobjects.gml.util.id.IdCreator;

public class Door {

	
	
	private  String type;
	private  int dimension;
	private  List<Corner> corners;
	private final  IdCreator idCreator = DefaultIdCreator.getInstance();
	private final GeometryFactory geometryFactory = GeometryFactory.newInstance().withIdCreator(idCreator);
	
	public Door(long id, String type, int dimension, List<Corner> corners) {
		super();
		this.id = id;
		this.type = type;
		this.dimension = dimension;
		this.corners = corners;
	}


	private  long id;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getDimension() {
		return dimension;
	}
	public void setDimension(int dimension) {
		this.dimension = dimension;
	}
	public List<Corner> getCorners() {
		return corners;
	}
	public void setCorners(List<Corner> corners) {
		this.corners = corners;
	}

}
