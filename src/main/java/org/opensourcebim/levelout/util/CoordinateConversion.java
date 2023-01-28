package org.opensourcebim.levelout.util;

import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.CoordinateTransformFactory;
import org.locationtech.proj4j.ProjCoordinate;


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

	static void originWGS84viaGeoCentric2(GeodeticPoint origin, CartesianPoint point) {
		CoordinateReferenceSystem WGS842 = crsFactory.createFromParameters("WGS84",
				"+proj=longlat +datum=WGS84 +no_defs");
		CoordinateReferenceSystem step1 = crsFactory.createFromParameters("geotoGC",
				"+proj=cart +ellps=GRS80 +no_defs");
//double a = 6378137;
//double b = 6356752.31414;
//double e2 = 0.00669438002;
//double Po = 0;
//double Lo =0;
//double height = 

		//double  e = 
		CoordinateTransform geotoGCtrans = ctFactory.createTransform(step1, WGS842);
		ProjCoordinate geotoGC = geotoGCtrans.transform(new ProjCoordinate(origin.longitude, origin.latitude),
				new ProjCoordinate());

		System.out.println(geotoGC.x);
		System.out.println(geotoGC.y);

	}

	private static GeodeticPoint originWGS84viaUTM(CartesianPoint point, GeodeticPoint origin,
												  double rotation) {

		CoordinateReferenceSystem wgs84 = crsFactory.createFromName("epsg:4326");
		String epsg = "epsg:" + getEpsg(origin);
		CoordinateReferenceSystem utm = crsFactory.createFromName(epsg);

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

		return new GeodeticPoint(wgs84Point.y, wgs84Point.x, origin.height);

	}

	static String getEpsg(GeodeticPoint origin) {
		// TODO : Account for special cases: Norway and Svalbard
		int num = 0;  // TODO throw  IllegalArgumentException if outside valid latitudes, should not be 32600 / 32700 in that case
		if (origin.latitude<=84 && (origin.latitude>-80)) {
			num = (int) ((Math.floor((origin.longitude + 180) / 6 ) % 60) + 1) ;
		}
		return origin.latitude > 0 ? "326" + num : "327" + num; // TODO pad with 0 for zone numbers 1-9
	}

	private static GeodeticPoint originArbitraryCRS(CartesianPoint point, ProjectedPoint projOrigin,
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

		return new GeodeticPoint(resultcoord.y, resultcoord.x, projOrigin.height);
	}

	public static class GeodeticPoint {
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
			this.eastings = eastings;
			this.northings = northings;
			this.height = height;
		}

	}

	public abstract static class CoordinateReference{
		public abstract GeodeticPoint cartesianToGeodetic(CartesianPoint cart);

	}

	public static class GeodeticOriginCRS extends CoordinateReference {
		private final GeodeticPoint origin;
		private final double rotation;

		public GeodeticOriginCRS(GeodeticPoint origin, double rotation){
			this.origin = origin;
			this.rotation = rotation;
		}

		@Override
		public GeodeticPoint cartesianToGeodetic(CartesianPoint cart) {
			return CoordinateConversion.originWGS84viaUTM(cart, origin, rotation);
		}
	}
	public static class ProjectedOriginCRS extends CoordinateReference {
		private final ProjectedPoint origin;
		private final double xAxisAbscissa;
		private final double xAxisOrdinate;
		private final String epsg;

		public ProjectedOriginCRS(ProjectedPoint origin, double xAxisAbscissa, double xAxisOrdinate, String epsg){
			this.origin = origin;
			this.xAxisAbscissa = xAxisAbscissa;
			this.xAxisOrdinate =xAxisOrdinate;
			this.epsg = epsg;
		}
		@Override
		public GeodeticPoint cartesianToGeodetic(CartesianPoint cart) {
			return CoordinateConversion.originArbitraryCRS(cart, origin, xAxisAbscissa, xAxisOrdinate, epsg);
		}
	}

}
