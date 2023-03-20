package org.opensourcebim.levelout.intermediatemodel;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class Storey implements Serializable {

	private static final long serialVersionUID = 224912596112948321L;
	private final int level;
	private final List<Room> rooms;
	private final List<Door> doors;
	private double elevation;

	public Storey(int level, double elevation, List<Room> rooms, List<Door> doors) {
		this.level = level;
		this.elevation = elevation;
		this.rooms = rooms;
		this.doors = doors;
		for(Room room: rooms){
			room.setStorey(this);
		}
		for(Door door: doors){
			door.setStorey(this);
		}
	}

	public List<Room> getRooms() {
		return Collections.unmodifiableList(rooms);
	}
	public List<Door> getDoors() {
		return Collections.unmodifiableList(doors);
	}

	public int getLevel() {
		return level;
	}

	public double getZ() {
		return elevation;
	}
}