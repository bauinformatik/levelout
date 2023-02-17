package org.opensourcebim.levelout.intermediatemodel.geo;

import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateTransformFactory;

public abstract class CoordinateReference {
	static final CRSFactory crsFactory = new CRSFactory();
	static final CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();

	static String getEpsg(GeodeticPoint origin) {
		// TODO : Account for special cases: Norway and Svalbard
		int num = 0; // TODO throw  IllegalArgumentException if outside valid latitudes, should not be 32600 / 32700 in that case
		String numstr ="";
		if (origin.latitude <= 84 && (origin.latitude > -80)) {
			num = (int) ((Math.floor((origin.longitude + 180) / 6) % 60) + 1);
			
			if(num<=9)
			{
				numstr =  String.format("%01d" , num);
			}
			else
			{
				numstr = Integer.toString(num);
			}
		}
		return origin.latitude > 0 ? "326" + numstr : "327" + numstr; // TODO pad with 0 for zone numbers 1-9
	}

	public abstract GeodeticPoint cartesianToGeodetic(CartesianPoint cart);

}
