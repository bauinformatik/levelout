package org.opensourcebim.levelout.util;

import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.CoordinateTransformFactory;
import org.locationtech.proj4j.ProjCoordinate;

import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.UTMRef;

public class CoordinateConversion {
	private static final CRSFactory crsFactory = new CRSFactory();
	private static final CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();

	public static GeodeticPoint originWGS84viaGeoCentric(GeodeticPoint origin, CartesianPoint point) {
		// https://epsg.io/9837-method
		// https://proj.org/operations/conversions/topocentric.html
		// tests:
		// https://github.com/locationtech/proj4j/tree/67d5e85fdf93f204e3b0d564f79c3195aaedaec0/core/src/test/java/org/locationtech/proj4j
		CRSFactory factory = new CRSFactory();
		// CoordinateReferenceSystem WGS84 = factory.createFromName("epsg:4326");
		CoordinateReferenceSystem WGS842 = factory.createFromParameters("WGS84", "+proj=longlat +datum=WGS84 +no_defs");
		CoordinateReferenceSystem step1 = factory.createFromParameters("Geodetic to Geocentric",
				"+proj=longlat +datum=WGS84");
		CoordinateReferenceSystem step2 = factory.createFromParameters("Geocentric to Topocentric",
				"+proj=topocentric +datum=WGS84 +lon_0=5 +lat_0=55"); // +h_0=200 factory.createFromName("epsg:9836");
		CoordinateReferenceSystem source = factory.createFromParameters("Topo", new String[] {});
		CoordinateReferenceSystem intermediate = factory.createFromParameters("Geocentric", new String[] {});
		CoordinateTransform ct = new CoordinateTransformFactory().createTransform(step1, step2);
		// ProjCoordinate utmOrigin = wgs84ToUTM.transform(new
		// ProjCoordinate(origin.longitude, origin.latitude),
		// new ProjCoordinate());
		// may be automatic in transformation
		CoordinateReferenceSystem target = factory.createFromParameters("Geodetic", new String[] {});
		new CoordinateTransformFactory().createTransform(source, intermediate);
		new CoordinateTransformFactory().createTransform(intermediate, target);
		new CoordinateTransformFactory().createTransform(step1, step2);
		return new GeodeticPoint(0, 0, 0);

	}

	public static void originWGS84viaGeoCentric2(GeodeticPoint origin, CartesianPoint point) {
		CoordinateReferenceSystem WGS842 = crsFactory.createFromParameters("WGS84",
				"+proj=longlat +datum=WGS84 +no_defs");
		CoordinateReferenceSystem step1 = crsFactory.createFromParameters("geotoGC",
				"+proj=longlat +datum=ED50 +no_defs");

		CoordinateTransform geotoGCtrans = ctFactory.createTransform(WGS842, step1);
		ProjCoordinate geotoGC = geotoGCtrans.transform(new ProjCoordinate(origin.longitude, origin.latitude),
				new ProjCoordinate());

		System.out.println(geotoGC.x);
		System.out.println(geotoGC.y);

	}

	public static GeodeticPoint originWGS84viaUTM(GeodeticPoint origin, CartesianPoint point,
												  double rotation) {

		CoordinateReferenceSystem wgs84 = crsFactory.createFromName("epsg:4326");
		String epsg = "epsg:" + getEpsg(origin);
		CoordinateReferenceSystem utm = crsFactory.createFromName(epsg); // use fixed UTM EPSG, we don't have this from
		// IFC

		CoordinateTransform wgs84ToUTM = ctFactory.createTransform(wgs84, utm);
		ProjCoordinate utmOrigin = wgs84ToUTM.transform(new ProjCoordinate(origin.longitude, origin.latitude),
				new ProjCoordinate());

		double a = Math.cos(rotation);
		double b = Math.sin(rotation);
		double utmPointX = (a * point.x) - (b * point.y) + utmOrigin.x;
		double utmPointY = (b * point.x) + (a * point.y) + utmOrigin.y;

		CoordinateTransform utmToWgs84 = ctFactory.createTransform(utm, wgs84);
		ProjCoordinate wgs84Point = utmToWgs84.transform(new ProjCoordinate(utmPointX, utmPointY),
				new ProjCoordinate());

		GeodeticPoint transformed = new GeodeticPoint(wgs84Point.y, wgs84Point.x, origin.height);

		return transformed;

	}

	private static String getEpsg(GeodeticPoint origin) {

		System.out.println("Convert Latitude/Longitude to UTM Reference");
		LatLng latlon = new LatLng(origin.latitude, origin.longitude);
		UTMRef utm = latlon.toUTMRef();
		int longzonenum = utm.getLngZone();
		String epsg;
		if (origin.latitude > 0) {
			epsg = "326" + Integer.toString(longzonenum);
		} else {
			epsg = "327" + Integer.toString(longzonenum);
		}
		return epsg;

	}

	public static ProjectedPoint originArbitraryCRS(CartesianPoint point, ProjectedPoint projOrigin,
													double xAxisAbscissa, double xAxisOrdinate, String epsg) {

		double rotation = Math.atan2(xAxisOrdinate, xAxisAbscissa);
		double a = Math.cos(rotation);
		double b = Math.sin(rotation);
		// TODO just normalize, not need for trigonometric functions

		double eastingsmap = (a * point.x) - (b * point.y) + projOrigin.eastings;
		double northingsmap = (b * point.x) + (a * point.y) + projOrigin.northings;

		CoordinateReferenceSystem wgs84 = crsFactory.createFromName("epsg:4326");
		CoordinateReferenceSystem originCRS = crsFactory.createFromName(epsg);

		CoordinateTransform originCrsToWgs84 = ctFactory.createTransform(originCRS, wgs84);
		ProjCoordinate resultcoord = originCrsToWgs84.transform(new ProjCoordinate(eastingsmap, northingsmap),
				new ProjCoordinate());

		ProjectedPoint transformed = new ProjectedPoint(resultcoord.x, resultcoord.y, projOrigin.height);
		return transformed;
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

	public static class ProjectedPoint {

		public double eastings;
		public double northings;
		public double height;

		public ProjectedPoint(double eastings, double northings, double height) {
			super();
			this.eastings = eastings;
			this.northings = northings;
			this.height = height;
		}

	}

}
