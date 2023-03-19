
package org.opensourcebim.levelout.intermediatemodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Room {
	private static long highestId = 0;
	private final long id;
	private final List<Corner> corners;
	private Storey storey;

	public Room(List<Corner> corners) {
		this.id = ++highestId;
		this.corners = corners;
	}

	void setStorey(Storey storey) {
		if(this.storey!=null) {
			throw new IllegalArgumentException("no rooms with multiple storeys allowed"); // TODO allow for elevators etc., but then getZ musst be removed and z value determined in context
		}
		// TODO thin-walled model: corner reuse and segment splitting (using other rooms in storey )
		this.storey = storey;
	}
	public List<Double> computeCentroid() {
		if(corners.isEmpty()) return null;
		double minX = corners.get(0).getX();
		double minY = corners.get(0).getY();
		double maxX = minX, maxY = minY;

		// TODO: check formula for centroid calculation

		for (Corner node : corners) {
			if (node.getX() < minX) {
				minX = node.getX();
			} else if (node.getX() > maxX) {
				maxX = node.getX();
			}
			if (node.getY() < minY) {
				minY = node.getY();
			} else if (node.getY() > maxY) {
				maxY = node.getY();
			}
		}

		double centroidX = (minX + maxX) / 2;
		double centroidY = (minY + maxY) / 2;
		return List.of(centroidX, centroidY, storey.getZ());
	}

	public long getId(){
		return id;
	}

	public List<Corner> getCorners(){
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

}
