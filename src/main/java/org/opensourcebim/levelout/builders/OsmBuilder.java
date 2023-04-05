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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OsmBuilder {
	private OsmOutputStream osmOutput;
	private CoordinateReference crs;
	private int wayId;
	private int nodeId;

	private long createAndWriteOsmNode(Corner pt) throws IOException {
		CartesianPoint cartesian = new CartesianPoint(pt.getX(), pt.getY(), 0);
		GeodeticPoint geodetic = crs.cartesianToGeodetic(cartesian);
		Node node = new Node(--nodeId, geodetic.longitude, geodetic.latitude);
		osmOutput.write(node);
		return node.getId();
	}

	private void createAndWriteOsmNode(List<Double> doorcentroid, List<OsmTag> tags) throws IOException {
		// TODO Auto-generated method stub
		CartesianPoint cartesian = new CartesianPoint(doorcentroid.get(0), doorcentroid.get(1), 0);
		GeodeticPoint geodetic = crs.cartesianToGeodetic(cartesian);
		Node door = new Node(--nodeId, geodetic.longitude, geodetic.latitude, tags);
		osmOutput.write(door);
	}

	private void createAndWriteRoom(Room room, List<Tag> levelTags) throws IOException {
		// TODO: cache OSM nodes per LevelOut corner (thin-walled model) or collect all
		// and write later
		List<Long> nodes = new ArrayList<>();
		for (Corner genericNode : room.getCorners()) {
			long nodeId = createAndWriteOsmNode(genericNode);
			nodes.add(nodeId);
		}
		if (nodes.size() > 0)
			nodes.add(nodes.get(0));

		List<OsmTag> tags = new ArrayList<>(List.of(
				new Tag("indoor", "room"),
				new Tag("ref", room.getName())));
		tags.addAll(levelTags);
		OsmWay way = new Way(--wayId, TLongArrayList.wrap(Longs.toArray(nodes)), tags);
		osmOutput.write(way);
	}

	private void createAndWriteDoor(Door door, List<Tag> levelTags) throws IOException {
		List<OsmTag> tags = new ArrayList<>(List.of(
				new Tag("indoor", "door"),
				new Tag("door", door.isClosable() ? "yes" : "no")
				));
		if(door.getName()!=null) tags.add(new Tag("ref", door.getName()));;
		tags.addAll(levelTags);
		createAndWriteOsmNode(door.computeCentroid(), tags);
	}

	private void createAndWriteStorey(Storey storey) throws IOException {
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
			long nodeId = createAndWriteOsmNode(corner);
			nodes.add(nodeId);
		}
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
		for (Storey storey : storeys) {
			createAndWriteStorey(storey);
		}
		this.osmOutput.complete();
	}

}
