package org.opensourcebim.levelout.builders;

import com.google.common.primitives.Longs;
import com.slimjars.dist.gnu.trove.list.array.TLongArrayList;
import de.topobyte.osm4j.core.access.OsmOutputStream;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.model.impl.Way;
import de.topobyte.osm4j.xml.output.OsmXmlOutputStream;
import org.opensourcebim.levelout.intermediatemodel.Building;
import org.opensourcebim.levelout.intermediatemodel.Corner;
import org.opensourcebim.levelout.intermediatemodel.Room;
import org.opensourcebim.levelout.intermediatemodel.Storey;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class OsmBuilder {
	public static void createosmWay(Room room, OsmOutputStream osmOutput) throws IOException {
		long id = room.getId() * -1;
		List<Long> nodes = new ArrayList<>();
		for (Corner genericNode : room.getRooms()) {
			OsmNode node = genericNode.createOsmNode();
			osmOutput.write(node);
			nodes.add(node.getId());
		}
		nodes.add(nodes.get(0));
		OsmWay way = new Way(id, TLongArrayList.wrap(Longs.toArray(nodes))); // TODO: how to create and set tags, the name of the polygon is just one part of the tag
		osmOutput.write(way);
	}

	public void createOsmBuilding(OutputStream outStream, Building building) throws IOException {
		OsmOutputStream osmOutStream = new OsmXmlOutputStream(outStream, true);
		for (Storey footPrint : building.getFootPrints()) {
			for (Room polygon: footPrint.getPolygonList()) {
				createosmWay(polygon, osmOutStream); // how to write tags
			}
		}
		osmOutStream.complete();
	}

	public static void writeTagswaysOsm(OsmOutputStream osmOutput, Storey storey) throws IOException {
		for (Room genericPolygon : storey.getPolygonList()) {
			createosmWay(genericPolygon, osmOutput); // how to set tags
		}
	}
}
