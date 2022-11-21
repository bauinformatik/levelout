package org.opensourcebim.levelout.intermediatemodel;

import java.util.ArrayList;
import java.util.List;

import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.impl.Node;

public class GenericNode {

	private final Number id;
	private final Number x;
	private final Number y;
	private final Number z;

	public GenericNode(Number id, Number x, Number y, Number z) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public OsmNode createOsmNode() {
		return new Node((id.longValue() * -1), x.longValue(), y.longValue());
	}

	public List<Double> createCityGmlNode() {
		List<Double> doubleList = new ArrayList<>();
		doubleList.add(x.doubleValue());                                        // Number cannot be cast to a primitive, use Number.doubleValue()
		doubleList.add(y.doubleValue());
		doubleList.add(z.doubleValue());
		return doubleList;
	}

	public Number getX() {
		return x;
	}

	public Number getY() {
		return y;
	}

	public Number getZ() {
		return z;
	}
}
