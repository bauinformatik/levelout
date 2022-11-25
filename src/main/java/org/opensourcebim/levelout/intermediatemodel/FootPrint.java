package org.opensourcebim.levelout.intermediatemodel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import de.topobyte.osm4j.core.access.OsmOutputStream;
import org.citygml4j.core.model.building.Building;
import org.opensourcebim.levelout.samples.IndoorGmlBuilding;
import org.xmlobjects.gml.model.geometry.aggregates.MultiSurface;
import org.xmlobjects.gml.model.geometry.aggregates.MultiSurfaceProperty;
import org.xmlobjects.gml.model.geometry.primitives.Polygon;
import org.xmlobjects.gml.model.geometry.primitives.Shell;
import org.xmlobjects.gml.model.geometry.primitives.Solid;
import org.xmlobjects.gml.model.geometry.primitives.SolidProperty;
import org.xmlobjects.gml.model.geometry.primitives.Surface;
import org.xmlobjects.gml.model.geometry.primitives.SurfaceProperty;

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

	public void setLodgeom(Building building) {
		List<Polygon> polygonList = new ArrayList<>();
		for (GenericPolygon genericPolygon : this.polygonList) {
			Polygon poly = genericPolygon.createCitygmlPoly(); // to use for shell
			polygonList.add(poly);
			building.addBoundary(genericPolygon.createBoundary(poly));
		}
		
		
		
	
	   
	        List<SurfaceProperty> surfaceMember = new ArrayList<>();
	        for (Polygon polygon : polygonList) {

	        	surfaceMember.add(new SurfaceProperty("#" + polygon.getId()));
			}
	      
	        	building. setLod0MultiSurface(new MultiSurfaceProperty(new MultiSurface(surfaceMember)));
	       
	}
	        

	       
	       
	/*	Shell shell = new Shell();
		for (Polygon polygon : polygonList) {
			shell.getSurfaceMembers().add(new SurfaceProperty("#" + polygon.getId()));
		}
		// polygonList.stream().map( p -> new SurfaceProperty("#" + p.getId())).forEach(shell.getSurfaceMembers()::add);
		building.setLod2Solid(new SolidProperty(new Solid(shell)));
	}*/

	public void writeTagswaysOsm(OsmOutputStream osmOutput) throws IOException {
		for (GenericPolygon genericPolygon : polygonList) {
			genericPolygon.createosmWay(osmOutput); // how to set tags
		}
	}

	public void createIndoorFeatures(List<StateMemberType> stateMembers, List<CellSpaceMemberType> cellSpaceMembers) {
		for (GenericPolygon genericPolygon : polygonList) {
			CellSpaceType cs = genericPolygon.createIndoorGmlCellSpace();
			IndoorGmlBuilding.createCellspaceMember(cs, cellSpaceMembers);

			StateType st = genericPolygon.createIndoorGmlState();
			IndoorGmlBuilding.createStateMember(st, stateMembers);

			IndoorGmlBuilding.setDualitycellspace(cs, st);
			IndoorGmlBuilding.setdualityState(cs, st);
		}
	}

	public List<GenericPolygon> getPolygonList() {
		return polygonList;
	}
}