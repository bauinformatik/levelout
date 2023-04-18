package org.opensourcebim.levelout.intermediatemodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Storey implements Serializable {

	private static final long serialVersionUID = 224912596112948321L;
	private final int level;
	private final List<Room> rooms = new ArrayList<>();
	private final List<Door> doors = new ArrayList<>();
	private final double elevation;

	private final String name;

	public Storey(int level, double elevation, String name){
		this.level = level;
		this.elevation = elevation;
		this.name = name;
	}
	public Storey(int level, double elevation, String name, List<Room> rooms, List<Door> doors) {
		this(level, elevation, name);
		this.addRooms( rooms.toArray(new Room[0]) );
		this.addDoors( doors.toArray(new Door[0]) );
	}

	public void addRooms(Room...  rooms){
		for (Room room : rooms) {
			room.setStorey(this);
			this.rooms.add(room);
		}
	}

	public void addDoors(Door... doors){
		for(Door door: doors){
			door.setStorey(this);
			this.doors.add(door);
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