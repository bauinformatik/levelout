package org.opensourcebim.levelout.intermediatemodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Door {

	
	
	private final String type;
	private final List<Corner> corners;
	//private final List<Room> connectedRooms;

	public Door(long id, String type, List<Corner> corners) {
		super();
		this.id = id;
		this.type = type;
		this.corners = corners;
	}


	private final long id;
	public long getId() {
		return id;
	}
	public String getType() {
		return type;
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
}
