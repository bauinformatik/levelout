package org.opensourcebim.levelout.builders;

import com.google.common.primitives.Longs;
import com.slimjars.dist.gnu.trove.list.array.TLongArrayList;
import de.topobyte.osm4j.core.access.OsmOutputStream;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmTag;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.model.impl.Tag;
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
	public static void createosmWay(Room room, OsmOutputStream osmOutput, List<OsmTag> tags) throws IOException {
		long id = room.getId() * -1;
		List<Long> nodes = new ArrayList<>();
		for (Corner genericNode : room.getRooms()) {
			OsmNode node = genericNode.createOsmNode();
			osmOutput.write(node);
			nodes.add(node.getId());
		}
		nodes.add(nodes.get(0));
		OsmWay way = new Way(id, TLongArrayList.wrap(Longs.toArray(nodes)), tags); // TODO: how to create and set tags, the name of the polygon is just one part of the tag
		osmOutput.write(way);
	}

	public void createOsmBuilding(OutputStream outStream, Building building) throws IOException {
		List<OsmTag> indoorTags =  new ArrayList<>();
		OsmTag tag1 = new Tag("building", "residential");
		OsmTag tag2 = new Tag("indoor", "room");
		indoorTags.add(tag1);
		indoorTags.add(tag2);
		OsmOutputStream osmOutStream = new OsmXmlOutputStream(outStream, true);
		for (Storey footPrint : building.getFootPrints()) {
			String lvl = Integer.toString(footPrint.getLevel());
			OsmTag tag3 = new Tag("level", lvl);
			indoorTags.add(tag3);
			for (Room polygon: footPrint.getPolygonList()) {
				createosmWay(polygon, osmOutStream, indoorTags); // how to write tags
			}
		}
		osmOutStream.complete();
	}

}
