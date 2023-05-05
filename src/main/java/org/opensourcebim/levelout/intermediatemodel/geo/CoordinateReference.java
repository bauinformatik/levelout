package org.opensourcebim.levelout.intermediatemodel.geo;

import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateTransformFactory;

public abstract class CoordinateReference {
	static final CRSFactory crsFactory = new CRSFactory();
	static final CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
	private final double a;
	private final double b;

	protected CoordinateReference(double rotation, double scale) {
		a = Math.cos(rotation) * scale;
		b = Math.sin(rotation) * scale;
	}

	public abstract GeodeticPoint cartesianToGeodetic(CartesianPoint cart);

	public CartesianPoint rotateAndScale(CartesianPoint cart) {
		return new CartesianPoint (a * cart.x - b * cart.y, b * cart.x + a * cart.y, 0);
	}

	public abstract double getOriginX();
	public abstract double getOriginY();
	public abstract String getEpsgvalue();
	public abstract double getOriginZ();
	
}
