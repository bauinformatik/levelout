package org.opensourcebim.levelout.intermediatemodel;

import java.util.Collections;
import java.util.List;

public class Building {

	private final List<Storey> footPrints;
	private final int id;

	public Building(int id, List<Storey> footPrints) {
		this.footPrints = footPrints;
		this.id = id;
	}

	public List<Storey> getFootPrints(){
		return Collections.unmodifiableList(footPrints);
	}

	public int getId() {
		return id;
	}
}
