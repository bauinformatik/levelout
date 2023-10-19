package org.opensourcebim.levelout.intermediatemodel.geo;

import org.locationtech.proj4j.*;

import java.io.Serializable;

public class GeodeticOriginCRS extends CoordinateReference implements Serializable {
	private static final long serialVersionUID = 6482545861344232840L;
	final GeodeticPoint origin;
	private final ProjCoordinate utmOrigin;
	private final CoordinateTransform utmToWgs84;

	public GeodeticOriginCRS(GeodeticPoint origin, double rotation, double scale) {
		super(rotation + origin.getUtmGridConvergence(), scale);
		this.origin = origin;
		CoordinateReferenceSystem wgs84 = crsFactory.createFromName("epsg:4326");
		String epsg = "epsg:" + origin.getUtmEpsg();
		CoordinateReferenceSystem utm = crsFactory.createFromName(epsg);
		CoordinateTransform wgs84ToUTM = ctFactory.createTransform(wgs84, utm);
		utmOrigin = wgs84ToUTM.transform(new ProjCoordinate(origin.longitude, origin.latitude), new ProjCoordinate());
		utmToWgs84 = ctFactory.createTransform(utm, wgs84);
	}

	public GeodeticOriginCRS(GeodeticPoint origin, double rotation) {
		this(origin, rotation, 1);
	}

	public GeodeticPoint cartesianToGeodeticViaGeoCentric(CartesianPoint cart) {
		// https://epsg.io/9837-method
		// https://en.wikipedia.org/wiki/Geographic_coordinate_conversion#Geodetic_to/from_ENU_coordinates
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
		return new GeodeticPoint(0, 0);
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
		CartesianPoint rotatedAndScaled = rotateAndScale(cart);
		double utmPointX = rotatedAndScaled.x + utmOrigin.x;
		double utmPointY = rotatedAndScaled.y + utmOrigin.y;
		ProjCoordinate wgs84Point = utmToWgs84.transform(new ProjCoordinate(utmPointX, utmPointY),
				new ProjCoordinate());
		return new GeodeticPoint(wgs84Point.y, wgs84Point.x);
	}

	@Override
	public double getOriginX() {
		return origin.latitude;
	}

	@Override
	public double getOriginY() {
		return origin.longitude;
	}

	@Override
	public String getEpsg() {
		return "4326";
	}

}
