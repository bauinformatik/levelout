package org.opensourcebim.levelout.intermediatemodel;

import java.util.Collections;
import java.util.List;

public class Door {

	
	
	private final String type;
	private final List<Corner> corners;

	public Door(long id, String type, List<Corner> corners) {
		super();
		this.id = id;
		this.type = type;
		this.corners = corners;
	}


	private final long id;
	public long getId() {
		return id;
	}
	public String getType() {
		return type;
	}

	public List<Corner> getCorners() {
		return Collections.unmodifiableList(corners);
	}

}
