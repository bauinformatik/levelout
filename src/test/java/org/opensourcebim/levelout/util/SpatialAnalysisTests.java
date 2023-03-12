package org.opensourcebim.levelout.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opensourcebim.levelout.intermediatemodel.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpatialAnalysisTests {
	private final Corner corner1 = new Corner(0, 0, 0);
	private final Corner corner2 = new Corner(6, 0, 0);
	private Room room1;
	private Room room2;
	private Door door;

	@Before
	public void setupModel() {
		room1 = new Room(
				Arrays.asList(new Corner(0, 0, 0), new Corner(6, 0, 0), new Corner(6, 6, 0), new Corner(0, 6, 0)));
		room2 = new Room(
				Arrays.asList(new Corner(6, 0, 0), new Corner(10, 0, 0), new Corner(10, 6, 0), new Corner(6, 6, 0)));
		door = new Door(Arrays.asList(new Corner(6, 1, 0), new Corner(6, 2, 0)));
	}

	@Test
	public void testDoorinRoom() {
		Door door = new Door(Arrays.asList(new Corner(1, 6, 0), new Corner(2, 6, 0)));
		Door door2 = new Door(Arrays.asList(new Corner(1, 7, 0), new Corner(2, 7, 0)));
		Door door3 = new Door(Arrays.asList(new Corner(6, 1, 0), new Corner(6, 2, 0)));
		Door door5 = new Door(Arrays.asList(new Corner(6, 1, 0), new Corner(6, 11, 0)));
		Door door6 = new Door(Arrays.asList(new Corner(6, 0, 0), new Corner(6, 1, 0)));
		Assert.assertTrue(SpatialAnalysis.isDoorInRoom(room1, door));
		Assert.assertFalse(SpatialAnalysis.isDoorInRoom(room1, door2));
		Assert.assertTrue(SpatialAnalysis.isDoorInRoom(room2, door3));
		Assert.assertFalse(SpatialAnalysis.isDoorInRoom(room2, door5));
		Assert.assertTrue(SpatialAnalysis.isDoorInRoom(room2, door6));
	}

	@Test
	public void testanalyzeRooms() {
		Door door = new Door(Arrays.asList(new Corner(1, 6, 0), new Corner(2, 6, 0)));
		Door door2 = new Door(Arrays.asList(new Corner(1, 7, 0), new Corner(2, 7, 0)));
		Door door3 = new Door(Arrays.asList(new Corner(6, 1, 0), new Corner(6, 2, 0)));
		Door door5 = new Door(Arrays.asList(new Corner(6, 1, 0), new Corner(6, 11, 0)));
		Door door6 = new Door(Arrays.asList(new Corner(6, 0, 0), new Corner(6, 1, 0)));
		List<Door> doors = Arrays.asList(door, door2, door3, door5, door6);
		List<Room> rooms = Arrays.asList(room1, room2);
		Map<Door, List<Room>> receivedresult = SpatialAnalysis.analyzeRooms(rooms, doors);
		Map<Door, List<Room>> expectedresult = new HashMap<>();
		expectedresult.put(door6, Arrays.asList(room1, room2));
		expectedresult.put(door3, Arrays.asList(room1, room2));
		expectedresult.put(door, Arrays.asList(room1));

		Assert.assertEquals(expectedresult, receivedresult);
	}

}
