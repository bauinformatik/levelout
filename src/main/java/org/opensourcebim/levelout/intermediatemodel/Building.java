package org.opensourcebim.levelout.intermediatemodel;

import org.opensourcebim.levelout.intermediatemodel.geo.CoordinateReference;
import org.opensourcebim.levelout.util.Geometry;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class Building implements Serializable {

	private static final long serialVersionUID = 5810896951015191802L;
	private final List<Storey> storeys;
	private final List<Corner> corners;
	private final CoordinateReference crs;

	public Building(List<Storey> storeys, List<Corner> corners, CoordinateReference crs) {
		Room.resetCounter();
		Door.resetCounter();
		this.storeys = storeys;
		this.corners = corners;
		this.crs = crs;
	}

	public List<Storey> getStoreys(){
		return Collections.unmodifiableList(storeys);
	}

	public List<Corner> getCorners() {
		return Collections.unmodifiableList(corners);
	}


	public List<Double> asCoordinateList() {
		return Geometry.asCoordinateList(corners, Geometry.minimumLevel(storeys)-0.1);
	}
	public List<Double> asCoordinateList(CoordinateReference crs) {
		return Geometry.asCoordinateList(corners, Geometry.minimumLevel(storeys)-0.1, crs);
	}
	public CoordinateReference getCrs() {
		return crs;
	}
}
