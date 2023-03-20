package org.opensourcebim.levelout.intermediatemodel.geo;

import java.io.Serializable;

public class GeodeticPoint implements Serializable {
	private static final long serialVersionUID = -5330066318904627149L;
	public double latitude;
	public double longitude;
	public double height;

	public GeodeticPoint(double latitude, double longitude, double height) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.height = height;
	}
}
