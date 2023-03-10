package org.opensourcebim.levelout.intermediatemodel;

import java.util.Arrays;
import java.util.List;

public class Corner {

	private final double x;
	private final double y;
	private final double z;

	public Corner(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}


	public List<Double> asCoordinateList() {
		return Arrays.asList(x, y, z);
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Corner){
			Corner c = (Corner) obj;
			return this.x==c.x && this.y == c.y && this.z== z;
		} else {
			return false;
		}
	}
}
