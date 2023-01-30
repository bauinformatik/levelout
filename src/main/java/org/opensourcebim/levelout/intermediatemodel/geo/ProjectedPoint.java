package org.opensourcebim.levelout.intermediatemodel.geo;

public class ProjectedPoint {

	public double eastings;
	public double northings;
	public double height;

	public ProjectedPoint(double eastings, double northings, double height) {
		this.eastings = eastings;
		this.northings = northings;
		this.height = height;
	}

}
