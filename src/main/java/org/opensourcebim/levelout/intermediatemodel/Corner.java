package org.opensourcebim.levelout.intermediatemodel;

import java.util.Arrays;
import java.util.List;

import org.opensourcebim.levelout.util.CoordinateConversion;
import org.opensourcebim.levelout.util.CoordinateConversion.CartesianPoint;
import org.opensourcebim.levelout.util.CoordinateConversion.GeodeticPoint;
import org.opensourcebim.levelout.util.CoordinateConversion.ProjectedPoint;

import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.impl.Node;

public class Corner {

	private final long id;
	private final double x;
	private final double y;
	private final double z;

	public Corner(long id, double x, double y, double z) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public OsmNode createOsmNode(int ifcVersion) {
		CartesianPoint a = new CartesianPoint(x, y, z);
		Object b = MapConversion.getMapparameters(ifcVersion, a);
		double xval = 0, yval = 0 ;
		if (b instanceof GeodeticPoint)
		{
			xval = ((GeodeticPoint)b).latitude;
			yval = ((GeodeticPoint)b).longitude;
		}
		if (b instanceof ProjectedPoint)
		{
			xval = ((ProjectedPoint)b).eastings;
			yval = ((ProjectedPoint)b).northings;
		}
		
		return new Node(id * -1, xval, yval);
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
}
