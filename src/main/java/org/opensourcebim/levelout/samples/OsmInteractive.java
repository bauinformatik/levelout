package org.opensourcebim.levelout.samples;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.CoordinateTransformFactory;
import org.locationtech.proj4j.ProjCoordinate;
import org.opensourcebim.levelout.builders.OsmBuilder;

import com.slimjars.dist.gnu.trove.list.array.TLongArrayList;

import de.topobyte.osm4j.core.model.iface.OsmTag;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.model.impl.Node;
import de.topobyte.osm4j.core.model.impl.Tag;
import de.topobyte.osm4j.core.model.impl.Way;
import de.topobyte.osm4j.xml.output.OsmXmlOutputStream;

public class OsmInteractive {
	public static OsmXmlOutputStream osmOutput;
	private static final CRSFactory crsFactory = new CRSFactory();
	private static final CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();

	public static void main(String[] args) throws IOException {
		String fileName = "output/osmoutput7.osm";
		OutputStream output = new FileOutputStream(fileName);
		osmOutput = new OsmXmlOutputStream(output, true);
		OsmBuilder.WriteNodeDetails(-1, 0, 0, osmOutput);
		OsmBuilder.WriteNodeDetails(-2, 6, 0, osmOutput);
		OsmBuilder.WriteNodeDetails(-3, 6, 6, osmOutput);
		OsmBuilder.WriteNodeDetails(-4, 0, 6, osmOutput);
		long[] nodeList = new long[] { -1, -2, -3, -4, -1 };
		List<OsmTag> outlinetags = List.of(new Tag("building", "residential"), new Tag("building:levels", "2"),
				new Tag("roof:shape", "flat"), new Tag("min_level", "0"), new Tag("max_level", "1"));
	OsmBuilder.writeWayDetails(-1, nodeList, outlinetags, osmOutput);
		
		osmOutput.complete();
	}

	public static void WriteNodeDetails(long id, double lat, double lon) {
		// TODO Auto-generated method stub
		osmOutput.write(new Node(id, lon, lat));
	}

	public static void writeWayDetails(long wayid, long[] nodeList, String osmtag) throws IOException {

		Map<String, List<OsmTag>> alltags = createTagSets();
		if (alltags.containsKey(osmtag)) {
			List<OsmTag> osmtags = alltags.get(osmtag);

			OsmWay way = createOsmWay(wayid, nodeList, osmtags);
			osmOutput.write(way);
		}

	}

	private static Map<String, List<OsmTag>> createTagSets() {
		Map<String, List<OsmTag>> alltags = new HashMap<>();
		List<OsmTag> outlinetags = List.of(new Tag("building", "residential"), new Tag("building:levels", "2"),
				new Tag("roof:shape", "flat"), new Tag("min_level", "0"), new Tag("max_level", "1"));
		List<OsmTag> indoortags1 = List.of(new Tag("indoor", "room"), new Tag("level", "0"));
		List<OsmTag> indoortags2 = List.of(new Tag("indoor", "room"), new Tag("level", "1"));

		alltags.put("outlinetags", outlinetags);
		alltags.put("indoortags1", indoortags1);
		alltags.put("indoortags2", indoortags2);
		return alltags;
	}

	private static OsmWay createOsmWay(long id, long[] nodes, List<OsmTag> osmTags) throws IOException {
		return new Way(id, TLongArrayList.wrap(nodes), osmTags);
	}

	

}
