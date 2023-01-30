package org.opensourcebim.levelout.intermediatemodel.geo;

import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.ProjCoordinate;

public class ProjectedOriginCRS extends CoordinateReference {
	private final ProjectedPoint origin;
	private final double xAxisAbscissa;
	private final double xAxisOrdinate;
	private final String epsg;

	public ProjectedOriginCRS(ProjectedPoint origin, double xAxisAbscissa, double xAxisOrdinate, String epsg) {
		this.origin = origin;
		this.xAxisAbscissa = xAxisAbscissa;
		this.xAxisOrdinate = xAxisOrdinate;
		this.epsg = epsg;
	}

	@Override
	public GeodeticPoint cartesianToGeodetic(CartesianPoint cart) {

		double rotation = Math.atan2(xAxisOrdinate, xAxisAbscissa);
		double a = Math.cos(rotation);
		double b = Math.sin(rotation);
		// TODO just normalize to avoid large products later, no need for trigonometric functions

		double eastingsmap = (a * cart.x) - (b * cart.y) + origin.eastings;
		double northingsmap = (b * cart.x) + (a * cart.y) + origin.northings;

		CoordinateReferenceSystem wgs84 = crsFactory.createFromName("epsg:4326");
		CoordinateReferenceSystem originCRS = crsFactory.createFromName(epsg);

		CoordinateTransform originCrsToWgs84 = ctFactory.createTransform(originCRS, wgs84);
		ProjCoordinate resultcoord = originCrsToWgs84.transform(new ProjCoordinate(eastingsmap, northingsmap),
			new ProjCoordinate());

		return new GeodeticPoint(resultcoord.y, resultcoord.x, origin.height);
	}
}
