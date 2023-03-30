package org.opensourcebim.levelout.intermediatemodel.geo;

import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateTransformFactory;

public abstract class CoordinateReference {
	static final CRSFactory crsFactory = new CRSFactory();
	static final CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();

	static String getEpsg(GeodeticPoint origin) {
		// TODO : Account for special cases: Norway and Svalbard
		if (origin.latitude <= -80 || origin.latitude > 84) {
			throw new IllegalArgumentException("Latitude outside of valid range -80..84.");
		} else {
			int num = (int) ((Math.floor((origin.longitude + 180) / 6) % 60) + 1);
			String numstr = String.format("%02d", num);
			return origin.latitude > 0 ? "326" + numstr : "327" + numstr;
		}
	}

	static double getGridconvergence(GeodeticPoint origin, String epsg) {

		double Gridnum = Double.valueOf(epsg.substring(8));
		double longitudeDiff = origin.longitude - (6 * Gridnum - 183);
		double convergenceangle = Math.atan(Math.tan(longitudeDiff) * Math.sin(origin.latitude));
		return convergenceangle;

	}

	public abstract GeodeticPoint cartesianToGeodetic(CartesianPoint cart);

}
