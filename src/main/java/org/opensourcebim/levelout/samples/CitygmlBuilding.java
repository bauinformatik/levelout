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
import org.xmlobjects.gml.model.base.AbstractGML;
import org.xmlobjects.gml.model.feature.BoundingShape;
import org.xmlobjects.gml.model.geometry.Envelope;
import org.xmlobjects.gml.model.geometry.aggregates.MultiSurfaceProperty;
import org.xmlobjects.gml.model.geometry.primitives.*;
import org.xmlobjects.gml.util.id.DefaultIdCreator;
import org.xmlobjects.gml.util.id.IdCreator;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class CitygmlBuilding {

	private IdCreator id;
	private GeometryFactory geom;

	public static void main(String[] args) throws Exception {
		new CitygmlBuilding().doMain();
	}

	public void doMain() throws Exception {
		String fileName = "output/out10.gml";
		
		CityGMLContext context = CityGMLContext.newInstance();

		id = DefaultIdCreator.getInstance();
		geom = GeometryFactory.newInstance().withIdCreator(id);

		Building building = new Building();
		
		
		
		Scanner sc2 = new Scanner(System.in);
		
		
		HashMap<String,Polygon> Polygons = new HashMap<String, Polygon>();
		
		System.out.println("Enter the number of polygons ");

		int num = sc2.nextInt();
	
		for (int i=0;i<num;i++)
		{
			Scanner sc = new Scanner(System.in);
			System.out.println("Enter the name of the Polygon");
			String name = sc.nextLine();
			System.out.println("Enter polygon dimesnsions");
			Polygon p1 =  createPoly(createDouble(), sc.nextInt());  	
			Polygons.put(name, p1);
			building.addBoundary(createBoundary(name,p1));
			
		}
	
		 Collection<Polygon> values = Polygons.values(); // for passing in stream
		 
		 List<Polygon> listOfpolyValues = new ArrayList<>(values); 
		 
		
		 
	/*	System.out.println("Input the dimensions of the Polygon ");
		Polygon ground =  createPoly(createDouble(), sc.nextInt());                                                         //geom.createPolygon(new double[] { 0, 0, 0, 0, 6, 0, 10, 6, 0, 10, 0, 0 }, 3);
		Polygon wall_1 =  createPoly(createDouble(), sc.nextInt()); 																								//geom.createPolygon(new double[] { 0, 0, 0, 0, 0, 3, 0, 6, 3, 0, 6, 0 }, 3);
		Polygon wall_2 =  createPoly(createDouble(), sc.nextInt()); 																											//geom.createPolygon(new double[] { 0, 0, 0, 0, 0, 3, 10, 0, 3, 10, 0, 0 }, 3);
		Polygon wall_3 =  createPoly(createDouble(), sc.nextInt());																											// geom.createPolygon(new double[] { 10, 0, 0, 10, 0, 3, 10, 6, 3, 10, 6, 0 }, 3);
		Polygon wall_4 =  createPoly(createDouble(), sc.nextInt());																												//geom.createPolygon(new double[] { 10, 6, 0, 10, 6, 3, 0, 6, 3, 0, 6, 0 }, 3);
		Polygon wall_5 =  createPoly(createDouble(), sc.nextInt());																												//geom.createPolygon(new double[] { 6, 0, 0, 6, 0, 3, 6, 6, 3, 6, 6, 0 }, 3);
		Polygon wall_6 =  createPoly(createDouble(), sc.nextInt());																												//geom.createPolygon(new double[] { 10, 2, 0, 10, 2, 3, 6, 2, 3, 6, 2, 0 }, 3);
		Polygon roof_1 =  createPoly(createDouble(), sc.nextInt());																												//geom.createPolygon(new double[] { 0, 0, 3, 0, 6, 3, 10, 6, 3, 10, 0, 3 }, 3);

		Polygon ground2 = 	createPoly(createDouble(), sc.nextInt());   																														//geom.createPolygon(new double[] { 0, 0, 3, 0, 6, 3, 10, 6, 3, 10, 0, 3 }, 3);
		Polygon wall_11 = 	createPoly(createDouble(), sc.nextInt());   																														//geom.createPolygon(new double[] { 0, 0, 3, 0, 0, 6, 0, 6, 6, 0, 6, 3 }, 3);
		Polygon wall_22 = 	createPoly(createDouble(), sc.nextInt());   																														//geom.createPolygon(new double[] { 0, 0, 3, 0, 0, 6, 10, 0, 6, 10, 0, 3 }, 3);
		Polygon wall_33 =	createPoly(createDouble(), sc.nextInt());   																														// geom.createPolygon(new double[] { 10, 0, 3, 10, 0, 6, 10, 6, 6, 10, 6, 3 }, 3);
		Polygon wall_44 = 	createPoly(createDouble(), sc.nextInt());   																														//geom.createPolygon(new double[] { 10, 6, 3, 10, 6, 6, 0, 6, 6, 0, 6, 3 }, 3);
		Polygon wall_55 = 	createPoly(createDouble(), sc.nextInt());   																														//geom.createPolygon(new double[] { 6, 0, 3, 6, 0, 6, 6, 6, 6, 6, 6, 3 }, 3);
		Polygon wall_66 =	createPoly(createDouble(), sc.nextInt());   																														// geom.createPolygon(new double[] { 10, 2, 3, 10, 2, 6, 6, 2, 6, 6, 2, 3 }, 3);
		Polygon roof_11 = 	createPoly(createDouble(), sc.nextInt());   																														//geom.createPolygon(new double[] { 0, 0, 6, 0, 6, 6, 10, 6, 6, 10, 0, 6 }, 3);

		building.addBoundary(processBoundarySurface(new GroundSurface(), ground));
		building.addBoundary(processBoundarySurface(new WallSurface(), wall_1));
		building.addBoundary(processBoundarySurface(new WallSurface(), wall_2));
		building.addBoundary(processBoundarySurface(new WallSurface(), wall_3));
		building.addBoundary(processBoundarySurface(new WallSurface(), wall_4));
		building.addBoundary(processBoundarySurface(new WallSurface(), wall_5));
		building.addBoundary(processBoundarySurface(new WallSurface(), wall_6));
		building.addBoundary(processBoundarySurface(new CeilingSurface(), roof_1));

		building.addBoundary(processBoundarySurface(new GroundSurface(), ground2));
		building.addBoundary(processBoundarySurface(new WallSurface(), wall_11));
		building.addBoundary(processBoundarySurface(new WallSurface(), wall_22));
		building.addBoundary(processBoundarySurface(new WallSurface(), wall_33));
		building.addBoundary(processBoundarySurface(new WallSurface(), wall_44));
		building.addBoundary(processBoundarySurface(new WallSurface(), wall_55));
		building.addBoundary(processBoundarySurface(new WallSurface(), wall_66));
		building.addBoundary(processBoundarySurface(new RoofSurface(), roof_11));
*/
		/*Shell shell = new Shell();
		Stream.of(ground, wall_1, wall_2, wall_3, wall_4, wall_5, wall_6, roof_1, ground2, wall_11, wall_22, wall_33,
				wall_44, wall_55, wall_66, roof_11).map(p -> new SurfaceProperty("#" + p.getId()))
				.forEach(shell.getSurfaceMembers()::add);
		building.setLod2Solid(new SolidProperty(new Solid(shell)));*/
		
		
		setLoDgeom(building, listOfpolyValues);
		
		

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

	private void setLoDgeom(Building building, List<Polygon> listOfpolyValues) {
		Shell shell = new Shell();
		for (int j=0;j<listOfpolyValues.size();j++)
		{
			
		Stream.of(listOfpolyValues.get(j)).map(p -> new SurfaceProperty("#" + p.getId()))
				.forEach(shell.getSurfaceMembers()::add);
		}
		building.setLod2Solid(new SolidProperty(new Solid(shell)));
	}
	
	
	public double[] createDouble() {
		double[] doubleList = new double[12];
		Scanner sc2 = new Scanner(System.in);
		for(int i = 0; i < 12; i++) {
		   doubleList[i] = sc2.nextDouble();
		}
		
		return doubleList;
	}
	
	public  AbstractSpaceBoundaryProperty createBoundary(String name,  Polygon polygons) {
		
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
		//System.out.println("Input the dimensions of the Polygon ");
		
		return geom.createPolygon(coordinates, dimension);
	}
	
	

	private AbstractSpaceBoundaryProperty processBoundarySurface(AbstractThematicSurface thematicSurface,
			Polygon... polygons) {
		thematicSurface.setId(id.createId());
		thematicSurface.setLod2MultiSurface(new MultiSurfaceProperty(geom.createMultiSurface(polygons)));
		return new AbstractSpaceBoundaryProperty(thematicSurface);
	}

}