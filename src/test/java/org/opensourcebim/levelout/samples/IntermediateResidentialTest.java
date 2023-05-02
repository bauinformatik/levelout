package org.opensourcebim.levelout.samples;

import org.citygml4j.xml.CityGMLContextException;
import org.citygml4j.xml.writer.CityGMLWriteException;
import org.junit.Before;
import org.junit.Test;
import org.opensourcebim.levelout.builders.CityGmlBuilder;
import org.opensourcebim.levelout.builders.IndoorGmlBuilder;
import org.opensourcebim.levelout.builders.OsmBuilder;
import org.opensourcebim.levelout.intermediatemodel.Building;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class IntermediateResidentialTest {
	private Building building;

	@Before
	public void setup(){
		building = IntermediateResidential.create();
		if(!new File("output").exists()){
			new File("output").mkdir();
		}
	}

	@Test
	public void testCityGml() throws FileNotFoundException, CityGMLWriteException, CityGMLContextException, ParserConfigurationException {
		new CityGmlBuilder().createAndWriteBuilding(building, new FileOutputStream("output/test-city.gml"));
	}
	@Test
	public void testIndoorGml() throws FileNotFoundException, JAXBException {
		new IndoorGmlBuilder().createAndWriteBuilding(building, new FileOutputStream("output/test-indoor.gml"));
	}
	@Test
	public void testOsm() throws IOException {
		new OsmBuilder().createAndWriteBuilding(building, new FileOutputStream("output/test.osm"));
	}
}
