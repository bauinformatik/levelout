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
import java.util.Arrays;
import java.util.Scanner;

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
