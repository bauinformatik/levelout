package org.opensourcebim.levelout;

import java.io.IOException;

import com.slimjars.dist.gnu.trove.list.array.TLongArrayList;

import de.topobyte.osm4j.core.access.OsmOutputStream;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.model.impl.Node;
import de.topobyte.osm4j.core.model.impl.Way;
import de.topobyte.osm4j.xml.output.OsmXmlOutputStream;

public class OsmSample {
	public static void main(String[] args) throws IOException {
		OsmNode node1 = new Node(-1,0,0);
		OsmNode node2 = new Node(-2,100,0);
		OsmNode node3 = new Node(-3, 100, 100);
		OsmNode node4 = new Node(-4, 0, 100);
		long[] nodes = {-1,-2,-3,-4};
		OsmWay way = new Way(-5, TLongArrayList.wrap(nodes));
	    OsmOutputStream osmOutput = new OsmXmlOutputStream(System.out, true);
	    osmOutput.write(node1);
	    osmOutput.write(node2);
	    osmOutput.write(node3);
	    osmOutput.write(node4);
	    osmOutput.write(way);
	    osmOutput.complete();
	}
}
