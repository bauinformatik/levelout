package org.opensourcebim.levelout.util;

public class CoordinateConversion {
	public static GeodeticPoint convertCartesianToWGS84 (GeodeticPoint origin, CartesianPoint point){
		return new GeodeticPoint(0,0,0);
	}


	public static class GeodeticPoint {
		// TODO: add CS ref
		public double latitude;
		public double longitude;
		public double height;

		public GeodeticPoint(double latitude, double longitude, double height) {
			this.latitude = latitude;
			this.longitude = longitude;
			this.height = height;
		}
	}
	public static class CartesianPoint {
		public double x;
		public double y;
		public double z;

		public CartesianPoint(double x, double y, double z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

	}
}
