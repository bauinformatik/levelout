package org.opensourcebim.levelout.intermediatemodel;

import java.io.Serializable;

public class Corner implements Serializable {

	private static final long serialVersionUID = 2676517367266810521L;
	private final double x;
	private final double y;

	public Corner(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Corner){
			Corner c = (Corner) obj;
			return this.x==c.x && this.y == c.y ;
		} else {
			return false;
		}
	}
}
