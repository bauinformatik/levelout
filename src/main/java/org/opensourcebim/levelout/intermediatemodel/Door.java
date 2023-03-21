package org.opensourcebim.levelout.intermediatemodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Door implements Serializable {

	private static final long serialVersionUID = 563629940339855368L;
	private static long highestId = 0;
	private final List<Corner> corners;
	private boolean external = false;
	private final long id;
	private Room room1;
	private Room room2;
	private Storey storey;

	public Door(List<Corner> corners) {
		this.id = ++highestId;
		this.corners = corners;
	}

	void setStorey(Storey storey) {
		if(this.storey!=null) {
			throw new IllegalArgumentException("no doors with multiple storeys allowed");
		}
		this.storey = storey;
	}
	public long getId() {
		return id;
	}

	public List<Corner> getCorners() {
		return Collections.unmodifiableList(corners);
	}

	public List<Double> asCoordinateList() {
		List<Double> coordinates = new ArrayList<>();
		for (Corner corner: corners){
			coordinates.add(corner.getX());
			coordinates.add(corner.getY());
			coordinates.add(storey == null ? 0 : storey.getZ());
		}
		return coordinates;
	}

	public void setExternal(Room room) {
		external = true;
		room1 = room;
	}

	public void setInternal(Room room1, Room room2) {
		external = false;
		this.room1 = room1;
		this.room2 = room2;
	}

	public Room getRoom1() {
		return room1;
	}

	public Room getRoom2() {
		return room2;
	}

	public boolean isExternal() {
		return external;
	}

	static void resetCounter() {
		highestId = 0;
	}
}
