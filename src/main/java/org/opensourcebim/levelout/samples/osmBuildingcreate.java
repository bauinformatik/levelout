package org.opensourcebim.levelout.samples;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.slimjars.dist.gnu.trove.list.array.TLongArrayList;

import de.topobyte.osm4j.core.access.OsmOutputStream;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmTag;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.model.impl.Node;
import de.topobyte.osm4j.core.model.impl.Tag;
import de.topobyte.osm4j.core.model.impl.Way;
import de.topobyte.osm4j.xml.output.OsmXmlOutputStream;

public class osmBuildingcreate {
	public static void main(String[] args) throws IOException {

		
			OsmNode node1 = new Node(-1,0,0);
			OsmNode node2 = new Node(-2,6,0);
			OsmNode node3 = new Node(-3,6,6);
			OsmNode node4 = new Node(-4,0,6);
			OsmNode node5 = new Node(-5,10,0);
			OsmNode node6 = new Node(-6,10,2);
			OsmNode node7 = new Node(-7,6,2);
			OsmNode node8 = new Node(-8,10,6);
			
			
			OsmNode node11 = new Node(-9,0,0);
			OsmNode node22 = new Node(-10,6,0);
			OsmNode node33 = new Node(-11,6,6);
			OsmNode node44 = new Node(-12,0,6);
			OsmNode node55 = new Node(-13,10,0);
			OsmNode node66 = new Node(-14,10,2);
			OsmNode node77 = new Node(-15,6,2);
			OsmNode node88 = new Node(-16,10,6);
			
			
			
			
			
			
			
			
			
			long[] nodes1 = {-1,-2,-3,-4, -1};
			long[] nodes2 = {-2,-5,-6,-7, -2};
			long[] nodes3 = {-7,-6,-8,-3, -7};
			
			long[] nodes11 = {-9,-10,-11,-12, -9};
			long[] nodes22 = {-10,-13,-14,-15, -10};
			long[] nodes33 = {-15,-14,-16,-11, -15};
			
			long[] outline = {-1,-5,-8,-4, -1};

		
			
			
			 List<OsmTag> outlinetags = new ArrayList<OsmTag>();
			 List<OsmTag> indoortags1 = new ArrayList<OsmTag>(); 
			 List<OsmTag> indoortags2 = new ArrayList<OsmTag>(); 
			 
			 OsmTag tag1 = new Tag("building", "residential");
			 OsmTag tag2 = new Tag("building:levels", "2");
			 OsmTag tag3 = new Tag ("roof:shape","flat");
			 OsmTag tag4 = new Tag ("indoor","room");
			 OsmTag tag5 = new Tag ("level","0");
			 OsmTag tag6 = new Tag ("level","1");
			 OsmTag tag7 = new Tag ("min_level","0");
			 OsmTag tag8 = new Tag ("max_level","1");
			 
			 
			 
			 outlinetags.add(tag1);
			 outlinetags.add(tag2);
			 outlinetags.add(tag3);
			 outlinetags.add(tag7);
			 outlinetags.add(tag8);
			 
			 indoortags1.add(tag4);
			 indoortags1.add(tag5);
			 
			 indoortags2.add(tag4);
			 indoortags2.add(tag6);
			 
			 
					 
			OsmWay way1 = new Way(-1, TLongArrayList.wrap(nodes1),indoortags1);
			OsmWay way2 = new Way(-2, TLongArrayList.wrap(nodes2),indoortags1);
			OsmWay way3 = new Way(-3, TLongArrayList.wrap(nodes3),indoortags1);
			
			OsmWay way11 = new Way(-4, TLongArrayList.wrap(nodes11),indoortags2);
			OsmWay way22 = new Way(-5, TLongArrayList.wrap(nodes22),indoortags2);
			OsmWay way33 = new Way(-6, TLongArrayList.wrap(nodes33),indoortags2);
			OsmWay wayoutline = new Way(-7, TLongArrayList.wrap(outline),outlinetags);
			
			
			
			OutputStream output = new FileOutputStream("C:\\Users\\rano2746\\3D Objects\\OSM\\SotMap/osmoutput3.osm");
			  
		    OsmOutputStream osmOutput = new OsmXmlOutputStream(output, true);
		    osmOutput.write(node1);
		   osmOutput.write(node2);
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
		    osmOutput.write(wayoutline);
		    
		    osmOutput.complete();
			
	}
	}
	
			
