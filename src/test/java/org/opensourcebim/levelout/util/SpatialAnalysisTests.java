package org.opensourcebim.levelout.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opensourcebim.levelout.intermediatemodel.*;

import java.util.*;

public class SpatialAnalysisTests {
	private Room room1;
	private Room room2;
	private Door door1;
	private Door door2;
	private Door door3;
	private Door door4;
	private Door door5;
	private Door door6;

	@Before
	public void setupModel() {
		/*
		   2      4
		+--1------+--+
		|         |   \
		|         3    6
		|         |     \
		+---------+5-----+

		*/
		room1 = new Room(
				Arrays.asList(new Corner(0, 0), new Corner(6, 0), new Corner(6, 6), new Corner(0, 6)));
		room2 = new Room(
				Arrays.asList(new Corner(6, 0), new Corner(10, 0), new Corner(7, 6), new Corner(6, 6)));
		door1 = new Door(Arrays.asList(new Corner(1, 6), new Corner(2, 6)));
		door2 = new Door(Arrays.asList(new Corner(1, 7), new Corner(2, 7)));
		door3 = new Door(Arrays.asList(new Corner(6, 1), new Corner(6, 2)));
		door4 = new Door(Arrays.asList(new Corner(6, 5), new Corner(6, 7)));
		door5 = new Door(Arrays.asList(new Corner(6, 0), new Corner(7, 0)));
		door6 = new Door(Arrays.asList(new Corner(9, 2), new Corner(8, 4)));
	}

	@Test
	public void testDoorinRoom() {
		Assert.assertTrue(SpatialAnalysis.isDoorInRoom(room1, door1));
		Assert.assertFalse(SpatialAnalysis.isDoorInRoom(room1, door2));
		Assert.assertTrue(SpatialAnalysis.isDoorInRoom(room2, door3));
		Assert.assertFalse(SpatialAnalysis.isDoorInRoom(room2, door4));
		Assert.assertTrue(SpatialAnalysis.isDoorInRoom(room2, door5));
		Assert.assertFalse(SpatialAnalysis.isDoorInRoom(room1, door5));
		Assert.assertTrue(SpatialAnalysis.isDoorInRoom(room2, door6));
	}

	@Test
	public void testanalyzeRooms() {
		List<Door> doors = Arrays.asList(door1, door2, door3, door4, door5, door6);
		List<Room> rooms = Arrays.asList(room1, room2);
		Map<Door, List<Room>> actualResult = SpatialAnalysis.analyzeRooms(rooms, doors);
		Map<Door, List<Room>> expectedResult = new HashMap<>();
		expectedResult.put(door6, Collections.singletonList(room2));
		expectedResult.put(door5, Collections.singletonList(room2));
		expectedResult.put(door3, Arrays.asList(room1, room2));
		expectedResult.put(door1, Collections.singletonList(room1));
		Assert.assertEquals(expectedResult, actualResult);
	}

	@Test
	public void testColinear() {
		Assert.assertTrue(
			SpatialAnalysis.isCollinear(new Corner(0, 0), new Corner(6, 0), new Corner(2, 0)));
	}

	@Test
	public void testNotColinear() {
		Assert.assertFalse(
			SpatialAnalysis.isCollinear(new Corner(0, 0), new Corner(6, 0), new Corner(2, 6)));
	}

	@Test
	public void testColinearSlanted(){
		Assert.assertTrue(SpatialAnalysis.isCollinear( new Corner(10,0), new Corner(9,2), new Corner(8,4)));
	}

	@Test
	public void testExternalDoor() {
		Map<Door, List<Room>> result = SpatialAnalysis.analyzeRooms(List.of(room1), List.of(door3));
		Assert.assertEquals(1, result.get(door3).size());
	}

	@Test
	public void testInternalDoor() {
		Map<Door, List<Room>> result = SpatialAnalysis.analyzeRooms(List.of(room1, room2), List.of(door3));
		Assert.assertEquals(2, result.get(door3).size());
	}

	@Test
	public void testDoorOnSegment() {
		Assert.assertTrue(SpatialAnalysis.isDoorOnLine(door1, new Corner(6, 6), new Corner(0, 6)));
	}

	@Test
	public void testDoorParallelNotOnSegment() {
		Assert.assertFalse(SpatialAnalysis.isDoorOnLine(door2, new Corner(6, 6), new Corner(0, 6)));
	}

	@Test
	public void testDoorSlantedSegment(){
		Assert.assertTrue(SpatialAnalysis.isDoorOnLine(door6, new Corner(10, 0), new Corner(7, 6)));
	}
}
