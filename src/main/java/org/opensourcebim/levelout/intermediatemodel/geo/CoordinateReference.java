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

	private final double scale;

	protected CoordinateReference(double rotation, double scale) {
		a = Math.cos(rotation) * scale;
		b = Math.sin(rotation) * scale;
		this.scale = scale;
	}

	public abstract GeodeticPoint cartesianToGeodetic(CartesianPoint cart);

	public CartesianPoint rotateAndScale(CartesianPoint cart) {
		return new CartesianPoint (a * cart.x - b * cart.y, b * cart.x + a * cart.y);
	}

	public double scale (double height){
		return height * scale;  // This could become part of CartesianPoint
	}

	public abstract double getOriginX();
	public abstract double getOriginY();
	public abstract String getEpsg();

}
