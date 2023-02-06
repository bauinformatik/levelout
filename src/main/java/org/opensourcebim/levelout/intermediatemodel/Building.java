package org.opensourcebim.levelout.intermediatemodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Building {

	private final List<Storey> storeys;
	private final int id;

	public Building(int id, List<Storey> storeys) {
		this.storeys = storeys;
		this.id = id;
	}

	public List<Storey> getStoreys(){
		return Collections.unmodifiableList(storeys);
	}

	public int getId() {
		return id;
	}

}
