package org.opensourcebim.levelout.intermediatemodel.geo;

import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateTransformFactory;

import java.io.Serializable;

public abstract class CoordinateReference implements Serializable {
	private static final long serialVersionUID = -6697679041586377603L;
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
		double x = a * cart.x - b * cart.y;
		double y = b * cart.x + a * cart.y;
		return new CartesianPoint (x, y, 0);
	}

	public abstract double getOriginX();
	public abstract double getOriginY();
	public abstract String getEpsg();

}
