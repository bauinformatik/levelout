package org.opensourcebim.levelout.samples;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.slimjars.dist.gnu.trove.list.array.TLongArrayList;

import de.topobyte.osm4j.core.model.iface.OsmTag;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.model.impl.Node;
import de.topobyte.osm4j.core.model.impl.Tag;
import de.topobyte.osm4j.core.model.impl.Way;
import de.topobyte.osm4j.xml.output.OsmXmlOutputStream;

public class OsmBuilding {
	private static final Scanner sc = new Scanner (System.in);
	private static OsmXmlOutputStream osmOutput;

	public static void main(String[] args) throws IOException {
		String fileName = "output/osmoutput7.osm";
		OutputStream output = new FileOutputStream(fileName);
		osmOutput = new OsmXmlOutputStream(output, true);
		System.out.println("Enter the number of nodes");
		int num = sc.nextInt();
		readAndWriteNodeDetails(num);
		Map<String, List<OsmTag>> tags = createTagSets();
		writeWayDetails(tags);
		osmOutput.complete();
	}

	private static void writeWayDetails(Map<String, List<OsmTag>> alltags)
			throws IOException {
		System.out.println("Enter the number of ways of the way list");

		int waynum = sc.nextInt();
		for (int j=0;j<waynum;j++) {
			System.out.println("Enter way id ");
			long wayid = sc.nextLong();
			sc.nextLine();
			System.out.println("Enter the name of the tag set");
			String tagname = sc.nextLine();

			if(alltags.containsKey(tagname)) {
				List<OsmTag> osmtags = alltags.get(tagname);
				for(OsmTag e :osmtags) {
					System.out.println(e);
				}
				System.out.println("Enter the list of node id's constituting the ways");
				long[] nodeList = readNodeList();
				OsmWay way = createOsmWay(wayid,nodeList, osmtags);
				osmOutput.write(way);
			}

		}
	}

	private static void readAndWriteNodeDetails(int num) {
		for  (int i=0;i<num;i++) {
			System.out.println("Enter the node details in the following order : id, longitude, latitude");
			long id = sc.nextLong();
			double lon = sc.nextDouble();
			double lat = sc.nextDouble();
			osmOutput.write(new Node(id, lon,lat));
		}
	}

	private static Map<String, List<OsmTag>> createTagSets() {
		Map<String,List<OsmTag>> alltags = new HashMap<>();
		List<OsmTag> outlinetags = new ArrayList<>();
		List<OsmTag> indoortags1 = new ArrayList<>();
		List<OsmTag> indoortags2 = new ArrayList<>();

		OsmTag tag1 = new Tag("building", "residential");
		OsmTag tag2 = new Tag("building:levels", "2");
		OsmTag tag3 = new Tag("roof:shape", "flat");
		OsmTag tag4 = new Tag("indoor", "room");
		OsmTag tag5 = new Tag("level", "0");
		OsmTag tag6 = new Tag("level", "1");
		OsmTag tag7 = new Tag("min_level", "0");
		OsmTag tag8 = new Tag("max_level", "1");

		outlinetags.add(tag1);
		outlinetags.add(tag2);
		outlinetags.add(tag3);
		outlinetags.add(tag7);
		outlinetags.add(tag8);

		indoortags1.add(tag4);
		indoortags1.add(tag5);

		indoortags2.add(tag4);
		indoortags2.add(tag6);

		alltags.put("outlinetags",outlinetags);
		alltags.put("indoortags1",indoortags1);
		alltags.put("indoortags2",indoortags2);
		return alltags;
	}


	private static OsmWay createOsmWay(long id, long[] nodes, List<OsmTag> osmTags) throws IOException {
		return new Way(id, TLongArrayList.wrap(nodes), osmTags);
	}

	private static long[] readNodeList() {
		long[] nodeList = new long[5];
		for(int i = 0; i < 5; i++) {
			nodeList[i] = sc.nextLong();
		}
		return nodeList;
	}

}
