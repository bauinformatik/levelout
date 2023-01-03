
package org.opensourcebim.levelout.intermediatemodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Room {
	private final long id;
	private final String type;
	private final List<Corner> corners;

	public Room(long id, String type, List<Corner> corners) {
		this.id = id; // TODO auto-increment
		this.type = type; // TODO change to enum if needed at all
		this.corners = corners;
	}

	public List<Double> computeCentroid() {
		double minX = corners.get(0).getX();
		double minY = corners.get(0).getY();
		double minZ = corners.get(0).getZ();
		double maxX = minX, maxY = minY, maxZ = minZ;

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
			if (node.getZ() < minZ) {
				minZ = node.getZ();
			} else if (node.getZ() > maxZ) {
				maxZ = node.getZ();
			}
		}

		double centroidX = (minX + maxX) / 2;
		double centroidY = (minY + maxY) / 2;
		double centroidZ = (minZ + maxZ) / 2;
		return List.of(centroidX, centroidY, centroidZ);
	}

	public String getType() {
		return type;
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
			coordinates.addAll(corner.asCoordinateList());
		}
		return coordinates;
	}
}
