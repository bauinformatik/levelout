package org.opensourcebim.levelout.intermediatemodel;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.citygml4j.core.model.building.Building;
import org.opensourcebim.levelout.samples.CitygmlBuilding;
import org.xmlobjects.gml.model.geometry.primitives.Polygon;
import org.xmlobjects.gml.model.geometry.primitives.Shell;
import org.xmlobjects.gml.model.geometry.primitives.Solid;
import org.xmlobjects.gml.model.geometry.primitives.SolidProperty;
import org.xmlobjects.gml.model.geometry.primitives.SurfaceProperty;

import de.topobyte.osm4j.core.access.OsmOutputStream;
import de.topobyte.osm4j.xml.output.OsmXmlOutputStream;

public class FootPrint {

	private int level;
	private int id;
	private List<GenericPolygon> polygonList;

	public FootPrint(int level, int id, List<GenericPolygon> polygonList) {
		super();
		this.level = level;
		this.id = id;
		this.polygonList = polygonList;
	}

	public void createFootPrint() {

	}

	public Building setLodgeom()
	{
		
		CitygmlBuilding cg = new CitygmlBuilding();
		Building building = new Building();
		List<Polygon> listOfpolyValues = new ArrayList<>(); 
		for (int i =0;i<polygonList.size();i++)
		{
			Polygon poly = polygonList.get(i).createCitygmlPoly(); // to use for shell
			listOfpolyValues.add(poly);
			building.addBoundary(cg.createBoundary(polygonList.get(i).getName(), poly));  
		}
		Shell shell = new Shell();
		for (int j=0;j<listOfpolyValues.size();j++)
		{
		Stream.of(listOfpolyValues.get(j)).map(p -> new SurfaceProperty("#" + p.getId()))
				.forEach(shell.getSurfaceMembers()::add);
		}
		building.setLod2Solid(new SolidProperty(new Solid(shell)));
		
		return building;
	}

	public void writeTagswaysOsm() {

	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<GenericPolygon> getPolygonList() {
		return polygonList;
	}

	public void setPolygonList(List<GenericPolygon> polygonList) {
		this.polygonList = polygonList;
	}

}
