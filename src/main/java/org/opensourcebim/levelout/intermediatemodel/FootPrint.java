package org.opensourcebim.levelout.intermediatemodel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.topobyte.osm4j.core.access.OsmOutputStream;
import org.citygml4j.core.model.building.Building;
import org.opensourcebim.levelout.samples.IndoorGmlBuilding;
import org.xmlobjects.gml.model.geometry.primitives.Polygon;
import org.xmlobjects.gml.model.geometry.primitives.Shell;
import org.xmlobjects.gml.model.geometry.primitives.Solid;
import org.xmlobjects.gml.model.geometry.primitives.SolidProperty;
import org.xmlobjects.gml.model.geometry.primitives.SurfaceProperty;

import de.topobyte.osm4j.core.model.iface.OsmWay;
import net.opengis.indoorgml.core.v_1_0.CellSpaceMemberType;
import net.opengis.indoorgml.core.v_1_0.CellSpaceType;
import net.opengis.indoorgml.core.v_1_0.StateMemberType;
import net.opengis.indoorgml.core.v_1_0.StateType;

public class FootPrint {

	private int level;
	private int id;
	private final List<GenericPolygon> polygonList;
	//private GenericPolygon gp;

	public FootPrint(int level, int id, List<GenericPolygon> polygonList) {
		this.level = level;
		this.id = id;
		this.polygonList = polygonList;
	}

	public void createFootPrint() {

	}

	public Building setLodgeom(Building building) {
		GenericPolygon gp = new GenericPolygon(); // is calling a default constructor ok?
		//CitygmlBuilding cg = new CitygmlBuilding();
		//Building building = new Building();
		List<Polygon> polygonList = new ArrayList<>();
		for (GenericPolygon genericPolygon : this.polygonList) {
			Polygon poly = genericPolygon.createCitygmlPoly(); // to use for shell
			polygonList.add(poly);
			building.addBoundary(gp.createBoundary(genericPolygon.getName(), poly));
		}
		Shell shell = new Shell();
		for (Polygon polygon : polygonList) {
			shell.getSurfaceMembers().add(new SurfaceProperty("#" + polygon.getId()));
		}
		// polygonList.stream().map( p -> new SurfaceProperty("#" + p.getId())).forEach(shell.getSurfaceMembers()::add);
		building.setLod2Solid(new SolidProperty(new Solid(shell)));
		return building;
	}

	public void writeTagswaysOsm(OsmOutputStream osmOutput) throws IOException {
		for (GenericPolygon genericPolygon : polygonList) {
			OsmWay way = genericPolygon.createosmWay(osmOutput); // how to set tags
		}
	}

	public List<List> createIndoorFeatures() {
		List<StateMemberType> states = new ArrayList<>();
		List<CellSpaceMemberType> cellSpaceMembers = new ArrayList<>();
		List<List> totallist = new ArrayList<>();
		for (GenericPolygon genericPolygon : polygonList) {
			CellSpaceType cs = genericPolygon.createIndoorGmlCellSpace();
			StateType st = genericPolygon.setStatePos();

			IndoorGmlBuilding.createCellspaceMember(cs, cellSpaceMembers);
			IndoorGmlBuilding.createStateMember(st, states);
			IndoorGmlBuilding.setDualitycellspace(cs, st);
			IndoorGmlBuilding.setdualityState(cs, st);

			totallist.add(cellSpaceMembers);
			totallist.add(states);
		}
		return totallist;
	}

	public List<GenericPolygon> getPolygonList() {
		return polygonList;
	}

}