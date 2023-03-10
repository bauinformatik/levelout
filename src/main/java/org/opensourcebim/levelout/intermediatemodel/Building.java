package org.opensourcebim.levelout.intermediatemodel;

import java.util.Collections;
import java.util.List;

public class Building {

	private final List<Storey> storeys;

	public Building(List<Storey> storeys) {
		this.storeys = storeys;
	}

	public List<Storey> getStoreys(){
		return Collections.unmodifiableList(storeys);
	}

}
