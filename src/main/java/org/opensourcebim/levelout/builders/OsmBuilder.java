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
import org.opensourcebim.levelout.intermediatemodel.geo.CartesianPoint;
import org.opensourcebim.levelout.intermediatemodel.geo.CoordinateReference;
import org.opensourcebim.levelout.intermediatemodel.geo.GeodeticPoint;
import org.opensourcebim.levelout.util.Topology;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public class OsmBuilder {
	private final boolean convertEpsg;
	private OsmOutputStream osmOutput;
	private CoordinateReference crs;
	private int wayId;
	private int nodeId;

	private final Topology topology = new Topology();
	private final Map<Corner, Node> nodeCache = new HashMap<>();

	public OsmBuilder() {
		this(false);
	}
	public OsmBuilder(boolean convertEpsg) {
		this.convertEpsg = convertEpsg;
	}

	private Node createOsmNode(Corner pt) {
		if(nodeCache.containsKey(pt)) return nodeCache.get(pt);
		CartesianPoint cartesian = new CartesianPoint(pt.getX(), pt.getY());
		GeodeticPoint geodetic = crs.cartesianToGeodetic(cartesian, false);
		Node node = new Node(--nodeId, geodetic.longitude, geodetic.latitude);
		nodeCache.put(pt, node);
		return node;
	}

	private void createOsmNode(Corner point, List<OsmTag> tags) throws IOException {
		Node node = createOsmNode(point);
		List<OsmTag> allTags = new ArrayList<>();
		allTags.addAll(node.getTags());
		allTags.addAll(tags);
		node.setTags(allTags);
	}

	private void writeAndClearNodeCache() throws IOException {
		for(Node node: nodeCache.values()){
			osmOutput.write(node);
		}
		nodeCache.clear();
	}

	private void createAndWriteRoom(Room room, List<Tag> levelTags) throws IOException {
		if(!room.hasGeometry()) return;  // In the following, we can assume geometry.
		List<Long> nodes = new ArrayList<>();
		for (Corner corner : topology.getOutline(room)) {
			nodes.add(createOsmNode(corner).getId());
		}
		nodes.add(nodes.get(0));
		List<OsmTag> tags = new ArrayList<>(List.of(
				new Tag("indoor", "room"),
				new Tag("ref", room.getName())));
				new Tag("id", Long.toString(room.getId()));;
		tags.addAll(levelTags);
		OsmWay way = new Way(--wayId, TLongArrayList.wrap(Longs.toArray(nodes)), tags);
		osmOutput.write(way);
	}

	private void createAndWriteDoor(Door door, List<Tag> levelTags) throws IOException {
		if(!door.hasGeometry()) return;  // In the following, we can assume geometry.
		List<OsmTag> tags = new ArrayList<>(List.of(
				new Tag("indoor", "door"),
				new Tag("door", door.isClosable() ? "yes" : "no")
				));
		if(door.getName()!=null) tags.add(new Tag("ref", door.getName()));
		tags.add(new Tag("id", Long.toString(door.getId())));
		tags.addAll(levelTags);
		createOsmNode(topology.getPoint(door), tags);
	}

	private void createAndWriteStorey(Storey storey) throws IOException {
		topology.init(storey);
		List<Tag> levelTags = Arrays.asList(
				new Tag("level", Integer.toString(storey.getLevel())),
				new Tag("level:ref", storey.getName())
		);
		for (Room room : storey.getRooms()) {
			createAndWriteRoom(room, levelTags);
		}
		for (Door door : storey.getDoors()) {
			createAndWriteDoor(door, levelTags);
		}
		writeAndClearNodeCache();
	}

	public static OsmWay writeWayDetails(long wayid, long[] nodeList, List<OsmTag> osmtag) {
		return new Way(wayid, TLongArrayList.wrap(nodeList), osmtag);
	}

	public static OsmNode WriteNodeDetails(long id, double lat, double lon) {
		return new Node(id, lon, lat);
	}

	public void createAndWriteBuilding(Building building, OutputStream outputStream) throws IOException {
		this.crs = building.getCrs();
		this.osmOutput = new OsmXmlOutputStream(outputStream, true);
		List<Long> nodes = new ArrayList<>();
		for (Corner corner : building.getCorners()) {
			Node node = createOsmNode(corner);
			nodes.add(node.getId());
		}
		if (!nodes.isEmpty()) nodes.add(nodes.get(0));
		List<Storey> storeys = building.getStoreys();
		List<Tag> tags = new ArrayList<>();
		tags.add(new Tag("building", "residential"));
		if (!storeys.isEmpty()) {
			tags.add(new Tag("levels", Integer.toString(storeys.size())));
			tags.add(new Tag("min_level", Integer.toString(storeys.get(0).getLevel())));
			tags.add(new Tag("max_level", Integer.toString(storeys.get(storeys.size() - 1).getLevel())));
		}
		OsmWay way = new Way(--wayId, TLongArrayList.wrap(Longs.toArray(nodes)), tags);
		this.osmOutput.write(way);
		writeAndClearNodeCache();
		for (Storey storey : storeys) {
			createAndWriteStorey(storey);
		}
		this.osmOutput.complete();
	}

}
