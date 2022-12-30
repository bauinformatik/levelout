
package org.opensourcebim.levelout.intermediatemodel;

import java.util.Collections;
import java.util.List;

public class Room {
	private final long id;
	private final String type;
	private final int dimension;
	private final List<Corner> nodeList;

	public Room(long id, String type, int dimension, List<Corner> nodeList) {
		this.id = id; // TODO auto-increment
		this.type = type; // TODO change to enum if needed at all
		this.dimension = dimension; // TODO static value, always 3
		this.nodeList = nodeList;
	}

	public List<Double> computeCentroid() {
		// computing centroid from nodeslist
		double minX = nodeList.get(0).getX();
		double minY = nodeList.get(0).getY();
		double minZ = nodeList.get(0).getZ();
		double maxX = minX, maxY = minY, maxZ = minZ;

		// TODO: check formula for centroid calculation

		for (Corner node : nodeList) {
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

	public int getDimension() {
		return dimension;
	}

	public String getType() {
		return type;
	}
	public long getId(){
		return id;
	}

	public List<Corner> getRooms(){
		return Collections.unmodifiableList(nodeList);
	}
}
