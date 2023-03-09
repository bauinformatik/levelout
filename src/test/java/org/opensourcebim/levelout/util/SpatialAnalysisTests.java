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
	private final Corner corner1 = new Corner(0, 0, 0, 0);
	private final Corner corner2 = new Corner(1, 6, 0, 0);
	private Room room1;
	private Room room2;
	private Door door;

	@Before
	public void setupModel() {
		room1 = new Room(1, Arrays.asList(new Corner(2, 0, 0, 0), new Corner(3, 6, 0, 0),
				new Corner(4, 6, 6, 0), new Corner(5, 0, 6, 0)));
		room2 = new Room(2, Arrays.asList(new Corner(2, 6, 0, 0), new Corner(3, 10, 0, 0),
				new Corner(4, 10, 6, 0), new Corner(5, 6, 6, 0)));
		door = new Door(6, Arrays.asList(new Corner(7, 6, 1, 0), new Corner(8, 6, 2, 0)));
	}

	@Test

	public void testDoorOnSegment() {
		Door door = new Door(1, Arrays.asList(new Corner(2, 1, 0, 0), new Corner(3, 2, 0, 0)));
		Assert.assertTrue(SpatialAnalysis.isDoorOnSegment(door, corner1, corner2));
	}

	public void testAdjacentRoom() {

		List<Room> rooms = List.of(room1, room2);
		List<List<Long>> rooms2 = SpatialAnalysis.adjacentRooms(rooms);

	}

	@Test
	public void testDoorParallelNotOnSegment() {
		Door door = new Door(1, Arrays.asList(new Corner(2, 1, 6, 0), new Corner(3, 2, 6, 0)));
		Assert.assertFalse(SpatialAnalysis.isDoorOnSegment(door, corner1, corner2));
	}

	@Test
	public void testDoorinRoom() {
		Door door = new Door(1, Arrays.asList(new Corner(2, 1, 6, 0), new Corner(3, 2, 6, 0)));
		Door door2 = new Door(2, Arrays.asList(new Corner(4, 1, 7, 0), new Corner(5, 2, 7, 0)));
		Door door3 = new Door(6, Arrays.asList(new Corner(7, 6, 1, 0), new Corner(8, 6, 2, 0)));
		Door door5 = new Door(7, Arrays.asList(new Corner(9, 6, 1, 0), new Corner(10, 6, 11, 0)));
		Door door6 = new Door(8, Arrays.asList(new Corner(11, 6, 0, 0), new Corner(12, 6, 1, 0)));
		Assert.assertTrue(SpatialAnalysis.isDoorInRoom(room1, door));
		Assert.assertFalse(SpatialAnalysis.isDoorInRoom(room1, door2));
		Assert.assertTrue(SpatialAnalysis.isDoorInRoom(room2, door3));
		Assert.assertFalse(SpatialAnalysis.isDoorInRoom(room2, door5));
		Assert.assertTrue(SpatialAnalysis.isDoorInRoom(room2, door6));		
	}
	@Test
	public void testanalyzeRooms() {
		Door door = new Door(1, Arrays.asList(new Corner(2, 1, 6, 0), new Corner(3, 2, 6, 0)));
		Door door2 = new Door(2, Arrays.asList(new Corner(4, 1, 7, 0), new Corner(5, 2, 7, 0)));
		Door door3 = new Door(6, Arrays.asList(new Corner(7, 6, 1, 0), new Corner(8, 6, 2, 0)));
		Door door5 = new Door(7, Arrays.asList(new Corner(9, 6, 1, 0), new Corner(10, 6, 11, 0)));
		Door door6 = new Door(8, Arrays.asList(new Corner(11, 6, 0, 0), new Corner(12, 6, 1, 0)));
		List<Door> doors = Arrays.asList(door,door2,door3,door5,door6);
		List<Room> rooms = Arrays.asList(room1,room2);
		Map<Door, List<Room>> receivedresult = SpatialAnalysis.analyzeRooms(rooms, doors);
		Map<Door, List<Room>> expectedresult = new HashMap<>();
		expectedresult.put(door6, Arrays.asList(room1,room2));
		expectedresult.put(door3, Arrays.asList(room1,room2));
		expectedresult.put(door, Arrays.asList(room1));
		
		Assert.assertEquals(expectedresult, receivedresult);
	}

	@Test
	public void testColinear() {
		Assert.assertTrue(
				SpatialAnalysis.isColinear(new Corner(1, 0, 0, 0), new Corner(2, 6, 0, 0), new Corner(3, 2, 0, 0)));
	}

	@Test
	public void testNotColinear() {
		Assert.assertFalse(
				SpatialAnalysis.isColinear(new Corner(1, 0, 0, 0), new Corner(2, 6, 0, 0), new Corner(3, 2, 6, 0)));
	}

	@Test
	public void testInternalDoor() {
		Building building = new Building(9, List.of(new Storey(0, List.of(room1), List.of(door))));
	}

	@Test
	public void testExternalDoor() {
		Building building = new Building(9, List.of(new Storey(0, List.of(room1, room2), List.of(door))));
	}
}
