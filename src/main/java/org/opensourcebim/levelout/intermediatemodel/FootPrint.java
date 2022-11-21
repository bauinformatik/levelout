package org.opensourcebim.levelout.intermediatemodel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import de.topobyte.osm4j.core.access.OsmOutputStream;
import de.topobyte.osm4j.core.model.impl.Way;
import org.citygml4j.core.model.building.Building;
import org.opensourcebim.levelout.samples.IndoorGmlBuilding;
import org.opensourcebim.levelout.samples.OsmBuilding;
import org.xmlobjects.gml.model.geometry.primitives.Polygon;
import org.xmlobjects.gml.model.geometry.primitives.Shell;
import org.xmlobjects.gml.model.geometry.primitives.Solid;
import org.xmlobjects.gml.model.geometry.primitives.SolidProperty;
import org.xmlobjects.gml.model.geometry.primitives.SurfaceProperty;

import de.topobyte.osm4j.core.model.iface.OsmWay;
import net.opengis.indoorgml.core.v_1_0.CellSpaceMemberType;
import net.opengis.indoorgml.core.v_1_0.CellSpaceType;
import net.opengis.indoorgml.core.v_1_0.IndoorFeaturesType;
import net.opengis.indoorgml.core.v_1_0.MultiLayeredGraphPropertyType;
import net.opengis.indoorgml.core.v_1_0.MultiLayeredGraphType;
import net.opengis.indoorgml.core.v_1_0.NodesType;
import net.opengis.indoorgml.core.v_1_0.PrimalSpaceFeaturesPropertyType;
import net.opengis.indoorgml.core.v_1_0.PrimalSpaceFeaturesType;
import net.opengis.indoorgml.core.v_1_0.SpaceLayerMemberType;
import net.opengis.indoorgml.core.v_1_0.SpaceLayerType;
import net.opengis.indoorgml.core.v_1_0.SpaceLayersType;
import net.opengis.indoorgml.core.v_1_0.StateMemberType;
import net.opengis.indoorgml.core.v_1_0.StateType;

public class FootPrint {

	private int level;
	private int id;
	private List<GenericPolygon> polygonList;
	//private GenericPolygon gp;

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
		GenericPolygon gp = new GenericPolygon(); // is calling a default constructor ok?
		//CitygmlBuilding cg = new CitygmlBuilding();
		Building building = new Building();
		List<Polygon> listOfpolyValues = new ArrayList<>(); 
		for (int i =0;i<polygonList.size();i++)
		{
			Polygon poly = polygonList.get(i).createCitygmlPoly(); // to use for shell
			listOfpolyValues.add(poly);
			building.addBoundary(gp.createBoundary(polygonList.get(i).getName(), poly));  
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

		
		
		public void writeTagswaysOsm(OsmOutputStream osmOutput) throws IOException{
			for (int i =0;i<polygonList.size();i++) {
				OsmWay way = polygonList.get(i).createosmWay(osmOutput); // how to set tags
			}
		}
		
		public IndoorFeaturesType setIndoorFeatures()
		{
			IndoorGmlBuilding inb = new IndoorGmlBuilding();
			
			IndoorFeaturesType indoorFeatures = new IndoorFeaturesType(); // description 
			indoorFeatures.setId("if"+ String.valueOf(id));

			PrimalSpaceFeaturesType primalSpaceFeature = new PrimalSpaceFeaturesType();
			primalSpaceFeature.setId("pf"+ String.valueOf(id));


			MultiLayeredGraphType multiLayeredGraph = new MultiLayeredGraphType();
			multiLayeredGraph.setId("mlg"+ String.valueOf(id));

			SpaceLayersType spaceLayers = new SpaceLayersType();
			spaceLayers.setId("slayers"+ String.valueOf(id));
			List<SpaceLayersType> spaceLayerslist = new ArrayList<SpaceLayersType>();
			spaceLayerslist.add(spaceLayers);

			SpaceLayerType spaceLayer = new SpaceLayerType();
			spaceLayer.setId("sl"+ String.valueOf(id));
			List<SpaceLayerMemberType> spaceLayermemberlist = new ArrayList<SpaceLayerMemberType>();
			SpaceLayerMemberType sLayermember = new SpaceLayerMemberType();
			sLayermember.setSpaceLayer(spaceLayer);
			spaceLayermemberlist.add(sLayermember);
			

			NodesType nodes  = new NodesType();
			nodes.setId("n"+ String.valueOf(id));
			List<NodesType> nodesList = new ArrayList<NodesType>();
			nodesList.add(nodes);
			


			PrimalSpaceFeaturesPropertyType primalspacefeaturesProp = new PrimalSpaceFeaturesPropertyType();
			primalspacefeaturesProp.setPrimalSpaceFeatures(primalSpaceFeature);

			indoorFeatures.setPrimalSpaceFeatures(primalspacefeaturesProp);

			MultiLayeredGraphPropertyType  multilayergraphProp = new MultiLayeredGraphPropertyType();
			multilayergraphProp.setMultiLayeredGraph(multiLayeredGraph);

			indoorFeatures.setMultiLayeredGraph(multilayergraphProp);
			
			multiLayeredGraph.setSpaceLayers(spaceLayerslist);
			
			spaceLayers.setSpaceLayerMember(spaceLayermemberlist);
			spaceLayer.setNodes(nodesList);
			
			List<StateMemberType> states = new ArrayList<StateMemberType>();
			
			List<CellSpaceMemberType> cellspacemembers = new ArrayList<CellSpaceMemberType>();
			
			for (int i =0;i<polygonList.size();i++)
			{
			 
				CellSpaceType cs = polygonList.get(i).createIndoorgmlCellspace();
				StateType st =polygonList.get(i).setStatePos();
				
				inb.createCellspaceMember(cs, cellspacemembers);
				inb.createStateMember(st, states);
				
				inb.setDualitycellspace(cs, st);
				inb.setdualityState(cs, st);
				
			}
			
			primalSpaceFeature.setCellSpaceMember(cellspacemembers);
			nodes.setStateMember(states);
			
			return indoorFeatures;
			

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