package org.opensourcebim.levelout.samples;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.slimjars.dist.gnu.trove.list.TLongList;
import com.slimjars.dist.gnu.trove.list.array.TLongArrayList;

import de.topobyte.osm4j.core.access.OsmOutputStream;
import de.topobyte.osm4j.core.model.iface.OsmBounds;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmTag;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.model.impl.Node;
import de.topobyte.osm4j.core.model.impl.Tag;
import de.topobyte.osm4j.core.model.impl.Way;
import de.topobyte.osm4j.xml.output.OsmXmlOutputStream;

public class OsmBuilding {

	public static void main(String[] args) throws IOException {

		String fileName = "output/osmoutput7.osm";
		Scanner sc = new Scanner (System.in);


		System.out.println("Enter the number of nodes");
		int num = sc.nextInt();

		OutputStream output = new FileOutputStream(fileName);
		OsmOutputStream osmOutput = new OsmXmlOutputStream(output, true);	

		setNodedetails(sc, num, osmOutput);

		Map<String, List<OsmTag>> alltags = createTags();





		setWaydetails(sc, osmOutput, alltags);

		osmOutput.complete();


	}



	private static void setWaydetails(Scanner sc, OsmOutputStream osmOutput, Map<String, List<OsmTag>> alltags)
			throws IOException {
		System.out.println("Enter the number of ways of the way list");

		int waynum = sc.nextInt();
		List<long[]> wayList = new ArrayList<long[]>();
		for (int j=0;j<waynum;j++)
		{
			System.out.println("Enter the list of node id's constituting the ways");
			long[] nodelis = nodeList();
			System.out.println(nodelis);
			wayList.add(nodelis);

			System.out.println("Enter way id ");
			long wayid = sc.nextLong();
			sc.nextLine();
			/*System.out.println("Enter the name of the tag");
			String tagname = sc.nextLine();

			if(alltags.containsKey(tagname))
			{
				List<OsmTag> osmtags = alltags.get(tagname);

				for(OsmTag e :osmtags)
				{
					System.out.println(e);
				}
				System.out.println("creating ways");*/
				OsmWay w1 = createosmWay(wayid,nodelis);
				System.out.println(w1);
				System.out.println("writing ways");

				osmOutput.write(w1);
			//}

		}
	}



	private static void setNodedetails(Scanner sc, int num, OsmOutputStream osmOutput) throws IOException {
		for  (int i=0;i<num;i++)
		{
			System.out.println("Enter the node details in the following order : id, longitude, latitude");

			long id = sc.nextLong();
			double lon = sc.nextDouble();
			double lat = sc.nextDouble();

			OsmNode n1 = createOsmnode(id, lon,lat);
			//osmOutput.write(n1);

		}
	}



	private static Map<String, List<OsmTag>> createTags() {
		Map<String,List<OsmTag>> alltags = new HashMap<String,List<OsmTag>>();
		List<OsmTag> outlinetags = new ArrayList<OsmTag>();
		List<OsmTag> indoortags1 = new ArrayList<OsmTag>();
		List<OsmTag> indoortags2 = new ArrayList<OsmTag>();

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
	


	private static OsmNode createOsmnode(long id, double lon, double lat)  {


		List <OsmNode> nodelist = new ArrayList<OsmNode>();

		OsmNode newnode = new Node(id,lon,lat);

		nodelist.add(newnode);

		return newnode;

	}

	public static OsmWay createosmWay(long id, long[] nodes) throws IOException {

		List <OsmWay> wayList = new ArrayList<OsmWay>();
		OsmWay ways = new Way(id, TLongArrayList.wrap(nodes));
		//long[] nodeList = new long[5];
		wayList.add(ways);
		return ways;


	}

	public static long[] nodeList() {
		long[] nodeList = new long[5];
		Scanner sc2 = new Scanner(System.in);
		for(int i = 0; i < 5; i++) {
			nodeList[i] = sc2.nextLong();
		}

		return nodeList;
	}

}

// old code


/*	OsmNode node1 = new Node(-1, 0, 0);
OsmNode node2 = new Node(-2, 6, 0);
OsmNode node3 = new Node(-3, 6, 6);
OsmNode node4 = new Node(-4, 0, 6);
OsmNode node5 = new Node(-5, 10, 0);
OsmNode node6 = new Node(-6, 10, 2);
OsmNode node7 = new Node(-7, 6, 2);
OsmNode node8 = new Node(-8, 10, 6);

OsmNode node11 = new Node(-9, 0, 0);
OsmNode node22 = new Node(-10, 6, 0);
OsmNode node33 = new Node(-11, 6, 6);
OsmNode node44 = new Node(-12, 0, 6);
OsmNode node55 = new Node(-13, 10, 0);
OsmNode node66 = new Node(-14, 10, 2);
OsmNode node77 = new Node(-15, 6, 2);
OsmNode node88 = new Node(-16, 10, 6);*/


/*	long[] nodes1 = { -1, -2, -3, -4, -1 };
	long[] nodes2 = { -2, -5, -6, -7, -2 };
	long[] nodes3 = { -7, -6, -8, -3, -7 };

	long[] nodes11 = { -9, -10, -11, -12, -9 };
	long[] nodes22 = { -10, -13, -14, -15, -10 };
	long[] nodes33 = { -15, -14, -16, -11, -15 };

	long[] outline = { -1, -5, -8, -4, -1 };*/




/*	OsmWay way1 = new Way(-1, TLongArrayList.wrap(nodes1), indoortags1);
	OsmWay way2 = new Way(-2, TLongArrayList.wrap(nodes2), indoortags1);
	OsmWay way3 = new Way(-3, TLongArrayList.wrap(nodes3), indoortags1);

	OsmWay way11 = new Way(-4, TLongArrayList.wrap(nodes11), indoortags2);
	OsmWay way22 = new Way(-5, TLongArrayList.wrap(nodes22), indoortags2);
	OsmWay way33 = new Way(-6, TLongArrayList.wrap(nodes33), indoortags2);
	OsmWay wayoutline = new Way(-7, TLongArrayList.wrap(outline), outlinetags);*/


//OsmOutputStream osmOutput = osmWriter();


/*	osmOutput.write(node2);
	osmOutput.write(node3);
	osmOutput.write(node4);
	osmOutput.write(node5);
	osmOutput.write(node6);
	osmOutput.write(node7);
	osmOutput.write(node8);

	osmOutput.write(node11);
	osmOutput.write(node22);
	osmOutput.write(node33);
	osmOutput.write(node44);
	osmOutput.write(node55);
	osmOutput.write(node66);
	osmOutput.write(node77);
	osmOutput.write(node88);

	osmOutput.write(way1);
	osmOutput.write(way2);
	osmOutput.write(way3);

	osmOutput.write(way11);
	osmOutput.write(way22);
	osmOutput.write(way33);
	osmOutput.write(wayoutline);*/



//private static <T> void osmWriter(<T> obj) {


//	OutputStream output = new FileOutputStream(fileName);
//	OsmOutputStream osmOutput = new OsmXmlOutputStream(output, true);





//	return osmOutput;
//	}


/*List<OsmTag> taglistname = new ArrayList<OsmTag>();
System.out.println("Enter the number of tags in the list");
int numberoftags= sc.nextInt();
sc.nextLine();
for (int i=0;i<numberoftags;i++)
{
	System.out.println("enter key and value");
	String key = sc.nextLine();
	String value = sc.nextLine();

	OsmTag tag1 = new Tag(key, value);
	
}*/

/*System.out.println("Enter the taglist name");
String taglistname = sc.nextLine();*/