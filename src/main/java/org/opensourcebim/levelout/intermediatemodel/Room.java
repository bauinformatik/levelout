
package org.opensourcebim.levelout.intermediatemodel;

import org.opensourcebim.levelout.util.Geometry;
import org.opensourcebim.levelout.util.Centroid;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class Room implements Serializable {
	private static final long serialVersionUID = -1615263281736055071L;
	private static long highestId = 0;
	private final long id;
	private final List<Corner> corners;
	private Storey storey;

	public Room(List<Corner> corners) {
		this.id = ++highestId;
		this.corners = corners;
	}

	void setStorey(Storey storey) {
		if (this.storey != null) {
			throw new IllegalArgumentException("no rooms with multiple storeys allowed"); // TODO allow for elevators
																							// etc., but then getZ musst
																							// be removed and z value
																							// determined in context
		}
		// TODO thin-walled model: corner reuse and segment splitting (using other rooms
		// in storey )
		this.storey = storey;
	}

	public List<Double> computeCentroid() {

		Corner roomcentroid = Centroid.computeCentroid(corners);
		return List.of(roomcentroid.getX(), roomcentroid.getY(), storey == null ? 0 : storey.getZ());
	}

	public long getId() {
		return id;
	}

	public List<Corner> getCorners() {
		return Collections.unmodifiableList(corners);
	}

	public List<Double> asCoordinateList() {
		return Geometry.asCoordinateList(corners, storey == null ? 0 : storey.getZ());
	}

	static void resetCounter() {
		highestId = 0;
	}

}
