package org.opensourcebim.levelout.builders;

import com.google.common.primitives.Longs;
import com.slimjars.dist.gnu.trove.list.array.TLongArrayList;
import de.topobyte.osm4j.core.access.OsmOutputStream;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmTag;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.model.impl.Node;
import de.topobyte.osm4j.core.model.impl.Tag;
import de.topobyte.osm4j.core.model.impl.Way;
import de.topobyte.osm4j.xml.output.OsmXmlOutputStream;
import org.opensourcebim.levelout.intermediatemodel.*;
import org.opensourcebim.levelout.util.CoordinateConversion;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OsmBuilder {
	private OsmOutputStream osmOutput;
	private int ifcVersion;

	private long createAndWriteOsmNode(Corner pt) throws IOException {
		CoordinateConversion.CartesianPoint cartesian = new CoordinateConversion.CartesianPoint(pt.getX(), pt.getY(), pt.getZ());
		CoordinateConversion.GeodeticPoint geodetic = MapConversion.getMapparameters(ifcVersion, cartesian);
		Node node = new Node(pt.getId() * -1, geodetic.latitude, geodetic.longitude);
		osmOutput.write(node);
		return node.getId();
	}

	private void createAndWriteRoom(Room room, String level) throws IOException {
		long id = room.getId() * -1;
		// TODO: cache OSM nodes per LevelOut corner (thin-walled model) or collect all
		// and write later
		List<Long> nodes = new ArrayList<>();
		for (Corner genericNode : room.getCorners()) {
			long nodeId = createAndWriteOsmNode(genericNode);
			nodes.add(nodeId);
		}
		if (nodes.size() > 0)
			nodes.add(nodes.get(0));
		List<OsmTag> tags = Arrays.asList(new Tag("indoor", "room"), new Tag("level", level));
		OsmWay way = new Way(id, TLongArrayList.wrap(Longs.toArray(nodes)), tags);
		osmOutput.write(way);
	}

	private void createAndWriteStorey(Storey storey) throws IOException {
		String lvl = Integer.toString(storey.getLevel());
		for (Room room : storey.getRooms()) {
			createAndWriteRoom(room, lvl);
		}
	}

	public static OsmWay writeWayDetails(long wayid, long[] nodeList, List<OsmTag> osmtag) {

		return new Way(wayid, TLongArrayList.wrap(nodeList), osmtag);

	}

	public static OsmNode WriteNodeDetails(long id, double lat, double lon) {
		// TODO Auto-generated method stub
		return (new Node(id, lon, lat));
	}

	public void createAndWriteBuilding(Building building, int ifcVersion, OutputStream outputStream)
			throws IOException {
		this.ifcVersion = ifcVersion;
		this.osmOutput = new OsmXmlOutputStream(outputStream, true);
		int id = -building.getId();
		OsmWay way = new Way(id, TLongArrayList.wrap(new long[] {}), List.of(new Tag("building", "residential")));
		// TODO: figure out building polygon
		this.osmOutput.write(way);
		for (Storey storey : building.getStoreys()) {
			createAndWriteStorey(storey);
		}
		this.osmOutput.complete();
	}

}
