package org.opensourcebim.levelout.samples;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import org.opensourcebim.levelout.builders.OsmBuilder;

import de.topobyte.osm4j.core.model.iface.OsmTag;
import de.topobyte.osm4j.core.model.impl.Tag;
import de.topobyte.osm4j.xml.output.OsmXmlOutputStream;

public class OsmInteractive {
	public static OsmXmlOutputStream osmOutput;

	public static void main(String[] args) throws IOException {
		String fileName = "output/osmoutput7.osm";
		OutputStream output = new FileOutputStream(fileName);
		osmOutput = new OsmXmlOutputStream(output, true);
		osmOutput.write(OsmBuilder.WriteNodeDetails(-1, 0, 0));
		osmOutput.write(OsmBuilder.WriteNodeDetails(-2, 6, 0));
		osmOutput.write(OsmBuilder.WriteNodeDetails(-3, 6, 6));
		osmOutput.write(OsmBuilder.WriteNodeDetails(-4, 0, 6));
		long[] nodeList = new long[] { -1, -2, -3, -4, -1 };
		List<OsmTag> outlinetags = List.of(new Tag("building", "residential"), new Tag("building:levels", "2"),
				new Tag("roof:shape", "flat"), new Tag("min_level", "0"), new Tag("max_level", "1"));
		osmOutput.write(OsmBuilder.writeWayDetails(-1, nodeList, outlinetags));

		osmOutput.complete();
	}

}
