package org.opensourcebim.levelout.intermediatemodel.geo;

import java.io.Serializable;

public class ProjectedPoint implements Serializable {
	private static final long serialVersionUID = -207730394997299999L;

	public double eastings;
	public double northings;

	public ProjectedPoint(double eastings, double northings) {
		this.eastings = eastings;
		this.northings = northings;
	}

}
