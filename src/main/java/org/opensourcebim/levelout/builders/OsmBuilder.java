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
import java.util.Arrays;
import java.util.List;

public class OsmBuilder {
	private OsmOutputStream osmOutput;

	private void createAndWriteRoom(Room room, String level) throws IOException {
		long id = room.getId() * -1;
		// TODO: cache OSM nodes per LevelOut corner (thin-walled model) or collect all and write later
		List<Long> nodes = new ArrayList<>();
		for (Corner genericNode : room.getCorners()) {
			OsmNode node = genericNode.createOsmNode();
			osmOutput.write(node);
			nodes.add(node.getId());
		}
		nodes.add(nodes.get(0));
		List<OsmTag> tags = Arrays.asList (new Tag("indoor", "room"), new Tag("level", level) );
		OsmWay way = new Way(id, TLongArrayList.wrap(Longs.toArray(nodes)), tags);
		osmOutput.write(way);
	}

	private void createAndWriteStorey(Storey storey) throws IOException {
		String lvl = Integer.toString(storey.getLevel());
		for (Room room: storey.getRooms()) {
			createAndWriteRoom(room, lvl);
		}
	}

	public void createAndWriteBuilding(Building building, OutputStream outputStream) throws IOException {
		this.osmOutput = new OsmXmlOutputStream(outputStream, true);
		int id = -building.getId();
		OsmWay way = new Way(id, TLongArrayList.wrap(new long[]{}), List.of(new Tag("building", "residential")));
		// TODO: figure out building polygon
		this.osmOutput.write(way);
		for (Storey storey : building.getStoreys()) {
			createAndWriteStorey(storey);
		}
		this.osmOutput.complete();
	}

}
