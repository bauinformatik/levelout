package org.opensourcebim.levelout.intermediatemodel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
import net.opengis.indoorgml.core.v_1_0.CellSpaceMemberType;
import net.opengis.indoorgml.core.v_1_0.IndoorFeaturesType;
import net.opengis.indoorgml.core.v_1_0.MultiLayeredGraphPropertyType;
import net.opengis.indoorgml.core.v_1_0.MultiLayeredGraphType;
import net.opengis.indoorgml.core.v_1_0.NodesType;
import net.opengis.indoorgml.core.v_1_0.ObjectFactory;
import net.opengis.indoorgml.core.v_1_0.PrimalSpaceFeaturesPropertyType;
import net.opengis.indoorgml.core.v_1_0.PrimalSpaceFeaturesType;
import net.opengis.indoorgml.core.v_1_0.SpaceLayerMemberType;
import net.opengis.indoorgml.core.v_1_0.SpaceLayerType;
import net.opengis.indoorgml.core.v_1_0.SpaceLayersType;
import net.opengis.indoorgml.core.v_1_0.StateMemberType;
public class GenericBuilding {

	private final List<FootPrint> fp;
	private final String name;
	private static final ObjectFactory objectFactory = new ObjectFactory();

	public GenericBuilding(List<FootPrint> fp, String name) {
		this.fp = fp;
		this.name = name;
	}
		
	public void createCitygmlBuilding()  throws Exception {
		CityGMLContext context = CityGMLContext.newInstance();
		Building building = new Building();
		for (FootPrint footPrint : fp) {
			building = footPrint.setLodgeom(building);
		}
		Envelope envelope = building.computeEnvelope();

		CityGMLVersion version = CityGMLVersion.v2_0;
		CityGMLOutputFactory outputFactory = context.createCityGMLOutputFactory(version);
		Path path = Paths.get("output", name + "-city.gml");
		Files.createDirectories(path.getParent());

		try (CityGMLChunkWriter writer = outputFactory.createCityGMLChunkWriter(path, StandardCharsets.UTF_8.name())) {
			writer.withIndent("  ").withDefaultSchemaLocations().withDefaultPrefixes()
					.withDefaultNamespace(CoreModule.of(version).getNamespaceURI())
					.withHeaderComment("File created with citygml4j");
			writer.getCityModelInfo().setBoundedBy(new BoundingShape(envelope));
			writer.writeMember(building);
		}
	}

	public void createOsmBuilding() throws IOException {
		OutputStream outStream = new FileOutputStream("output/" + name + ".osm");
		OsmOutputStream osmOutStream = new OsmXmlOutputStream(outStream, true);
		for (FootPrint footPrint : fp) {
			for (GenericPolygon polygon: footPrint.getPolygonList()) {
				OsmWay way = polygon.createosmWay(osmOutStream); // how to write tags
			}
		}
		osmOutStream.complete();
	}

	public void createIndoorGmlBuilding() throws FileNotFoundException, JAXBException {
		IndoorFeaturesType indoorFeatures = new IndoorFeaturesType(); // description
		indoorFeatures.setId("if");
		PrimalSpaceFeaturesType primalSpaceFeature = new PrimalSpaceFeaturesType();
		primalSpaceFeature.setId("pf");
		MultiLayeredGraphType multiLayeredGraph = new MultiLayeredGraphType();
		multiLayeredGraph.setId("mlg"); //	+ String.valueOf(id);

		SpaceLayersType spaceLayers = new SpaceLayersType();
		spaceLayers.setId("slayers");
		List<SpaceLayersType> spaceLayerslist = new ArrayList<>();
		spaceLayerslist.add(spaceLayers);

		SpaceLayerType spaceLayer = new SpaceLayerType();
		spaceLayer.setId("sl");
		List<SpaceLayerMemberType> spaceLayerMemberList = new ArrayList<>();
		SpaceLayerMemberType spaceLayerMember = new SpaceLayerMemberType();
		spaceLayerMember.setSpaceLayer(spaceLayer);
		spaceLayerMemberList.add(spaceLayerMember);

		NodesType nodes = new NodesType();
		nodes.setId("n");
		List<NodesType> nodesList = new ArrayList<>();
		nodesList.add(nodes);

		PrimalSpaceFeaturesPropertyType primalSpaceFeaturesProp = new PrimalSpaceFeaturesPropertyType();
		primalSpaceFeaturesProp.setPrimalSpaceFeatures(primalSpaceFeature);
		indoorFeatures.setPrimalSpaceFeatures(primalSpaceFeaturesProp);

		MultiLayeredGraphPropertyType multilayergraphProp = new MultiLayeredGraphPropertyType();
		multilayergraphProp.setMultiLayeredGraph(multiLayeredGraph);
		indoorFeatures.setMultiLayeredGraph(multilayergraphProp);

		multiLayeredGraph.setSpaceLayers(spaceLayerslist);

		spaceLayers.setSpaceLayerMember(spaceLayerMemberList);
		spaceLayer.setNodes(nodesList);

		List<StateMemberType> states = new ArrayList<>();

		List<CellSpaceMemberType> cellSpaceMembers = new ArrayList<>();

		FileOutputStream outStream = new FileOutputStream("output/" + name + "-indoor.gml", true);
		JAXBContext context = JAXBContext.newInstance(IndoorFeaturesType.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
				IndoorGMLNameSpaceMapper.DEFAULT_URI + " http://schemas.opengis.net/indoorgml/1.0/indoorgmlcore.xsd " +
						IndoorGMLNameSpaceMapper.NAVIGATION_URI + " http://schemas.opengis.net/indoorgml/1.0/indoorgmlnavi.xsd" +
						IndoorGMLNameSpaceMapper.XLINK_URI + " https://www.w3.org/XML/2008/06/xlink.xsd");
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new IndoorGMLNameSpaceMapper());

		for (FootPrint footPrint : fp) {
			List<List> a = footPrint.createIndoorFeatures();
			cellSpaceMembers.addAll(a.get(0));
			states.addAll(a.get(1));
		}

		primalSpaceFeature.setCellSpaceMember(cellSpaceMembers);
		nodes.setStateMember(states);

		marshaller.marshal(objectFactory.createIndoorFeatures(indoorFeatures), outStream);
	}

	public static class IndoorGMLNameSpaceMapper extends NamespacePrefixMapper {
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
			return new String[]{DEFAULT_URI, NAVIGATION_URI, GML_URI, XLINK_URI};
		}
	}
}

