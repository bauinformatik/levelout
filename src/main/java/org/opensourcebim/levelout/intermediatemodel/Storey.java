package org.opensourcebim.levelout.intermediatemodel;

import java.util.Collections;
import java.util.List;

public class Storey {

	private int level;
	private long id;
	private final List<Room> polygonList;
	private final List<Door> DoorList;

	public Storey(int level, int id, List<Room> polygonList, List<Door> doorList) {
		this.level = level;
		this.id = id;
		this.polygonList = polygonList;
		this.DoorList = doorList;
	}

	public List<Room> getPolygonList() {
		return Collections.unmodifiableList(polygonList);
	}
	public List<Door> getDoorList() {
		return Collections.unmodifiableList(DoorList);
	}

}