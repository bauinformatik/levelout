package org.opensourcebim.levelout.intermediatemodel.geo;

public class GeodeticPoint {
	public double latitude;
	public double longitude;
	public double height;

	public GeodeticPoint(double latitude, double longitude, double height) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.height = height;
	}
}
