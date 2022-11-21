package org.opensourcebim.levelout.intermediatemodel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import de.topobyte.osm4j.xml.output.OsmXmlOutputStream;
import org.citygml4j.core.model.CityGMLVersion;
import org.citygml4j.core.model.building.Building;
import org.citygml4j.xml.CityGMLContext;
import org.citygml4j.xml.module.citygml.CoreModule;
import org.citygml4j.xml.writer.CityGMLChunkWriter;
import org.citygml4j.xml.writer.CityGMLOutputFactory;
import org.xmlobjects.gml.model.feature.BoundingShape;
import org.xmlobjects.gml.model.geometry.Envelope;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

import de.topobyte.osm4j.core.access.OsmOutputStream;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import net.opengis.indoorgml.core.v_1_0.IndoorFeaturesType;
import net.opengis.indoorgml.core.v_1_0.ObjectFactory;
public class GenericBuilding {

	static FootPrint fp;
	 String fileName2 = "output/osmoutputnew.osm";
	 private String name = "default";
	 private static ObjectFactory objectFactory = new ObjectFactory();
	 
		OsmOutputStream osmOutput;
	//static GenericNode gn;
	//static GenericPolygon pn;
		
	public GenericBuilding(FootPrint fp, String name) {
		super();
		this.fp = fp;
		this.name = name;
	}
	public GenericBuilding() {
		// TODO Auto-generated constructor stub
	}


	public static void main(String[] args) throws Exception {
		new GenericBuilding(fp, "test").createCitygmlBuilding();
	}
	public void createCitygmlBuilding()  throws Exception {
		CityGMLContext context = CityGMLContext.newInstance();
		Building b = fp.setLodgeom();
		Envelope envelope = b.computeEnvelope();

		CityGMLVersion version = CityGMLVersion.v2_0;
		CityGMLOutputFactory out = context.createCityGMLOutputFactory(version);
		Path output = Paths.get("output", name + "-city.gml");
		Files.createDirectories(output.getParent());
		System.out.print(output.getParent());
		Files.createFile(output);

		try (CityGMLChunkWriter writer = out.createCityGMLChunkWriter(output, StandardCharsets.UTF_8.name())) {
			writer.withIndent("  ").withDefaultSchemaLocations().withDefaultPrefixes()
					.withDefaultNamespace(CoreModule.of(version).getNamespaceURI())
					.withHeaderComment("File created with citygml4j");

			writer.getCityModelInfo().setBoundedBy(new BoundingShape(envelope));

			writer.writeMember(b);
		}
	}
	
	public  void createOsmBuilding() throws IOException {
		OutputStream output2 = new FileOutputStream("output/" + name + ".osm");
		OsmOutputStream osmOutput = new OsmXmlOutputStream(output2, true);
		for (int i=0;i<fp.getPolygonList().size();i++) {
			fp.getPolygonList().get(i).createosmWay(osmOutput); // how to write tags
		}
		//osmOutput.write(way); // do we need to write both ways and nodes?
		//osmOutput.write(pn.createosmWay());

	}
	public void createIndoorGmlBuilding() throws FileNotFoundException, JAXBException {
		FileOutputStream fout = new FileOutputStream("output/" + name + "-indoor.gml");
		JAXBContext context = JAXBContext.newInstance(IndoorFeaturesType.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
				IndoorGMLNameSpaceMapper.DEFAULT_URI + " http://schemas.opengis.net/indoorgml/1.0/indoorgmlcore.xsd " +
						IndoorGMLNameSpaceMapper.NAVIGATION_URI + " http://schemas.opengis.net/indoorgml/1.0/indoorgmlnavi.xsd" +
						IndoorGMLNameSpaceMapper.XLINK_URI + " https://www.w3.org/XML/2008/06/xlink.xsd");
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new IndoorGMLNameSpaceMapper());
		marshaller.marshal(objectFactory.createIndoorFeatures(fp.setIndoorFeatures()), fout);
	}
	
	public class IndoorGMLNameSpaceMapper extends NamespacePrefixMapper {
		private static final String DEFAULT_URI = "http://www.opengis.net/indoorgml/1.0/core";
		private static final String NAVIGATION_URI = "http://www.opengis.net/indoorgml/1.0/navigation";
		private static final String GML_URI = "http://www.opengis.net/gml/3.2";
		private static final String XLINK_URI = "http://www.w3.org/1999/xlink";

		@Override
		public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
			if (DEFAULT_URI.equals(namespaceUri)) {
				return "core";
			} else if (NAVIGATION_URI.equals(namespaceUri)) {
				return "navi";
			} else if (GML_URI.equals(namespaceUri)) {
				return "gml";
			} else if (XLINK_URI.equals(namespaceUri)) {
				return "xlink";
			}
			return suggestion;
		}

		@Override
		public String[] getPreDeclaredNamespaceUris() {
			return new String[] { DEFAULT_URI, NAVIGATION_URI, GML_URI, XLINK_URI };
		}
	}
}

