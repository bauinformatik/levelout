package org.opensourcebim.levelout;

import de.topobyte.osm4j.xml.output.OsmXmlOutputStream;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.proj4j.ProjCoordinate;
import org.opensourcebim.levelout.samples.OsmInteractive;
import org.opensourcebim.levelout.util.CoordinateConversion;
import org.opensourcebim.levelout.util.CoordinateConversion.CartesianPoint;
import org.opensourcebim.levelout.util.CoordinateConversion.GeodeticPoint;

import java.io.FileNotFoundException;
import java.io.IOException;

public class UtilityTests {
	

	@Test
	public void testCartesianToWgs84Origin() {
		CartesianPoint pointToTransform = new CartesianPoint(0, 0, 0);
		GeodeticPoint csOrigin = new GeodeticPoint(53.320555, -1.729000, 0);
		GeodeticPoint transformed = CoordinateConversion.convertCartesianToWGS84(csOrigin, pointToTransform);
		Assert.assertEquals(csOrigin.longitude, transformed.longitude, 0.000000001);
		Assert.assertEquals(csOrigin.latitude, transformed.latitude, 0.000000001);
		Assert.assertEquals(csOrigin.height, transformed.height, 0.000000001);
	}

	@Test
	public void testCartesianToWgs84BuildingSmart(){
		// origin WGS84 lat=56.336468, long=150.311068
		// rotation xAxis abscissa=0.990330045, ordinate=-0.138731399
		// point x=116.611, y=75.960

		// Map Grid of Australia Zone 56
		// Ref1 E = 333,780.622 N = 6,246,775.891 H = 97.457
		// Ref2 E = 333,906.644 N = 6,246,834.938 H = 98.291
		// Ref1 X = 0.000 Y = 0.000 Z = 0.000
		// Ref2 X = 116.611 Y = 75.960 Z = 0.834
		// Eastings = 333,780.622
		//Northings = 6,246,775.891
		//OrthogonalHeight = 97.457
		//XAxisAbscissa = 0.990330045
		//XAxisOrdinate = -0.138731399
		//Scale = 0.999998

		OsmInteractive.osmOutput = new OsmXmlOutputStream(System.out, true);
		OsmInteractive.ifclocalcoordtoglobalcoordv4(115.611, 75.960, 333780.622, 6246755.891, 0.990330045, -0.138731399, "epsg:28356");
		OsmInteractive.osmOutput.complete();
	}

	@Test
	public void writeNodeandWaydetails() throws IOException
	{
		OsmInteractive.osmOutput = new OsmXmlOutputStream(System.out, true);
		OsmInteractive.WriteNodeDetails(-1, 0, 0);
		OsmInteractive.WriteNodeDetails(-2, 6, 0);
		OsmInteractive.WriteNodeDetails(-3, 6, 6);
		OsmInteractive.WriteNodeDetails(-4, 0, 6);
		long [] nodeList = new long [] {-1,-2,-3,-4,-1};
		OsmInteractive.writeWayDetails(-1, nodeList, "indoortags1");
		OsmInteractive.osmOutput.complete();
	}

	@Test
	public void testifc2geolocations() throws FileNotFoundException {
		OsmInteractive.osmOutput = new OsmXmlOutputStream(System.out, true);
		ProjCoordinate wgsV2 = OsmInteractive.ifclocalcoord2globalcoordv2(116.611, 75.960, -0.13918031137,
				-33.90632062825244, 151.20215639320887, "epsg:28356");
		ProjCoordinate wgsV4 = OsmInteractive.ifclocalcoordtoglobalcoordv4(116.611, 75.960, 333780.62200000236,
				6246775.890999999, 0.990330045, -0.138731399, "epsg:28356");
		Assert.assertEquals(wgsV2.x, 151.203530034203, 0.000000001);
		Assert.assertEquals(wgsV2.y, -33.9058082188758, 0.000000001);
		Assert.assertEquals(wgsV4.x, 151.203530034203, 0.000000001);
		Assert.assertEquals(wgsV4.y, -33.9058082188758, 0.000000001);
		OsmInteractive.osmOutput.complete();

	}
}
