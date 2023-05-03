package org.opensourcebim.levelout.intermediatemodel.geo;

import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateTransformFactory;

public abstract class CoordinateReference {
	static final CRSFactory crsFactory = new CRSFactory();
	static final CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();

	public abstract GeodeticPoint cartesianToGeodetic(CartesianPoint cart);
	
	public abstract double getOriginX();
	public abstract double getOriginY();
	public abstract String getEpsgvalue();
	public abstract double getOriginZ();
	
}
