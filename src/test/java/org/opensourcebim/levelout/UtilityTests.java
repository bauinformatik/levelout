package org.opensourcebim.levelout;

import de.topobyte.osm4j.xml.output.OsmXmlOutputStream;
import org.junit.Assert;
import org.junit.Test;
import org.opensourcebim.levelout.samples.OsmInteractive;
import org.opensourcebim.levelout.util.CoordinateConversion;
import org.opensourcebim.levelout.util.CoordinateConversion.CartesianPoint;
import org.opensourcebim.levelout.util.CoordinateConversion.GeodeticPoint;

import java.util.Scanner;

public class UtilityTests {
	@Test
	public void testGetGeoLocations(){
		OsmInteractive.osmOutput = new OsmXmlOutputStream(System.out, true);
		OsmInteractive.getGeolocations(-1, 50.9773653, 11.34782554233, 0, 0);
		OsmInteractive.getGeolocations(-2, 50.9773653, 11.34782554233, 6, 90);
		OsmInteractive.getGeolocations(-3, 50.9773653, 11.34782554233, 6, 0);
		OsmInteractive.getGeolocations(-4, 50.9773653, 11.34782554233, 6, 45);
		OsmInteractive.osmOutput.complete();
	}
	@Test
	public void testInteractive(){
		OsmInteractive.osmOutput = new OsmXmlOutputStream(System.out, true);
		OsmInteractive.sc = new Scanner("-1 0 0\n-2 0 6\n-3 6 6\n-4 6 0\n");
		OsmInteractive.readAndWriteNodeDetails(4);
		OsmInteractive.osmOutput.complete();
	}

	@Test
	public void testJCoord(){
		OsmInteractive.wgstoProjectedCoorsys(53.320555, -1.729000);
		OsmInteractive.projectedtoWgsCoorsys(5908683.746009846, 584662.2085495128, 30, 'U');
		OsmInteractive.ifclocaltomapcoord(6,6);
	}

	@Test
	public void testCartesianToWgs84Origin(){
		CartesianPoint pointToTransform = new CartesianPoint(0,0,0);
		GeodeticPoint csOrigin = new GeodeticPoint(53.320555, -1.729000, 0);
		GeodeticPoint transformed = CoordinateConversion.convertCartesianToWGS84(csOrigin, pointToTransform);
		Assert.assertEquals(csOrigin.longitude, transformed.longitude, 0.000000001);
		Assert.assertEquals(csOrigin.latitude, transformed.latitude, 0.000000001);
		Assert.assertEquals(csOrigin.height, transformed.height, 0.000000001);
	}
}
