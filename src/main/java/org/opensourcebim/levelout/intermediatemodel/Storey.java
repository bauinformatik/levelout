package org.opensourcebim.levelout.intermediatemodel;

import java.util.Collections;
import java.util.List;

public class Storey {

	private final int level;
	private final List<Room> rooms;
	private final List<Door> doors;

	public Storey(int level, List<Room> rooms, List<Door> doors) {
		this.level = level;
		this.rooms = rooms;
		this.doors = doors;
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
}