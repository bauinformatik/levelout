

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
import org.opensourcebim.levelout.intermediatemodel.Corner;
import org.opensourcebim.levelout.intermediatemodel.Door;
import org.xmlobjects.gml.model.feature.BoundingShape;
import org.xmlobjects.gml.model.geometry.Envelope;
import org.xmlobjects.gml.model.geometry.aggregates.MultiCurve;
import org.xmlobjects.gml.model.geometry.aggregates.MultiCurveProperty;
import org.xmlobjects.gml.model.geometry.aggregates.MultiSurfaceProperty;
import org.xmlobjects.gml.model.geometry.primitives.*;
import org.xmlobjects.gml.util.id.DefaultIdCreator;
import org.xmlobjects.gml.util.id.IdCreator;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class CityGMLWall {

	private IdCreator id;
	private GeometryFactory geom;
	
	public static void main(String[] args) throws Exception {
		new CityGMLWall().doMain();
	}

	public void doMain() throws Exception {
		String fileName = "output/wallfinalsample.gml";
		
		CityGMLContext context = CityGMLContext.newInstance();

		id = DefaultIdCreator.getInstance();
		geom = GeometryFactory.newInstance().withIdCreator(id);

		Building building = new Building();

		List<LineString> lines = new ArrayList<>();
		
		
		List<Double> coordinates = new ArrayList<>();
		coordinates.add(0.);
		coordinates.add(0.);
		coordinates.add(0.);
		coordinates.add(6.);
		coordinates.add(0.);
		coordinates.add(0.);
		
		List<Double> coordinates2 = new ArrayList<>();
		coordinates2.add(6.);
		coordinates2.add(0.);
		coordinates2.add(0.);
		coordinates2.add(6.);
		coordinates2.add(6.);
		coordinates2.add(0.);
		
		List<Double> coordinates3 = new ArrayList<>();
		coordinates3.add(6.);
		coordinates3.add(6.);
		coordinates3.add(0.);
		coordinates3.add(0.);
		coordinates3.add(6.);
		coordinates3.add(0.);
		
		List<Double> coordinates4 = new ArrayList<>();
		coordinates4.add(0.);
		coordinates4.add(6.);
		coordinates4.add(0.);
		coordinates4.add(0.);
		coordinates4.add(0.);
		coordinates4.add(0.);
		
		
	
			LineString l1 =  geom.createLineString(coordinates, 3);
			LineString l2 =  geom.createLineString(coordinates2, 3);
			LineString l3 =  geom.createLineString(coordinates3, 3);
			LineString l4 =  geom.createLineString(coordinates4, 3);
			
			lines.add(l1);
			lines.add(l2);
			lines.add(l3);
			lines.add(l4);
			
		
			
			building.addBoundary(createBoundary("wall",l1));
			building.addBoundary(createBoundary("wall",l2));
			building.addBoundary(createBoundary("wall",l3));
			building.addBoundary(createBoundary("wall",l4));
			
			
			setLoDgeomd(building, lines);
			Envelope envelope = building.computeEnvelope();

			CityGMLVersion version = CityGMLVersion.v3_0;
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
	
	
	
	
		private AbstractSpaceBoundaryProperty createBoundary(String name, LineString l1) {
							return processBoundarySurface(new WallSurface(), l1);
			
			}
	

		private AbstractSpaceBoundaryProperty processBoundarySurface(WallSurface wallSurface, LineString ls) {
			wallSurface.setId(id.createId());
			wallSurface.setLod0MultiCurve(new MultiCurveProperty(geom.createMultiCurve(ls)));
			return new AbstractSpaceBoundaryProperty(wallSurface);
		}

		
	


	

private void setLoDgeomd(Building building, List<LineString> linestrings) {
	
	 List<CurveProperty> curveMember = new ArrayList<>();
      for (LineString line : linestrings) {

      	curveMember.add(new CurveProperty("#" + line.getId()));
		}
      
      for(CurveProperty curveprop : curveMember)
      {
    	  System.out.println(curveprop);
      }
    
      	building. setLod0MultiCurve(new MultiCurveProperty(new MultiCurve(curveMember)));
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
		bsp =  processBoundarySurface(new WallSurface(), polygons);
	}
	else if (name.contains("roof"))
	{
		bsp = processBoundarySurface(new RoofSurface(), polygons);
	}
	else if (name.contains("ceiling"))
	{
		bsp = processBoundarySurface(new CeilingSurface(), polygons);
	}
	else if (name.contains("floor"))
	{
		bsp = processBoundarySurface(new FloorSurface(), polygons);
	}
	return bsp;

	}
	
	private Polygon createPoly(double[] coordinates, int dimension) {
		return geom.createPolygon(coordinates, dimension);
	}
	private LineString createLine(List<Double> coordinates, int dimension) {
		return geom.createLineString(coordinates, dimension);
		
		
	}

	private AbstractSpaceBoundaryProperty processBoundarySurface(AbstractThematicSurface thematicSurface,
			Polygon... polygons) {
		thematicSurface.setId(id.createId());
		thematicSurface.setLod0MultiSurface(new MultiSurfaceProperty(geom.createMultiSurface(polygons)));
		return new AbstractSpaceBoundaryProperty(thematicSurface);
	}

}