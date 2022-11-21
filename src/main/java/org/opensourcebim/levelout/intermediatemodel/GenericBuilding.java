package org.opensourcebim.levelout.intermediatemodel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import org.citygml4j.core.model.CityGMLVersion;
import org.citygml4j.core.model.building.Building;
import org.citygml4j.xml.CityGMLContext;
import org.citygml4j.xml.CityGMLContextException;
import org.citygml4j.xml.module.citygml.CoreModule;
import org.citygml4j.xml.writer.CityGMLChunkWriter;
import org.citygml4j.xml.writer.CityGMLOutputFactory;
import org.opensourcebim.levelout.samples.CitygmlBuilding;
import org.opensourcebim.levelout.samples.IndoorGmlBuilding.IndoorGMLNameSpaceMapper;
import org.xmlobjects.gml.model.feature.BoundingShape;
import org.xmlobjects.gml.model.geometry.Envelope;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

import de.topobyte.osm4j.core.access.OsmOutputStream;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.xml.output.OsmXmlOutputStream;
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

	 
	
	 private List<FootPrint> fp;

	String fileName2 = "output/osmoutputnew8.osm";
	 private static ObjectFactory objectFactory = new ObjectFactory();
	 
	//	OsmOutputStream osmOutput;
	//static GenericNode gn;
	//static GenericPolygon pn;
		
		
	public GenericBuilding(List<FootPrint> fp) {
		super();
		this.fp = fp;
	}


	public List<FootPrint> getFp() {
		return fp;
	}


	public void setFp(List<FootPrint> fp) {
		this.fp = fp;
	}
	public GenericBuilding() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception {
		new GenericBuilding().createCitygmlBuilding();
	}
	public void createCitygmlBuilding()  throws Exception {
	
		String fileName = "output/out20.gml";
		CityGMLContext context = CityGMLContext.newInstance();
		Building b = new Building();
		for(int i=0;i<fp.size();i++)
		{
		 b = fp.get(i).setLodgeom(b);
		}
		
		Envelope envelope = b.computeEnvelope();

		CityGMLVersion version = CityGMLVersion.v2_0;
		CityGMLOutputFactory out = context.createCityGMLOutputFactory(version);
		Path output = Paths.get(fileName);
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
	
	
	public  void createOsmBuilding() throws IOException
	{
		OutputStream output2 ;
		   OsmOutputStream  osmOutput ;
		 output2 = new FileOutputStream(fileName2,true);
	 	  osmOutput  = new OsmXmlOutputStream(output2, true);
		
		//fp.get(0).getPolygonList().get(0).createOsmFile();
		
		System.out.println("written");
		for(int j=0;j<fp.size();j++)
		{
		for (int i=0;i<fp.get(j).getPolygonList().size();i++)
		{
		OsmWay way = fp.get(j).getPolygonList().get(i).createosmWay(osmOutput); // how to write tags 
		}
		}
		
	//	fp.get(0).getPolygonList().get(0).osmOutput.complete();
		//osmOutput.write(way); // do we need to write both ways and nodes?
		//osmOutput.write(pn.createosmWay());
       
		osmOutput.complete();
	}


	public void createIndoorGmlBuilding() throws FileNotFoundException, JAXBException {
		
		
		

		IndoorFeaturesType indoorFeatures = new IndoorFeaturesType(); // description 
		indoorFeatures.setId("if");

		PrimalSpaceFeaturesType primalSpaceFeature = new PrimalSpaceFeaturesType();
		primalSpaceFeature.setId("pf");


		MultiLayeredGraphType multiLayeredGraph = new MultiLayeredGraphType();
		multiLayeredGraph.setId("mlg");
	//	multiLayeredGraph.setId("mlg"+ String.valueOf(id));

		SpaceLayersType spaceLayers = new SpaceLayersType();
		spaceLayers.setId("slayers");
		List<SpaceLayersType> spaceLayerslist = new ArrayList<SpaceLayersType>();
		spaceLayerslist.add(spaceLayers);

		SpaceLayerType spaceLayer = new SpaceLayerType();
		spaceLayer.setId("sl");
		List<SpaceLayerMemberType> spaceLayermemberlist = new ArrayList<SpaceLayerMemberType>();
		SpaceLayerMemberType sLayermember = new SpaceLayerMemberType();
		sLayermember.setSpaceLayer(spaceLayer);
		spaceLayermemberlist.add(sLayermember);
		

		NodesType nodes  = new NodesType();
		nodes.setId("n");
		List<NodesType> nodesList = new ArrayList<NodesType>();
		nodesList.add(nodes);
		


		PrimalSpaceFeaturesPropertyType primalspacefeaturesProp = new PrimalSpaceFeaturesPropertyType();
		primalspacefeaturesProp.setPrimalSpaceFeatures(primalSpaceFeature);

		indoorFeatures.setPrimalSpaceFeatures(primalspacefeaturesProp);

		MultiLayeredGraphPropertyType  multilayergraphProp = new MultiLayeredGraphPropertyType();
		multilayergraphProp.setMultiLayeredGraph(multiLayeredGraph);

		indoorFeatures.setMultiLayeredGraph(multilayergraphProp);
		
		multiLayeredGraph.setSpaceLayers(spaceLayerslist);
		
		spaceLayers.setSpaceLayerMember(spaceLayermemberlist);
		spaceLayer.setNodes(nodesList);
		
		
		String fileName = "output/outindoor12.gml";
		FileOutputStream fout = new FileOutputStream(fileName, true); // to append to file and not overwrite
		//IndoorFeaturesType indoorFeatures = null;
		
        List<StateMemberType> states = new ArrayList<StateMemberType>();
		
		List<CellSpaceMemberType> cellspacemembers = new ArrayList<CellSpaceMemberType>();
		
		JAXBContext context = JAXBContext.newInstance(IndoorFeaturesType.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
				IndoorGMLNameSpaceMapper.DEFAULT_URI + " http://schemas.opengis.net/indoorgml/1.0/indoorgmlcore.xsd " +
						IndoorGMLNameSpaceMapper.NAVIGATION_URI + " http://schemas.opengis.net/indoorgml/1.0/indoorgmlnavi.xsd" +
						IndoorGMLNameSpaceMapper.XLINK_URI + " https://www.w3.org/XML/2008/06/xlink.xsd");
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new IndoorGMLNameSpaceMapper());
		
		for (int i=0;i<fp.size();i++)
		{
			int n = fp.size();
			System.out.println(n);
		    List<List>  a = fp.get(i).createIndoorFeatures();
		  	cellspacemembers.addAll(a.get(0));
			states.addAll(a.get(1));
		}
		
		primalSpaceFeature.setCellSpaceMember(cellspacemembers);
		nodes.setStateMember(states);
		
		
		 marshaller.marshal(objectFactory.createIndoorFeatures(indoorFeatures), fout);
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

	
	/*
	
	public void createIndoorgmlBuilding() throws FileNotFoundException
	{
		String fileName = "output/outindoor5.gml";
		FileOutputStream fout = new FileOutputStream(fileName);
	}*/

