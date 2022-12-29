
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

public class CityGMLDoor {

	private IdCreator id;
	private GeometryFactory geom;
	
	public static void main(String[] args) throws Exception {
		new CityGMLDoor().doMain();
	}

	public void doMain() throws Exception {
		String fileName = "output/doorsample11.gml";
		
		CityGMLContext context = CityGMLContext.newInstance();

		id = DefaultIdCreator.getInstance();
		geom = GeometryFactory.newInstance().withIdCreator(id);

		Building building = new Building();

		List<LineString> lines = new ArrayList<>();
		
		
		List<Double> coordinates = new ArrayList<>();
		coordinates.add(1.);
		coordinates.add(0.);
		coordinates.add(0.);
		coordinates.add(2.);
		coordinates.add(0.);
		coordinates.add(0.);
		
		List<Double> coordinates2 = new ArrayList<>();
		coordinates2.add(6.);
		coordinates2.add(5.);
		coordinates2.add(0.);
		coordinates2.add(6.);
		coordinates2.add(6.);
		coordinates2.add(0.);
		
		
	
			LineString l1 =  geom.createLineString(coordinates, 3);
			LineString l2 =  geom.createLineString(coordinates2, 3);
			lines.add(l1);
			lines.add(l2);
			
			WallSurface wall = new WallSurface();
	
			wall.setFillingSurfaces(Arrays.asList(createBoundary("door",l1),createBoundary("door",l2)));
			
			
		building.addBoundary(new AbstractSpaceBoundaryProperty(wall));
			//building.addBoundary(createBoundary("door",l2));
			
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
	
	
	
	
		private AbstractFillingSurfaceProperty createBoundary(String name, LineString l1) {
							return processBoundarySurface(new DoorSurface(), l1);
			
			}
	

		private AbstractFillingSurfaceProperty processBoundarySurface(DoorSurface thematicSurface, LineString l1) {
			thematicSurface.setId(id.createId());
			thematicSurface.setLod0MultiCurve(new MultiCurveProperty(geom.createMultiCurve(l1)));
			return new AbstractFillingSurfaceProperty(thematicSurface);
					//AbstractSpaceBoundaryProperty(thematicSurface);
		}

		
	


	

private void setLoDgeomd(Building building, List<LineString> lines) {
	
	 List<CurveProperty> curveMember = new ArrayList<>();
      for (LineString line : lines) {

      	curveMember.add(new CurveProperty("#" + line.getId()));
		}
      
      for(CurveProperty curveprop : curveMember)
      {
    	  System.out.println(curveprop);
      }
    
      	building. setLod0MultiCurve(new MultiCurveProperty(new MultiCurve(curveMember)));
}
	

	
	
	

}