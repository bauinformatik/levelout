package org.opensourcebim.levelout.util;

import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.CoordinateTransformFactory;

public class CoordinateConversion {
	public static GeodeticPoint convertCartesianToWGS84 (GeodeticPoint origin, CartesianPoint point){
		// https://epsg.io/9837-method
		// https://proj.org/operations/conversions/topocentric.html
		// tests: https://github.com/locationtech/proj4j/tree/67d5e85fdf93f204e3b0d564f79c3195aaedaec0/core/src/test/java/org/locationtech/proj4j
		CRSFactory factory = new CRSFactory();
		// CoordinateReferenceSystem WGS84 = factory.createFromName("epsg:4326");
		CoordinateReferenceSystem WGS842 = factory.createFromParameters("WGS84", "+proj=longlat +datum=WGS84 +no_defs");
		CoordinateReferenceSystem step1 = factory.createFromParameters("Geodetic to Geocentric", "+proj=longlat +datum=WGS84");
		CoordinateReferenceSystem step2 = factory.createFromParameters("Geocentric to Topocentric", "+proj=topocentric +datum=WGS84 +lon_0=5 +lat_0=55"); //  +h_0=200
		CoordinateReferenceSystem source = factory.createFromParameters("Topo", new String[]{});
		CoordinateReferenceSystem intermediate = factory.createFromParameters("Geocentric", new String[]{});
			// may be automatic in transformation
		CoordinateReferenceSystem target = factory.createFromParameters("Geodetic", new String[]{});
		new CoordinateTransformFactory().createTransform(source, intermediate);
		new CoordinateTransformFactory().createTransform(intermediate, target);
		new CoordinateTransformFactory().createTransform(step1, step2);
		return new GeodeticPoint(0,0,0);
	}

	public static GeodeticPoint cartesianWithWGS84OriginToWGS84viaUTM(){
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
