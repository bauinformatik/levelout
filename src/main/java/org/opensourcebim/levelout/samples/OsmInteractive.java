package org.opensourcebim.levelout.samples;

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
	public static OsmXmlOutputStream osmOutput;
	private static final CRSFactory crsFactory = new CRSFactory();
	private static final CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();

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

	public static ProjCoordinate ifclocalcoord2globalcoordv2(double x, double y, double rotation, double lat, double lon, String epsg) {

		CoordinateReferenceSystem wgs84 = crsFactory.createFromName("epsg:4326");
		CoordinateReferenceSystem utm = crsFactory.createFromName(epsg); // use fixed UTM EPSG, we don't have this from IFC

		CoordinateTransform wgs84ToUTM = ctFactory.createTransform(wgs84, utm);
		ProjCoordinate utmOrigin = wgs84ToUTM.transform(new ProjCoordinate(lon, lat), new ProjCoordinate());

		double a = Math.cos(rotation);
		double b = Math.sin(rotation);
		double utmPointX = (a * x) - (b * y) + utmOrigin.x;
		double utmPointY = (b * x) + (a * y) + utmOrigin.y;

		CoordinateTransform utmToWgs84 = ctFactory.createTransform(utm, wgs84);
		ProjCoordinate wgs84Point = utmToWgs84.transform(new ProjCoordinate(utmPointX, utmPointY), new ProjCoordinate());

		osmOutput.write(new Node( -1, wgs84Point.x, wgs84Point.y));

		return wgs84Point;

	}

	public static ProjCoordinate ifclocalcoordtoglobalcoordv4(double x, double y, double eastings, double northings,
			double xAxisAbscissa, double xAxisOrdinate, String epsg) {

		double rotation = Math.atan2(xAxisOrdinate, xAxisAbscissa);
		double a = Math.cos(rotation);
		double b = Math.sin(rotation);
		// TODO just normalize, not need for trigonometric functions

		double eastingsmap = (a * x) - (b * y) + eastings;
		double northingsmap = (b * x) + (a * y) + northings;

		CoordinateReferenceSystem wgs84 = crsFactory.createFromName("epsg:4326");
		CoordinateReferenceSystem originCRS = crsFactory.createFromName(epsg);

		CoordinateTransform originCrsToWgs84 = ctFactory.createTransform(originCRS, wgs84);
		ProjCoordinate resultcoord = originCrsToWgs84.transform(new ProjCoordinate(eastingsmap, northingsmap), new ProjCoordinate());

		osmOutput.write(new Node(-1, resultcoord.x, resultcoord.y));

		return resultcoord;
	}

}
