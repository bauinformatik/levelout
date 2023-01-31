package org.opensourcebim.levelout.util;

import org.junit.Assert;
import org.junit.Test;
import org.opensourcebim.levelout.intermediatemodel.Corner;
import org.opensourcebim.levelout.intermediatemodel.Door;

import java.util.Arrays;

public class SpatialAnalysisTests {
	private Corner corner1 = new Corner(0,0, 0, 0);
	private Corner corner2 = new Corner(1, 6,0, 0);
	@Test
	public void testCollinearity(){
		Door door = new Door(1,"dummy", Arrays.asList(
			new Corner(2, 1,0,0),
			new Corner(3, 2,0,0)
		));
		Assert.assertTrue(SpatialAnalysis.testColinear(door, corner1, corner2));
	}
	@Test
	public void testParallelNonCollinear(){
		Door door = new Door(1,"dummy", Arrays.asList(
			new Corner(2, 1,6,0),
			new Corner(3, 2,6,0)
		));
		Assert.assertFalse(SpatialAnalysis.testColinear(door, corner1, corner2));
	}
}
