package org.opensourcebim.levelout.intermediatemodel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.topobyte.osm4j.core.access.OsmOutputStream;
import org.citygml4j.core.model.building.Building;
import org.opensourcebim.levelout.samples.IndoorGmlResidential;
import org.xmlobjects.gml.model.geometry.aggregates.MultiSurface;
import org.xmlobjects.gml.model.geometry.aggregates.MultiSurfaceProperty;
import org.xmlobjects.gml.model.geometry.primitives.Polygon;
import org.xmlobjects.gml.model.geometry.primitives.SurfaceProperty;

import net.opengis.indoorgml.core.v_1_0.CellSpaceMemberType;
import net.opengis.indoorgml.core.v_1_0.CellSpaceType;
import net.opengis.indoorgml.core.v_1_0.StateMemberType;
import net.opengis.indoorgml.core.v_1_0.StateType;

public class Storey {

	private int level;
	private long id;
	private final List<Room> polygonList;
	//private GenericPolygon gp;

	public Storey(int level, int id, List<Room> polygonList) {
		this.level = level;
		this.id = id;
		this.polygonList = polygonList;
	}

	public void createFootPrint() {

	}

	public void setLodgeom(Building building) {
		List<Polygon> polygonList = new ArrayList<>();
		for (Room genericPolygon : this.polygonList) {
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
		for (Room genericPolygon : polygonList) {
			genericPolygon.createosmWay(osmOutput); // how to set tags
		}
	}

	public void createIndoorFeatures(List<StateMemberType> stateMembers, List<CellSpaceMemberType> cellSpaceMembers) {
		for (Room genericPolygon : polygonList) {
			CellSpaceType cs = genericPolygon.createIndoorGmlCellSpace();
			IndoorGmlResidential.createCellSpaceMember(cs, cellSpaceMembers);

			StateType st = genericPolygon.createIndoorGmlState();
			IndoorGmlResidential.createStateMember(st, stateMembers);

			IndoorGmlResidential.setDualityCellSpace(cs, st);
			IndoorGmlResidential.setDualityState(st, cs);
		}
	}

	public List<Room> getPolygonList() {
		return polygonList;
	}
}