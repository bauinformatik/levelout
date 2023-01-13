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
import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.UTMRef;

public class OsmInteractive {
	public static Scanner sc = new Scanner(System.in);
	public static OsmXmlOutputStream osmOutput;

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

	public static void readAndWriteNodeDetails(int num) {
		List<Node> nodeList = new ArrayList<>();
		double bearing = 0;
		double originlat = 50.9773653;
		double originlon = 11.34782554233;
		double originx = 0;
		double originy = 0;
		double mapunitdistance = 0.10;
		for (int i = 0; i < num; i++) {
			System.out.println("Enter the node details in the following order : id, x, y");
			long id = sc.nextLong();
			double x = sc.nextDouble();
			double y = sc.nextDouble();
			double dist = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
			double angle = Math.atan2(y, x);
			double angleAdjusted = angle;
			// getGeolocation(originlat, originlon, x, y);
			if (i == 0) {
				getGeolocations(id, originlat, originlon, 0, 0);
				originx = y;
				originy = x;
			} else {
				double distance = mapunitdistance
						* (Math.sqrt(Math.pow((y - originx), 2) + Math.pow((x - originy), 2)));
				if (y != originx && x == originy) {
					bearing = 90;
					getGeolocations(id, originlat, originlon, distance, bearing);
				} else if (y == originx && x != originy) {
					bearing = 0;
					getGeolocations(id, originlat, originlon, distance, bearing);
				} else if (y != originx && x != originy) {
					bearing = 45;
					getGeolocations(id, originlat, originlon, distance, bearing);
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

	public static void getGeolocations(long i, double lat1, double lon1, double distance, double brng) {

		if (i == -1) {
			System.out.println(lon1);
			System.out.println(lat1);
			osmOutput.write(new Node(i, lon1, lat1));

		} else {
			lon1 = Math.toRadians(lon1);
			lat1 = Math.toRadians(lat1);
			brng = Math.toRadians(brng);
			double lat2 = Math.asin(Math.sin(lat1) * Math.cos(distance / 6371)
					+ Math.cos(lat1) * Math.sin(distance / 6371) * Math.cos(brng));

			double lon2 = lon1 + Math.atan2(Math.sin(brng) * Math.sin(distance / 6371) * Math.cos(lat1),
					Math.cos(distance) - Math.sin(lat1) * Math.sin(lat2));

			double lat2d = Math.toDegrees(lat2);
			double long2d = Math.toDegrees(lon2);

			osmOutput.write(new Node(i, long2d, lat2d));
			System.out.println(lat2d);
			System.out.println(long2d);
		}
	}
	
	public static void ifcwgs2Utm2wgs (double x, double y, double lat , double lon)
	{
		
		double lat1 = 56.336468;
		double long1 = 150.311068 ;
		UTMRef utm =wgstoProjectedCoorsys(lat1, long1);
		String str = utm.toString();
		String[] splited = str.split("\\s+");
		String zone = splited[0];
		double easting = Double.valueOf(splited[1]);
		double northing = Double.valueOf(splited[2]);
		
		ifclocaltomapcoord(116.611,75.960,easting, northing, zone);
		
		
		
	}

	public static void ifc2geolocations (double x , double y, double dist) {
		
		
		double lon = 11.34782554233 ;
		double lat = 50.9773653;
		double bearing;
		double distance = dist/1000;
		// bearing =  Math.toDegrees(Math.atan2(y,x));  
		 //bearing = (bearing+360)%360;
		 
		 
		
	
		
		if(x==0 && y>0)
		{
				bearing = 0;
		}
		else if(y==0 && x<0)
		{
				bearing = 270;
		}
		else if(x==0 && y>0)
		{
				bearing = 180;
		}
		else if(y==0 && x>0)
		{
				bearing = 90;
		}
		else
		{
		 bearing =   Math.toDegrees(Math.atan2(y,x))    ;                  
		}
	/*	else
		{
			bearing = 90 - Math.toDegrees(Math.atan2(y,x))    ; 
		}*/
		
		//System.out.println(bearing);
		
		 double bearing2 = (bearing+360)%360;
		 bearing2 = Math.toRadians(bearing2);
		 System.out.println(bearing2);
			
		 
		double lon1 = Math.toRadians(lon);
		double lat1 = Math.toRadians(lat);
		double lat2 = Math.asin(Math.sin(lat1) * Math.cos(distance / 6371)
				+ Math.cos(lat1) * Math.sin(distance / 6371) * Math.cos(bearing2));

		double lon2 = lon1 + Math.atan2(Math.sin(bearing2) * Math.sin(distance / 6371) * Math.cos(lat1),
				Math.cos(distance/6371) - Math.sin(lat1) * Math.sin(lat2));
		

		double lat2d = Math.toDegrees(lat2);
		double long2d = Math.toDegrees(lon2);
		
		
		//	long2d = (long2d+540)%360;
			//long2d = long2d -180;
		
	

		System.out.println("lat "+lat2d);
		System.out.println("lon"+long2d);
		
		
	}
	
	public static UTMRef wgstoProjectedCoorsys(double lat, double lon) {
		System.out.println("Convert Latitude/Longitude to UTM Reference");
		LatLng latlon = new LatLng(lat, lon);
		// LatLng latlon = new LatLng(53.320555, -1.729000);
		System.out.println("Latitude/Longitude: " + latlon.toString());
		UTMRef utm = latlon.toUTMRef();
		System.out.println("Converted to UTM Ref: " + utm.toString());
		System.out.println();
		return utm;
	}

	public static void projectedtoWgsCoorsys(double northing, double easting, int zonelon, char zonelat) {
		System.out.println("Convert UTM Reference to Latitude/Longitude");
		UTMRef utm = new UTMRef(zonelon, zonelat, easting, northing);
		// UTMRef utm = new UTMRef(30, 'U', 584662.2085495128, 5908683.746009846);
		System.out.println("UTM Reference: " + utm.toString());
		LatLng latlong = utm.toLatLng();
		System.out.println("Converted to Lat/Long: " + latlong.toString());
		System.out.println();
	}

	public static void ifclocaltomapcoord(double lat, double lon, double easting, double northing, String zone) {
		// IFC Map Conversion parameters

		double eastings = easting ;//333780.622;
		double northings = northing;//6246775.891;
		double orthogonalHeight = 97.457;
		double xAxisAbscissa = 0.990330045;
		double xAxisOrdinate = -0.138731399;
		double scale = 0.999998;
		double rotation = Math.atan2(xAxisOrdinate, xAxisAbscissa); // check order
		String zn = zone;
		int zonelon = Integer.valueOf(zn.substring(0, 2));
		char zonelat = zn.charAt(2);

		double localx = lat;
		double localy = lon;
		double localzh = 0;

		double a = scale * Math.cos(rotation);
		double b = scale * Math.sin(rotation);

		// map coordinates
		double eastingsmap = (a * localx) - (b * localy) + eastings;
		double northingsmap = (b * localx) + (a * localy) + northings;

		System.out.println("local to Map Grid in x " + eastingsmap);
		System.out.println("local to Map Grid in y " + northingsmap);
		
		projectedtoWgsCoorsys(northingsmap, eastingsmap, zonelon, zonelat);

		double height = localzh + orthogonalHeight;
	}

	public static void ifc2global2Dhelmert(double x, double y, double z) {
		// 4 parameters 2D Helmerts transformation
		// Assumption of N, E known for the origin of the Engineering coordinate system
		double originEastings = 333780.622;
		double originNorthing = 6246775.891;
		double ref2Eastings = 333906.644;
		double ref2Northing = 6246834.938;
		double xtranslation = originEastings;
		double ytranslation = originNorthing;
		double scale = 0.999998;

		// local coordinates
		double ref1x = 0;
		double ref1y = 0;
		double ref2x = 116.611;
		double ref2y = 75.960;

		// using 2D helmerts transformation, solving simultaneous equations

		double val1 = Math.pow(ref2x, 2) + Math.pow(ref2y, 2);
		double val2 = (ref2Eastings - originEastings) * ref2x + (ref2Northing - originNorthing) * ref2y;

		double val3 = val2 / val1;

		double localx = x;
		double localy = y;
		double localzh = z;
		// solving for theta , assuming scale =1

		double angle = Math.acos(val3);

		double a = scale * Math.cos(angle);
		double b = scale * Math.sin(angle);

		double eastingsmap = (a * localx) - (b * localy) + xtranslation;
		double northingsmap = (b * localx) + (a * localy) + ytranslation;

		System.out.println("local to Map Grid Helmert in x " + eastingsmap);
		System.out.println("local to Map Grid Helmert in y " + northingsmap);

		// double height = localzh + orthogonalHeight;

	}

}
