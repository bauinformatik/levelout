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

public class OsmInteractive {
	private static final Scanner sc = new Scanner(System.in);
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

	private static void writeWayDetails(Map<String, List<OsmTag>> alltags) throws IOException {
		System.out.println("Enter the number of ways of the way list");

		int waynum = sc.nextInt();
		for (int j = 0; j < waynum; j++) {
			System.out.println("Enter way id ");
			long wayid = sc.nextLong();
			sc.nextLine();
			System.out.println("Enter the name of the tag set");
			String tagname = sc.nextLine();

			if (alltags.containsKey(tagname)) {
				List<OsmTag> osmtags = alltags.get(tagname);
				for (OsmTag e : osmtags) {
					System.out.println(e);
				}
				System.out.println("Enter the list of node id's constituting the ways");
				long[] nodeList = readNodeList();
				OsmWay way = createOsmWay(wayid, nodeList, osmtags);
				osmOutput.write(way);
			}

		}
	}

	private static void readAndWriteNodeDetails(int num) {
		List<Node> nodeList = new ArrayList<>();
		double bearing =0;
		double originlat = 50.9773653;
    	double originlon = 11.34782554233;
    	double originx =0;
    	double originy=0;
    	double mapunitdistance = 0.010;
		for (int i = 0; i < num; i++) {
			System.out.println("Enter the node details in the following order : id, longitude, latitude");
			long id = sc.nextLong();
			double lat = sc.nextDouble();
			double lon = sc.nextDouble();
		    if (i==0)
		    {
				getGeolocations(id,originlat, originlon, 0, 0);
				originx =lon;
				originy = lat;
		    }
			else
			{
				double distance =  mapunitdistance*(Math.sqrt(Math.pow((lon-originx), 2) + Math.pow((lat-originy), 2)));
				if (lon != originx && lat == originy)
				{
					bearing= 90;
					getGeolocations(id,originlat, originlon, distance, bearing);
				}
				else if (lon == originx && lat != originy)
				{
					bearing =0;
					getGeolocations(id,originlat, originlon, distance, bearing);
				}
				else if (lon != originx && lat != originy)
				{
					bearing =45;
					getGeolocations(id,originlat, originlon, distance, bearing);
				}
			}
			
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

	private static long[] readNodeList() {
		long[] nodeList = new long[5];
		for (int i = 0; i < 5; i++) {
			nodeList[i] = sc.nextLong();
		}
		return nodeList;
	}

	public static void getGeolocations( long i, double lat1, double lon1, double distance, double brng) {

		if (i==-1)
		{
			System.out.println(lon1);
			System.out.println(lat1);
			osmOutput.write(new Node(i, lon1, lat1));
			
		}
		else
		{
		lon1 = Math.toRadians(lon1);
		lat1 = Math.toRadians(lat1);
		brng = Math.toRadians(brng);
		double lat2 = Math.asin(Math.sin(lat1) * Math.cos(distance / 6371)
				+ Math.cos(lat1) * Math.sin(distance / 6371) * Math.cos(brng));

		double lon2 = lon1 + Math.atan2(Math.sin(brng) * Math.sin(distance / 6371) * Math.cos(lat1),
				Math.cos(distance) - Math.sin(lat1) * Math.sin(lat2));


		double lat2d = Math.toDegrees(lat2);
		double long2d = Math.toDegrees(lon2);

		osmOutput.write(new Node(i, long2d,lat2d));
		System.out.println(lat2d);
		System.out.println(long2d);
		}
	}

}