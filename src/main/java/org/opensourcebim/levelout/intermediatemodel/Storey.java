package org.opensourcebim.levelout.intermediatemodel;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class Storey implements Serializable {

	private static final long serialVersionUID = 224912596112948321L;
	private final int level;
	private final List<Room> rooms;
	private final List<Door> doors;
	private final double elevation;

	private final String name;

	public Storey(int level, double elevation, String name, List<Room> rooms, List<Door> doors) {
		this.level = level;
		this.elevation = elevation;
		this.name = name;
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

	public String getName() {
		return  name;
	}
}