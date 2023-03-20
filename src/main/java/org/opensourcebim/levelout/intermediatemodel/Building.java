package org.opensourcebim.levelout.intermediatemodel;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class Building implements Serializable {

	private static final long serialVersionUID = 481571932663279905L;
	private final List<Storey> storeys;
	private final List<Corner> corners;

	public Building(List<Storey> storeys, List<Corner> corners) {
		this.storeys = storeys;
		this.corners = corners;
	}

	public List<Storey> getStoreys(){
		return Collections.unmodifiableList(storeys);
	}

	public List<Corner> getCorners() {
		return Collections.unmodifiableList(corners);
	}
}
