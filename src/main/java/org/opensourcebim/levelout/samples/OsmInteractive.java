package org.opensourcebim.levelout.samples;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.CoordinateTransformFactory;
import org.locationtech.proj4j.ProjCoordinate;

import com.slimjars.dist.gnu.trove.list.array.TLongArrayList;

import de.topobyte.osm4j.core.model.iface.OsmTag;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.model.impl.Node;
import de.topobyte.osm4j.core.model.impl.Tag;
import de.topobyte.osm4j.core.model.impl.Way;
import de.topobyte.osm4j.xml.output.OsmXmlOutputStream;

public class OsmInteractive {
	public static Scanner sc = new Scanner(System.in);
	public static OsmXmlOutputStream osmOutput;

	public static void main(String[] args) throws IOException {
		String fileName = "output/osmoutput7.osm";
		OutputStream output = new FileOutputStream(fileName);
		osmOutput = new OsmXmlOutputStream(output, true);
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

	public static ProjCoordinate ifclocalcoord2globalcoordv2(double x, double y, double rotation, double lat,
			double lon, String epsg) throws FileNotFoundException {

		long idcount = 0;
		List<CoordinateReferenceSystem> crslist = createCRSfac(epsg);
		ProjCoordinate projcoord = createCTfac(crslist.get(0), crslist.get(1), lat, lon);

		List<Double> mapcoords = ifctomapcoord(x, y, rotation, projcoord.x, projcoord.y);
		ProjCoordinate projcoordwgs = createCTfac(crslist.get(1), crslist.get(0), mapcoords.get(1), mapcoords.get(0));
		idcount = idcount - 1;
		osmOutput.write(new Node(idcount, projcoordwgs.x, projcoordwgs.y));

		return projcoordwgs;

	}

	public static ProjCoordinate ifclocalcoordtoglobalcoordv4(double x, double y, double eastings, double northings,
			double xAxisAbscissa, double xAxisOrdinate, String epsg) {
		// IFC Map Conversion parameters
		long idcount = 0;
		double rotation = Math.atan2(xAxisOrdinate, xAxisAbscissa);

		double a = Math.cos(rotation);
		double b = Math.sin(rotation);

		double eastingsmap = (a * x) - (b * y) + eastings;
		double northingsmap = (b * x) + (a * y) + northings;

		List<CoordinateReferenceSystem> crslist = createCRSfac(epsg);
		ProjCoordinate projcoordwgs = createCTfac(crslist.get(1), crslist.get(0), northingsmap, eastingsmap);
		idcount = idcount - 1;
		osmOutput.write(new Node(idcount, projcoordwgs.x, projcoordwgs.y));

		return projcoordwgs;
	}

	private static List<Double> ifctomapcoord(double x, double y, double rotation, double eastings, double northings) {

		double a = Math.cos(rotation);
		double b = Math.sin(rotation);

		// map coordinates
		double eastingsmap = (a * x) - (b * y) + eastings;
		double northingsmap = (b * x) + (a * y) + northings;

		List<Double> mapcoords = Arrays.asList(eastingsmap, northingsmap);

		return mapcoords;
	}

	private static List<CoordinateReferenceSystem> createCRSfac(String epsg) {

		CRSFactory crsFactory = new CRSFactory();
		CoordinateReferenceSystem wgs84 = crsFactory.createFromName("epsg:4326");
		CoordinateReferenceSystem ifcproj = crsFactory.createFromName(epsg);

		List<CoordinateReferenceSystem> crs = Arrays.asList(wgs84, ifcproj);
		return crs;
	}

	private static ProjCoordinate createCTfac(CoordinateReferenceSystem crs1, CoordinateReferenceSystem crs2,
			double xcoord, double ycoord) {

		CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
		CoordinateTransform coordtransformer = ctFactory.createTransform(crs1, crs2);
		ProjCoordinate resultcoord = new ProjCoordinate();
		coordtransformer.transform(new ProjCoordinate(ycoord, xcoord), resultcoord);
		return resultcoord;

	}

}
