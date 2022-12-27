package org.opensourcebim.levelout.samples;

import org.citygml4j.core.model.CityGMLVersion;
import org.citygml4j.core.model.building.Building;
import org.citygml4j.core.model.construction.*;
import org.citygml4j.core.model.core.AbstractSpaceBoundaryProperty;
import org.citygml4j.core.model.core.AbstractThematicSurface;
import org.citygml4j.core.util.geometry.GeometryFactory;
import org.citygml4j.xml.CityGMLContext;
import org.citygml4j.xml.module.citygml.CoreModule;
import org.citygml4j.xml.writer.CityGMLChunkWriter;
import org.citygml4j.xml.writer.CityGMLOutputFactory;
import org.xmlobjects.gml.model.feature.BoundingShape;
import org.xmlobjects.gml.model.geometry.Envelope;
import org.xmlobjects.gml.model.geometry.aggregates.MultiSurfaceProperty;
import org.xmlobjects.gml.model.geometry.primitives.*;
import org.xmlobjects.gml.util.id.DefaultIdCreator;
import org.xmlobjects.gml.util.id.IdCreator;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CitygmlBuilding {

	private IdCreator id;
	private GeometryFactory geom;
	private final Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) throws Exception {
		new CitygmlBuilding().doMain();
	}

	public void doMain() throws Exception {
		String fileName = "output/out120.gml";
		
		CityGMLContext context = CityGMLContext.newInstance();

		id = DefaultIdCreator.getInstance();
		geom = GeometryFactory.newInstance().withIdCreator(id);

		Building building = new Building();

		List<Polygon> polygons = new ArrayList<>();
		
		System.out.println("Enter the number of polygons ");

		int num = scanner.nextInt();
	
		for (int i=0;i<num;i++) {
			Scanner sc = new Scanner(System.in);
			System.out.println("Enter the name of the Polygon");
			String name = sc.nextLine();
			System.out.println("Enter polygon dimensions");
			Polygon p1 =  createPoly(createDouble(), sc.nextInt());  	
			polygons.add(p1);
			building.addBoundary(createBoundary(name,p1));
		}
	
		setLoDgeom(building, polygons);

		Envelope envelope = building.computeEnvelope();

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
			writer.writeMember(building);
		}

	}

	private void setLoDgeom(Building building, List<Polygon> polygons) {
		Shell shell = new Shell();
		for (Polygon polygon : polygons) {
			shell.getSurfaceMembers().add(new SurfaceProperty("#" + polygon.getId()));
		}
		building.setLod2Solid(new SolidProperty(new Solid(shell)));
	}
	
	
	private double[] createDouble() {
		double[] doubleList = new double[12];
		Scanner sc2 = new Scanner(System.in);
		for(int i = 0; i < 12; i++) {
		   doubleList[i] = sc2.nextDouble();
		}
		
		return doubleList;
	}
	
	private AbstractSpaceBoundaryProperty createBoundary(String name,  Polygon polygons) {
		
		AbstractSpaceBoundaryProperty bsp = null;
	if (name.contains("ground"))
	{
		bsp = processBoundarySurface(new GroundSurface(), polygons);
	}
	else if (name.contains("wall"))
	{
		bsp=  processBoundarySurface(new WallSurface(), polygons);
	}
	else if (name.contains("roof"))
	{
		bsp= processBoundarySurface(new RoofSurface(), polygons);
	}
	else if (name.contains("ceiling"))
	{
		bsp= processBoundarySurface(new CeilingSurface(), polygons);
	}
	else if (name.contains("floor"))
	{
		bsp= processBoundarySurface(new FloorSurface(), polygons);
	}
	return bsp;

	}
	
	private Polygon createPoly(double[] coordinates, int dimension) {
		return geom.createPolygon(coordinates, dimension);
	}

	private AbstractSpaceBoundaryProperty processBoundarySurface(AbstractThematicSurface thematicSurface,
			Polygon... polygons) {
		thematicSurface.setId(id.createId());
		thematicSurface.setLod0MultiSurface(new MultiSurfaceProperty(geom.createMultiSurface(polygons)));
		return new AbstractSpaceBoundaryProperty(thematicSurface);
	}

}