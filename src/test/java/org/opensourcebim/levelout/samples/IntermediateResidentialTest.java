package org.opensourcebim.levelout.samples;

import org.citygml4j.xml.CityGMLContextException;
import org.citygml4j.xml.writer.CityGMLWriteException;
import org.junit.Before;
import org.junit.Test;
import org.opensourcebim.levelout.builders.CityGmlBuilder;
import org.opensourcebim.levelout.builders.IndoorGmlBuilder;
import org.opensourcebim.levelout.builders.OsmBuilder;
import org.opensourcebim.levelout.intermediatemodel.Building;
import org.opensourcebim.levelout.intermediatemodel.geo.*;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class IntermediateResidentialTest {
	private CoordinateReference crs;
	private Building building;

	@Before
	public void setup(){
		building = IntermediateResidential.create();
		if(!new File("output").exists()){
			if(!new File("output").mkdir()){
				return;
			}
		}
		crs = new GeodeticOriginCRS(new GeodeticPoint(50.9772, 11.3465, 0), 0.25);

		// crs = new GeodeticOriginCRS(new GeodeticPoint(53.320555, -1.729000, 0), -0.13918031137);
		// crs = new ProjectedOriginCRS(new ProjectedPoint(333780.622, 6246775.891, 0), 0.990330045, -0.138731399, "epsg:28356");

	}

	@Test
	public void testCityGml() throws FileNotFoundException, CityGMLWriteException, CityGMLContextException {
		new CityGmlBuilder().createAndWriteBuilding(building, new FileOutputStream("output/test-city.gml"));
	}
	@Test
	public void testIndoorGml() throws FileNotFoundException, JAXBException {
		new IndoorGmlBuilder().createAndWriteBuilding(building, new FileOutputStream("output/test-indoor.gml"));
	}
	@Test
	public void testOsm() throws IOException {
		new OsmBuilder().createAndWriteBuilding(building, crs, new FileOutputStream("output/test.osm"));
	}
}
