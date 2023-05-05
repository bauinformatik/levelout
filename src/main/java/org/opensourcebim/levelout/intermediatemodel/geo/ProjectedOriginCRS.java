package org.opensourcebim.levelout.intermediatemodel.geo;

import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.ProjCoordinate;

import java.io.Serializable;

public class ProjectedOriginCRS extends CoordinateReference implements Serializable {
	private static final long serialVersionUID = 4685496957100410339L;
	private final ProjectedPoint origin;
	private final String epsg;
	private final double scale;

	public ProjectedOriginCRS(ProjectedPoint origin, double xAxisAbscissa, double xAxisOrdinate, double scale,
			String epsg) {
		super(Math.atan2(xAxisOrdinate, xAxisAbscissa), scale);
		// TODO just normalize to avoid large products later, no need for trigonometric functions
		this.scale = scale;
		this.origin = origin;
		this.epsg = epsg;
	}

	public ProjectedOriginCRS(ProjectedPoint origin, double xAxisAbscissa, double xAxisOrdinate, String epsg) {
		this(origin, xAxisAbscissa, xAxisOrdinate, 1, epsg);
	}

	@Override
	public GeodeticPoint cartesianToGeodetic(CartesianPoint cart) {
		CartesianPoint rotatedAndScaled = rotateAndScale(cart);
		double eastingsmap = rotatedAndScaled.x + origin.eastings * scale;
		double northingsmap = rotatedAndScaled.y + origin.northings * scale;

		CoordinateReferenceSystem wgs84 = crsFactory.createFromName("epsg:4326");
		CoordinateReferenceSystem originCRS = crsFactory.createFromName(epsg);

		CoordinateTransform originCrsToWgs84 = ctFactory.createTransform(originCRS, wgs84);
		ProjCoordinate resultcoord = originCrsToWgs84
				.transform(new ProjCoordinate(eastingsmap, northingsmap ), new ProjCoordinate());

		return new GeodeticPoint(resultcoord.y, resultcoord.x);
	}

	@Override
	public double getOriginX() {
		return origin.northings;
	}

	@Override
	public double getOriginY() {
		return origin.eastings;
	}

	@Override
	public String getEpsg() {
		return epsg;
	}

}
