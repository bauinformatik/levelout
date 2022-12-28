package org.opensourcebim.levelout.intermediatemodel;

import java.util.ArrayList;
import java.util.List;

import org.citygml4j.core.model.construction.CeilingSurface;
import org.citygml4j.core.model.construction.DoorSurface;
import org.citygml4j.core.model.construction.FloorSurface;
import org.citygml4j.core.model.construction.GroundSurface;
import org.citygml4j.core.model.construction.RoofSurface;
import org.citygml4j.core.model.construction.WallSurface;
import org.citygml4j.core.model.core.AbstractSpaceBoundaryProperty;
import org.citygml4j.core.model.core.AbstractThematicSurface;
import org.citygml4j.core.util.geometry.GeometryFactory;
import org.xmlobjects.gml.model.geometry.aggregates.MultiCurveProperty;
import org.xmlobjects.gml.model.geometry.aggregates.MultiSurfaceProperty;
import org.xmlobjects.gml.model.geometry.primitives.LineString;
import org.xmlobjects.gml.model.geometry.primitives.Polygon;
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
	public LineString createCitygmlLines() {
		List<Double> coordinates = new ArrayList<>();
		for (Corner genericNode : corners) {
			coordinates.addAll(genericNode.createCityGmlNode()); // adding the list generated from citygmlnode
		}
		return geometryFactory.createLineString(coordinates, getDimension());
	}
	
	public AbstractSpaceBoundaryProperty createBoundaryLine(LineString line) {
		if (type.contains("wall")) {
			return processBoundarySurface(new WallSurface(), line);
		} else if (type.contains("door")) {
				return processBoundarySurface(new DoorSurface(), line);
		 
		} else return null;  // TODO: not nice
	}
	
	private AbstractSpaceBoundaryProperty processBoundarySurface(AbstractThematicSurface thematicSurface,
			LineString line) {
		thematicSurface.setId(idCreator.createId());
		thematicSurface.setLod0MultiCurve(new MultiCurveProperty(geometryFactory.createMultiCurve(line)));
		return new AbstractSpaceBoundaryProperty(thematicSurface);
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
