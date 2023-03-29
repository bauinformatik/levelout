package org.opensourcebim.levelout.util;

import java.util.List;

import org.opensourcebim.levelout.intermediatemodel.Corner;

public class Centroid {
	public static Corner computeCentroid(List<Corner> corners) {

		double cenX = 0;
		double cenY = 0;
		for (Corner node : corners) {

			cenX += node.getX();
			cenY += node.getY();
		}

		Corner centroid = new Corner(cenX / corners.size(), cenY / corners.size());
		return centroid;

	}
}
