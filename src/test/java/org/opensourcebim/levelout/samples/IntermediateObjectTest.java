package org.opensourcebim.levelout.samples;

import org.citygml4j.xml.CityGMLContextException;
import org.citygml4j.xml.writer.CityGMLWriteException;
import org.junit.Before;
import org.junit.Test;
import org.opensourcebim.levelout.builders.CityGmlBuilder;
import org.opensourcebim.levelout.builders.IndoorGmlBuilder;
import org.opensourcebim.levelout.builders.OsmBuilder;
import org.opensourcebim.levelout.intermediatemodel.Building;
import org.opensourcebim.levelout.intermediatemodel.geo.CoordinateReference;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class IntermediateObjectTest {

	private final Path intermediate = Paths.get("output/residential.obj");
	Building building;
	CoordinateReference crs;

	@Before
	public void setup() throws IOException, ClassNotFoundException, URISyntaxException {
		Path test = intermediate.toFile().exists() ? intermediate : Path.of(getClass().getResource("residential.obj").toURI());
		ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(test.toFile()));
		building = (Building) objectInputStream.readObject();
		crs = (CoordinateReference) objectInputStream.readObject();
	}

	@Test
	public void testOsmFromFile() throws IOException {
		new OsmBuilder().createAndWriteBuilding(building, crs, getOutputPath(".osm"));
	}

	@Test
	public void testIndoorFromFile() throws FileNotFoundException, JAXBException {
		new IndoorGmlBuilder().createAndWriteBuilding(building, getOutputPath(".indoor.gml"));
	}

	@Test
	public void testCityGmlFromFile() throws FileNotFoundException, CityGMLWriteException, CityGMLContextException {
		new CityGmlBuilder().createAndWriteBuilding(building, getOutputPath(".city.gml"));
	}
	private FileOutputStream getOutputPath(String extension) throws FileNotFoundException {
		Path outputPath = Paths.get("output").resolve(intermediate.getFileName().toFile().getName() + extension);
		return new FileOutputStream(outputPath.toFile());
	}

}
