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
import java.util.stream.Stream;

public class CitygmlBuilding {

	private IdCreator id;
	private GeometryFactory geom;

	public static void main(String[] args) throws Exception {
		new CitygmlBuilding().doMain();
	}

	public void doMain() throws Exception {
		String fileName = "output/out56.gml";
		
		CityGMLContext context = CityGMLContext.newInstance();

		id = DefaultIdCreator.getInstance();
		geom = GeometryFactory.newInstance().withIdCreator(id);

		Building building = new Building();

		Polygon ground = geom.createPolygon(new double[] { 0, 0, 0, 0, 6, 0, 10, 6, 0, 10, 0, 0 }, 3);
		Polygon wall_1 = geom.createPolygon(new double[] { 0, 0, 0, 0, 0, 3, 0, 6, 3, 0, 6, 0 }, 3);
		Polygon wall_2 = geom.createPolygon(new double[] { 0, 0, 0, 0, 0, 3, 10, 0, 3, 10, 0, 0 }, 3);
		Polygon wall_3 = geom.createPolygon(new double[] { 10, 0, 0, 10, 0, 3, 10, 6, 3, 10, 6, 0 }, 3);
		Polygon wall_4 = geom.createPolygon(new double[] { 10, 6, 0, 10, 6, 3, 0, 6, 3, 0, 6, 0 }, 3);
		Polygon wall_5 = geom.createPolygon(new double[] { 6, 0, 0, 6, 0, 3, 6, 6, 3, 6, 6, 0 }, 3);
		Polygon wall_6 = geom.createPolygon(new double[] { 10, 2, 0, 10, 2, 3, 6, 2, 3, 6, 2, 0 }, 3);
		Polygon roof_1 = geom.createPolygon(new double[] { 0, 0, 3, 0, 6, 3, 10, 6, 3, 10, 0, 3 }, 3);

		Polygon ground2 = geom.createPolygon(new double[] { 0, 0, 3, 0, 6, 3, 10, 6, 3, 10, 0, 3 }, 3);
		Polygon wall_11 = geom.createPolygon(new double[] { 0, 0, 3, 0, 0, 6, 0, 6, 6, 0, 6, 3 }, 3);
		Polygon wall_22 = geom.createPolygon(new double[] { 0, 0, 3, 0, 0, 6, 10, 0, 6, 10, 0, 3 }, 3);
		Polygon wall_33 = geom.createPolygon(new double[] { 10, 0, 3, 10, 0, 6, 10, 6, 6, 10, 6, 3 }, 3);
		Polygon wall_44 = geom.createPolygon(new double[] { 10, 6, 3, 10, 6, 6, 0, 6, 6, 0, 6, 3 }, 3);
		Polygon wall_55 = geom.createPolygon(new double[] { 6, 0, 3, 6, 0, 6, 6, 6, 6, 6, 6, 3 }, 3);
		Polygon wall_66 = geom.createPolygon(new double[] { 10, 2, 3, 10, 2, 6, 6, 2, 6, 6, 2, 3 }, 3);
		Polygon roof_11 = geom.createPolygon(new double[] { 0, 0, 6, 0, 6, 6, 10, 6, 6, 10, 0, 6 }, 3);

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

		Shell shell = new Shell();
		Stream.of(ground, wall_1, wall_2, wall_3, wall_4, wall_5, wall_6, roof_1, ground2, wall_11, wall_22, wall_33,
				wall_44, wall_55, wall_66, roof_11).map(p -> new SurfaceProperty("#" + p.getId()))
				.forEach(shell.getSurfaceMembers()::add);
		building.setLod2Solid(new SolidProperty(new Solid(shell)));

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

	private AbstractSpaceBoundaryProperty processBoundarySurface(AbstractThematicSurface thematicSurface,
			Polygon... polygons) {
		thematicSurface.setId(id.createId());
		thematicSurface.setLod2MultiSurface(new MultiSurfaceProperty(geom.createMultiSurface(polygons)));
		return new AbstractSpaceBoundaryProperty(thematicSurface);
	}

}