package org.opensourcebim.levelout.intermediatemodel;

public class Corner {

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
