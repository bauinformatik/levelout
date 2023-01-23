package org.opensourcebim.levelout.intermediatemodel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.opensourcebim.levelout.util.CoordinateConversion;
import org.opensourcebim.levelout.util.CoordinateConversion.CartesianPoint;
import org.opensourcebim.levelout.util.CoordinateConversion.GeodeticPoint;
import org.opensourcebim.levelout.util.CoordinateConversion.ProjectedPoint;

import de.topobyte.osm4j.xml.output.OsmXmlOutputStream;

public class MapConversion {

	
		int ifcVersion;
		
		public static Object getMapparameters(int ifcVersion, CartesianPoint pointToTransform)
		{
			GeodeticPoint transformedgp;
			ProjectedPoint transformedpp;
			GeodeticPoint csOrigin = new GeodeticPoint(53.320555, -1.729000, 0);
			ProjectedPoint csOriginprj = new ProjectedPoint(333780.622, 6246775.891, 0);
			double rotation =-0.13918031137;
			if (ifcVersion == 2)
			{
				return transformedgp = CoordinateConversion.CartesianWithWgs84OriginToWgs84O(csOrigin, pointToTransform,rotation);
			}
			else
			{
				return transformedpp = CoordinateConversion.CartesianToWgs84BuildingSmart(pointToTransform, csOriginprj,
						0.990330045, -0.138731399, "epsg:28356");
			}
			
		}


		public MapConversion(int ifcVersion) {
			super();
			this.ifcVersion = ifcVersion;
		}


}
