package org.opensourcebim.levelout.intermediatemodel;

import java.util.Collections;
import java.util.List;

public class Building {

	private final List<Storey> footPrints;

	public Building(List<Storey> footPrints) {
		this.footPrints = footPrints;
	}

	public List<Storey> getFootPrints(){
		return Collections.unmodifiableList(footPrints);
	}

}
