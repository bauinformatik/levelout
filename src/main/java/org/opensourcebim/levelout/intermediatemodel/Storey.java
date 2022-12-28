package org.opensourcebim.levelout.intermediatemodel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.topobyte.osm4j.core.access.OsmOutputStream;
import org.citygml4j.core.model.building.Building;
import org.opensourcebim.levelout.samples.IndoorGmlResidential;
import org.xmlobjects.gml.model.geometry.aggregates.MultiCurve;
import org.xmlobjects.gml.model.geometry.aggregates.MultiCurveProperty;
import org.xmlobjects.gml.model.geometry.aggregates.MultiSurface;
import org.xmlobjects.gml.model.geometry.aggregates.MultiSurfaceProperty;
import org.xmlobjects.gml.model.geometry.primitives.CurveProperty;
import org.xmlobjects.gml.model.geometry.primitives.LineString;
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
	private final List<Door> DoorList;
	
	//private GenericPolygon gp;

	public Storey(int level, int id, List<Room> polygonList, List<Door> doorList) {
		this.level = level;
		this.id = id;
		this.polygonList = polygonList;
		this.DoorList = doorList;
	}

	public void createFootPrint() {

	}

	public void setLodgeom(Building building) {
		List<Polygon> polygonList = new ArrayList<>();
		List<LineString> LineStringList = new ArrayList<>();
		for (Room genericPolygon : this.polygonList) {
			Polygon poly = genericPolygon.createCitygmlPoly(); // to use for shell
			polygonList.add(poly);
			building.addBoundary(genericPolygon.createBoundary(poly));
		}
		for (Door genericPolygon : this.DoorList) {
			LineString line = genericPolygon.createCitygmlLines(); // to use for shell
			LineStringList.add(line);
			building.addBoundary(genericPolygon.createBoundaryLine(line));
		}
		
		
		
	
	   
	        List<SurfaceProperty> surfaceMember = new ArrayList<>();
	        for (Polygon polygon : polygonList) {

	        	surfaceMember.add(new SurfaceProperty("#" + polygon.getId()));
			}
	      
	        	building. setLod0MultiSurface(new MultiSurfaceProperty(new MultiSurface(surfaceMember)));
	        	
	        	
	        	
	        	
	        	 List<CurveProperty> curveMember = new ArrayList<>();
	 	        for (LineString line : LineStringList) {

	 	        	curveMember.add(new CurveProperty("#" + line.getId()));
	 			}
	 	      
	 	        	building. setLod0MultiCurve(new MultiCurveProperty(new MultiCurve(curveMember)));
	       
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
	public List<Door> getDoorList() {
		return DoorList;
	}

}