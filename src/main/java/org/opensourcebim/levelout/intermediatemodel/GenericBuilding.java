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

import org.citygml4j.core.model.CityGMLVersion;
import org.citygml4j.core.model.building.Building;
import org.citygml4j.xml.CityGMLContext;
import org.citygml4j.xml.CityGMLContextException;
import org.citygml4j.xml.module.citygml.CoreModule;
import org.citygml4j.xml.writer.CityGMLChunkWriter;
import org.citygml4j.xml.writer.CityGMLOutputFactory;
import org.opensourcebim.levelout.samples.CitygmlBuilding;
import org.xmlobjects.gml.model.feature.BoundingShape;
import org.xmlobjects.gml.model.geometry.Envelope;

import de.topobyte.osm4j.core.access.OsmOutputStream;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.xml.output.OsmXmlOutputStream;

public class GenericBuilding {

	 
	
	 static FootPrint fp;
	 String fileName2 = "output/osmoutputnew.osm";
	
	 
		OsmOutputStream osmOutput;
	//static GenericNode gn;
	//static GenericPolygon pn;
		
	public GenericBuilding(FootPrint fp) {
		super();
		this.fp = fp;
	}
	

	public GenericBuilding() {
		// TODO Auto-generated constructor stub
	}


	public static void main(String[] args) throws Exception {
		new GenericBuilding(fp).createCitygmlBuilding();
	}
	public void createCitygmlBuilding()  throws Exception {
	
		String fileName = "output/out12.gml";
		CityGMLContext context = CityGMLContext.newInstance();
		Building b = fp.setLodgeom();
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
		
		
		System.out.println("written");
		for (int i=0;i<fp.getPolygonList().size();i++)
		{
		OsmWay way = fp.getPolygonList().get(i).createosmWay();
		}
		
		//osmOutput.write(way); // do we need to write both ways and nodes?
		//osmOutput.write(pn.createosmWay());

	}
	
	/*
	
	public void createIndoorgmlBuilding() throws FileNotFoundException
	{
		String fileName = "output/outindoor5.gml";
		FileOutputStream fout = new FileOutputStream(fileName);
	}*/
}
