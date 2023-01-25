package org.opensourcebim.levelout.intermediatemodel;

import org.opensourcebim.levelout.util.CoordinateConversion;
import org.opensourcebim.levelout.util.CoordinateConversion.CartesianPoint;
import org.opensourcebim.levelout.util.CoordinateConversion.GeodeticPoint;
import org.opensourcebim.levelout.util.CoordinateConversion.ProjectedPoint;

public class MapConversion {

	
		int ifcVersion;
		
		public static GeodeticPoint getMapparameters(int ifcVersion, CartesianPoint pointToTransform)
		{
			GeodeticPoint csOrigin = new GeodeticPoint(53.320555, -1.729000, 0);
			ProjectedPoint csOriginprj = new ProjectedPoint(333780.622, 6246775.891, 0);
			double rotation =-0.13918031137;
			return  (ifcVersion == 2)
				? CoordinateConversion.originWGS84viaUTM(pointToTransform, csOrigin, rotation)
				: CoordinateConversion.originArbitraryCRS(pointToTransform, csOriginprj, 0.990330045, -0.138731399, "epsg:28356");
		}

		public MapConversion(int ifcVersion) {
			super();
			this.ifcVersion = ifcVersion;
		}


}
