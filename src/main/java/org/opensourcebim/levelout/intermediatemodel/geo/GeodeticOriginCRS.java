package org.opensourcebim.levelout.intermediatemodel.geo;

import org.locationtech.proj4j.*;

import java.io.Serializable;

public class GeodeticOriginCRS extends CoordinateReference implements Serializable {
	private static final long serialVersionUID = 6482545861344232840L;
	private final GeodeticPoint origin;
	private final double rotation;
	private final double utmGridConvergence;
	private final ProjCoordinate utmOrigin;
	private final CoordinateTransform utmToWgs84;

	public GeodeticOriginCRS(GeodeticPoint origin, double rotation) {
		this.origin = origin;
		this.rotation = rotation;

		CoordinateReferenceSystem wgs84 = crsFactory.createFromName("epsg:4326");
		String epsg = "epsg:" + getEpsg();
		CoordinateReferenceSystem utm = crsFactory.createFromName(epsg);
		utmGridConvergence = getUtmGridConvergence();
		CoordinateTransform wgs84ToUTM = ctFactory.createTransform(wgs84, utm);
		utmOrigin = wgs84ToUTM.transform(new ProjCoordinate(origin.longitude, origin.latitude),
				new ProjCoordinate());
		utmToWgs84 = ctFactory.createTransform(utm, wgs84);
	}

	String getEpsg() {
		// TODO : Account for special cases: Norway and Svalbard
		if (origin.latitude <= -80 || origin.latitude > 84) {
			throw new IllegalArgumentException("Latitude outside of valid range -80..84.");
		} else {
			String segment = String.format("%02d", getSegmentNumber());
			return (origin.latitude > 0 ? "326" : "327") + segment;
		}
	}

	private int getSegmentNumber() {
		return (int) ((Math.floor((origin.longitude + 180) / 6) % 60) + 1);
	}

	double getUtmGridConvergence() {  // at origin, this may still be off if the origin is far away from actual coordinates
		double longitudeDiff = origin.longitude - (6 * (double) getSegmentNumber() - 183);
		return Math.atan(Math.tan(Math.toRadians(longitudeDiff)) * Math.sin(Math.toRadians(origin.latitude)));
	}

	public GeodeticPoint cartesianToGeodeticViaGeoCentric(CartesianPoint cart) {
		// https://epsg.io/9837-method
		// https://en.wikipedia.org/wiki/Geographic_coordinate_conversion#Geodetic_to/from_ENU_coordinates
		// https://proj.org/operations/conversions/topocentric.html
		// tests: https://github.com/locationtech/proj4j/tree/67d5e85fdf93f204e3b0d564f79c3195aaedaec0/core/src/test/java/org/locationtech/proj4j
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

	public GeodeticPoint cartesianToGeodeticViaGeoCentric2(CartesianPoint cart) {
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

		// double e =
		CoordinateTransform geotoGCtrans = ctFactory.createTransform(step1, WGS842);
		ProjCoordinate geotoGC = geotoGCtrans.transform(new ProjCoordinate(origin.longitude, origin.latitude),
				new ProjCoordinate());

		System.out.println(geotoGC.x);
		System.out.println(geotoGC.y);
		return null;
	}

	@Override
	public GeodeticPoint cartesianToGeodetic(CartesianPoint cart) {
		double a = Math.cos(rotation + utmGridConvergence);
		double b = Math.sin(rotation + utmGridConvergence);
		double utmPointX = (a * cart.x) - (b * cart.y) + utmOrigin.x;
		double utmPointY = (b * cart.x) + (a * cart.y) + utmOrigin.y;

		ProjCoordinate wgs84Point = utmToWgs84.transform(new ProjCoordinate(utmPointX, utmPointY),
				new ProjCoordinate());

		return new GeodeticPoint(wgs84Point.y, wgs84Point.x, origin.height);

	}
}
