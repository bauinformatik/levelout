package org.opensourcebim.levelout.util;

import org.opensourcebim.levelout.intermediatemodel.Corner;
import org.opensourcebim.levelout.intermediatemodel.Storey;

import java.util.ArrayList;
import java.util.List;

public class Geometry {
	public static List<Double> asCoordinateList(List<Corner> corners, double z){
		List<Double> coordinates = new ArrayList<>();
		for (Corner corner: corners){
			coordinates.add(corner.getX());
			coordinates.add(corner.getY());
			coordinates.add(z);
		}
		return coordinates;
	}

	public static double minimumLevel(List<Storey> storeys){
		double minElevation = Double.MAX_VALUE;
		for(Storey storey : storeys){
			if(storey.getZ() < minElevation) minElevation = storey.getZ();
		}
		return minElevation;
	}
}
