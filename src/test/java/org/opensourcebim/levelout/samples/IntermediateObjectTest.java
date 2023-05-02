package org.opensourcebim.levelout.samples;

import org.citygml4j.xml.CityGMLContextException;
import org.citygml4j.xml.writer.CityGMLWriteException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opensourcebim.levelout.builders.CityGmlBuilder;
import org.opensourcebim.levelout.builders.IndoorGmlBuilder;
import org.opensourcebim.levelout.builders.OsmBuilder;
import org.opensourcebim.levelout.intermediatemodel.Building;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class IntermediateObjectTest {

	private static final List<File> intermediate = new ArrayList<>();
	private static final Path path = Paths.get("output");
	private Building building;

	@BeforeClass
	public static void setup(){
		if(path.toFile().exists()) {
			intermediate.addAll(List.of(path.toFile().listFiles((dir, name) -> name.endsWith(".obj"))));
		}
	}
	@Test
	public void testOsmFromFile() throws IOException, ClassNotFoundException {
		for(File file: intermediate){
			setup(file);
			new OsmBuilder().createAndWriteBuilding(building, getOutputStream(".osm", file));
		}
	}

	@Test
	public void testIndoorFromFile() throws IOException, JAXBException, ClassNotFoundException {
		for(File file: intermediate) {
			setup(file);
			new IndoorGmlBuilder().createAndWriteBuilding(building, getOutputStream(".indoor.gml", file));
		}
	}

	@Test
	public void testCityGmlFromFile() throws IOException, CityGMLWriteException, CityGMLContextException, ClassNotFoundException, ParserConfigurationException {
		for(File file: intermediate) {
			setup(file);
			new CityGmlBuilder().createAndWriteBuilding(building, getOutputStream(".city.gml", file));
		}
	}

	private void setup(File file) throws IOException, ClassNotFoundException {
		ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
		building = (Building) objectInputStream.readObject();
	}

	private FileOutputStream getOutputStream(String extension, File file) throws FileNotFoundException {
		Path outputPath = path.resolve(file.getName() + extension);
		return new FileOutputStream(outputPath.toFile());
	}

}
