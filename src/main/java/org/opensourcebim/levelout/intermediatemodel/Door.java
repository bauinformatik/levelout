package org.opensourcebim.levelout.intermediatemodel;

import org.opensourcebim.levelout.intermediatemodel.geo.CoordinateReference;
import org.opensourcebim.levelout.util.Geometry;
import org.opensourcebim.levelout.util.Topology;

import java.io.Serializable;
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
	private final String name;

	private final boolean closable;

	public Door(String name, List<Corner> corners) {
		this(name, corners, true);
	}
	public  Door(String name, List<Corner> corners, boolean closable) {
		this.id = ++highestId;
		this.name = name;
		this.corners = Topology.withoutCollinearCorners(corners);
		this.closable = closable;
	}

	void setStorey(Storey storey) {
		if (this.storey != null) {
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

	public List<Double> computeCentroid() {
		Corner doorcentroid = Geometry.computeCentroid(corners);
		return List.of(doorcentroid.getX(), doorcentroid.getY(), storey == null ? 0 : storey.getZ());
	}

	public List<Double> asCoordinateList() {
		return Geometry.asCoordinateList(corners, storey == null ? 0 : storey.getZ());
	}
	

	public List<Double> asCoordinateList(CoordinateReference crs) {
		return Geometry.asCoordinateList(corners, storey == null ? 0 : storey.getZ(), crs);
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

	public Storey getStorey() {
		return storey;
	}

	public boolean isExternal() {
		return external;
	}

	static void resetCounter() {
		highestId = 0;
	}

	public String getName() {
		return name;
	}

	public boolean isClosable() {
		return closable;
	}

	public boolean hasGeometry(){
		return !corners.isEmpty();
	}
}
