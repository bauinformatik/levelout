package org.opensourcebim.levelout.intermediatemodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Door {

	private final List<Corner> corners;
	private boolean external = false;
	private Room room1;
	private Room room2;
	//private final List<Room> connectedRooms;

	public Door(long id, List<Corner> corners) {
		super();
		this.id = id;
		this.corners = corners;
	}


	private final long id;
	public long getId() {
		return id;
	}

	public List<Corner> getCorners() {
		return Collections.unmodifiableList(corners);
	}

	public List<Double> asCoordinateList() {
		List<Double> coordinates = new ArrayList<>();
		for (Corner corner: corners){
			coordinates.addAll(corner.asCoordinateList());
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
}
