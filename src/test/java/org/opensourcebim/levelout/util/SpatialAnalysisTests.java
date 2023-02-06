package org.opensourcebim.levelout.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opensourcebim.levelout.intermediatemodel.*;

import java.util.Arrays;
import java.util.List;

public class SpatialAnalysisTests {
	private final Corner corner1 = new Corner(0,0, 0, 0);
	private final Corner corner2 = new Corner(1, 6,0, 0);
	private Room room1;
	private Room room2;
	private Door door;

	@Before
	public void setupModel() {
		room1 = new Room(1, "dummy", Arrays.asList(
			new Corner(2, 0, 0, 0),
			new Corner(3, 6, 0, 0),
			new Corner(4, 6, 6, 0),
			new Corner(5, 0, 6, 0)
		));
		room2 = new Room(2, "dummy", Arrays.asList(
			new Corner(2, 6, 0, 0),
			new Corner(3, 10, 0, 0),
			new Corner(4, 10, 6, 0),
			new Corner(5, 6, 6, 0)
		));
		door = new Door(6, "dummy", Arrays.asList(
			new Corner(7, 6, 1, 0), new Corner(8, 6, 2, 0))
		);
	}

	@Test
	public void testDoorOnSegment(){
		Door door = new Door(1,"dummy", Arrays.asList(
			new Corner(2, 1,0,0),
			new Corner(3, 2,0,0)
		));
		Assert.assertTrue(SpatialAnalysis.isDoorOnSegment(door, corner1, corner2));
	}
	@Test
	public void testDoorParallelNotOnSegment(){
		Door door = new Door(1,"dummy", Arrays.asList(
			new Corner(2, 1,6,0),
			new Corner(3, 2,6,0)
		));
		Assert.assertFalse(SpatialAnalysis.isDoorOnSegment(door, corner1, corner2));
	}

	@Test
	public void testColinear(){
		Assert.assertTrue(SpatialAnalysis.isColinear(
			new Corner(1,0,0, 0),
			new Corner(2, 6, 0, 0),
			new Corner(3, 2, 0, 0)
		));
	}
    @Test
	public void testNotColinear(){
		Assert.assertFalse(SpatialAnalysis.isColinear(
			new Corner(1, 0,0,0),
			new Corner(2, 6, 0, 0),
			new Corner(3, 2, 6,0)
		));
	}

	@Test
	public void testInternalDoor(){
		Building building = new Building(9, List.of(new Storey(0, List.of(room1), List.of(door))));
	}
	@Test
	public void testExternalDoor() {
		Building building = new Building(9, List.of(new Storey(0, List.of(room1, room2), List.of(door))));
	}
}
