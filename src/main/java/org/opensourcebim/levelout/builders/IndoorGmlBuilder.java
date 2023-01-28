package org.opensourcebim.levelout.builders;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import net.opengis.gml.v_3_2.*;
import net.opengis.indoorgml.core.v_1_0.*;
import org.opensourcebim.levelout.intermediatemodel.Building;
import org.opensourcebim.levelout.intermediatemodel.Corner;
import org.opensourcebim.levelout.intermediatemodel.Door;
import org.opensourcebim.levelout.intermediatemodel.Room;
import org.opensourcebim.levelout.intermediatemodel.Storey;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.OutputStream;
import java.lang.Boolean;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.Map.Entry;

public class IndoorGmlBuilder {
	private static final net.opengis.indoorgml.core.v_1_0.ObjectFactory indoorObjectFactory = new net.opengis.indoorgml.core.v_1_0.ObjectFactory();
	private static final net.opengis.gml.v_3_2.ObjectFactory gmlObjectFactory = new net.opengis.gml.v_3_2.ObjectFactory();

	private PointType createPoint(double x, double y, double z) {
		PointType point = new PointType();
		DirectPositionType directPosition = new DirectPositionType();
		directPosition.withValue(x, y, z).withSrsDimension(BigInteger.valueOf(3));
		point.setPos(directPosition);
		return point;
	}
	private LineStringType createLineString(List<Double> coordinates) {
		LineStringType linestring = new LineStringType();
		DirectPositionListType dirPositions = new DirectPositionListType();
		linestring.setPosList(dirPositions);
		dirPositions.setValue(coordinates);
		return linestring;
	}
	private static PolygonType createSurface(List<Double> coordinates) {
		DirectPositionListType directPositions = new DirectPositionListType();
		directPositions.setValue(coordinates);
		LinearRingType linearRing = new LinearRingType();
		linearRing.setPosList(directPositions);
		AbstractRingPropertyType abstractRingProperty = new AbstractRingPropertyType();
		abstractRingProperty.setAbstractRing(gmlObjectFactory.createLinearRing(linearRing));
		PolygonType polygon = new PolygonType();
		polygon.setExterior(abstractRingProperty);
		return polygon;
	}
	private static SolidType createSolid(PolygonType polygon) {
		SurfacePropertyType surfaceProperty = new SurfacePropertyType();
		surfaceProperty.setAbstractSurface(gmlObjectFactory.createPolygon(polygon));
		ShellType shell = new ShellType();
		shell.setSurfaceMember(List.of(surfaceProperty));
		ShellPropertyType shellProperty = new ShellPropertyType();
		shellProperty.setShell(shell);
		SolidType solid = new SolidType();
		solid.setExterior(shellProperty);
		return solid;
	}
	private ExternalReferenceType createExternalReference(String externalUri) {
		// resource at external URI represents whole object
		ExternalObjectReferenceType externalObjectReference = new ExternalObjectReferenceType();
		externalObjectReference.setUri(null);
		ExternalReferenceType externalReference = new ExternalReferenceType();
		externalReference.setExternalObject(externalObjectReference);
		return externalReference;
	}
	private ExternalReferenceType createExternalReference(String externalUri, String externalId) {
		// resource at external URI represents multiple objects, one of which is identified by externalId
		ExternalObjectReferenceType externalObjectReference = new ExternalObjectReferenceType();
		externalObjectReference.setName(externalId);
		ExternalReferenceType externalReference = new ExternalReferenceType();
		externalReference.setInformationSystem(externalUri);
		externalReference.setExternalObject(externalObjectReference);
		return externalReference;
	}
	private void addExternalReference(CellSpaceType cellspace, ExternalReferenceType externalReference){
		cellspace.getExternalReference().add(externalReference);
	}
	public CellSpaceType createCellSpace(String id) {
		CellSpaceType cellSpace = new CellSpaceType();
		cellSpace.setId(id);
		return cellSpace;
	}
	private CellSpaceType createCellSpace(Room room) {
		return createCellSpace("cs"+room.getId());
	}
	public void add2DGeometry(CellSpaceType cellSpace, List<Double> coordinates){
		PolygonType polygon = createSurface(coordinates);
		add2DGeometry(cellSpace, polygon);
	}
	private void add2DGeometry(CellSpaceType cellSpace, PolygonType polygon) {
		CellSpaceGeometryType cellSpaceGeometry = new CellSpaceGeometryType();
		SurfacePropertyType surfaceProperty = new SurfacePropertyType();
		surfaceProperty.setAbstractSurface(gmlObjectFactory.createPolygon(polygon));
		cellSpaceGeometry.setGeometry2D(surfaceProperty);
		cellSpace.setCellSpaceGeometry(cellSpaceGeometry);
	}
	private void add2DGeometry(CellSpaceType cellSpace, Room room) {
		add2DGeometry(cellSpace, room.asCoordinateList());
	}
	public void add3DGeometry(CellSpaceType cellSpace, List<Double> coordinates) {
		PolygonType polygon = createSurface(coordinates);
		SolidType solid = createSolid(polygon);
		add3DGeometry(cellSpace, solid);
	}
	private void add3DGeometry(CellSpaceType cellSpace, SolidType solid) {
		SolidPropertyType solidProperty = new SolidPropertyType();
		solidProperty.setAbstractSolid(gmlObjectFactory.createSolid(solid));
		CellSpaceGeometryType cellSpaceGeometry = new CellSpaceGeometryType();
		cellSpaceGeometry.setGeometry3D(solidProperty);
		cellSpace.setCellSpaceGeometry(cellSpaceGeometry);
	}
	private void add3DGeometry(CellSpaceType cellSpace, Room room) {
		add3DGeometry(cellSpace, room.asCoordinateList());
	}
	public void addCellSpace(PrimalSpaceFeaturesType primalSpaceFeatures, CellSpaceType cellSpace) {
		CellSpaceMemberType cellSpaceMember = new CellSpaceMemberType();
		cellSpaceMember.setCellSpace(indoorObjectFactory.createCellSpace(cellSpace));
		primalSpaceFeatures.getCellSpaceMember().add(cellSpaceMember);
	}
	public StateType createState(String id) {
		StateType name = new StateType();
		name.setId(id);
		return name;
	}
	private StateType createState(Room room) {
		return createState("st" + room.getId());
	}
	public void setStatePos(StateType state, double x, double y, double z) {
		PointType point = createPoint(x, y, z);
		PointPropertyType pointProperty = new PointPropertyType();
		pointProperty.setPoint(point);
		state.setGeometry(pointProperty);
		//state.getGeometry().getPoint().getCoordinates()
	}
	private void setStatePos(StateType state, Room room) {
		List<Double> centroid = room.computeCentroid();
		if(centroid!=null){
			setStatePos(state, centroid.get(0), centroid.get(1), centroid.get(2));
		}
	}
	public void addState(NodesType nodes, StateType state) {
		StateMemberType stateMember = new StateMemberType();
		stateMember.setState(state);
		nodes.getStateMember().add(stateMember);
	}
	public TransitionType createTransition(String id) {
		TransitionType name = new TransitionType();
		name.setId(id);
		return name;
	}
	public void setTransitionPos(TransitionType trans, List<Double> coordinates) {
		LineStringType linestring = createLineString(coordinates);
		CurvePropertyType curveProp = new CurvePropertyType ();
		curveProp.setAbstractCurve(gmlObjectFactory.createLineString(linestring));
		trans.setGeometry(curveProp);
	}
	public void addTransition(EdgesType edges, TransitionType transition) {
		TransitionMemberType transitionMember = new TransitionMemberType();
		transitionMember.setTransition(transition);
		edges.getTransitionMember().add(transitionMember);
	}
	private void findneighbours(CellSpaceType cs, Room room) {
	//	cs.getCellSpaceGeometry().getGeometry2D().getAbstractSurface()
	}
	private void setDualCellSpaceForState(StateType state, CellSpaceType cellSpace) {
		CellSpacePropertyType cellSpaceProperty = new CellSpacePropertyType();
		cellSpaceProperty.setHref("#" + cellSpace.getId());
		state.setDuality(cellSpaceProperty);
	}
	private void setDualStateForCellSpace(CellSpaceType cellSpace, StateType state) {
		StatePropertyType stateProperty = new StatePropertyType();
		stateProperty.setHref("#" + state.getId());
		cellSpace.setDuality(stateProperty);
	}
	public void setDuality(CellSpaceType cellSpace, StateType state){
		setDualCellSpaceForState(state, cellSpace);
		setDualStateForCellSpace(cellSpace, state);
	}
	
	public SpaceLayerType getFirstDualSpaceLayer(IndoorFeaturesType indoorFeatures) {
		return indoorFeatures.getMultiLayeredGraph().getMultiLayeredGraph().getSpaceLayers().get(0).getSpaceLayerMember().get(0).getSpaceLayer();

	}

	public PrimalSpaceFeaturesType getPrimalSpace(IndoorFeaturesType indoorFeatures) {
		return indoorFeatures.getPrimalSpaceFeatures().getPrimalSpaceFeatures();
	}

	public IndoorFeaturesType createIndoorFeatures() {
		// TODO: do this in builder constructor and keep it private, to ensure proper creation of primal and dual space
		IndoorFeaturesType indoorFeatures = new IndoorFeaturesType();
		indoorFeatures.setId("if");

		PrimalSpaceFeaturesType primalSpaceFeature = new PrimalSpaceFeaturesType();
		primalSpaceFeature.setId("pf");
		PrimalSpaceFeaturesPropertyType primalSpaceFeaturesProp = new PrimalSpaceFeaturesPropertyType();
		primalSpaceFeaturesProp.setPrimalSpaceFeatures(primalSpaceFeature);
		indoorFeatures.setPrimalSpaceFeatures(primalSpaceFeaturesProp);
		MultiLayeredGraphType multiLayeredGraph = new MultiLayeredGraphType();
		multiLayeredGraph.setId("mlg");

		SpaceLayersType spaceLayers = new SpaceLayersType();
		spaceLayers.setId("slayers");
		SpaceLayerType spaceLayer = new SpaceLayerType();
		spaceLayer.setId("sl");
		SpaceLayerMemberType spaceLayerMember = new SpaceLayerMemberType();
		spaceLayerMember.setSpaceLayer(spaceLayer);

		NodesType nodes = new NodesType();
		nodes.setId("n");
		EdgesType edges = new EdgesType();
		edges.setId("e");
		MultiLayeredGraphPropertyType multilayergraphProp = new MultiLayeredGraphPropertyType();
		multilayergraphProp.setMultiLayeredGraph(multiLayeredGraph);
		indoorFeatures.setMultiLayeredGraph(multilayergraphProp);

		multiLayeredGraph.setSpaceLayers(List.of(spaceLayers));
		spaceLayers.setSpaceLayerMember(List.of(spaceLayerMember));
		spaceLayer.setNodes(List.of(nodes));
		spaceLayer.setEdges(List.of(edges));
		return indoorFeatures;
	}

	private IndoorFeaturesType createIndoorFeatures(Building building) {
		IndoorFeaturesType indoorFeatures = createIndoorFeatures();
		PrimalSpaceFeaturesType primalSpace = getPrimalSpace(indoorFeatures);
		SpaceLayerType dualSpace = getFirstDualSpaceLayer(indoorFeatures);
		List<Room> roomslist = new ArrayList<>();
		List<CellSpaceType> cellspacelist = new ArrayList<>();
		for (Storey storey : building.getStoreys()) {
			for (Room room : storey.getRooms()) {
				for(Door door : storey.getDoors()) {
				CellSpaceType cs = createCellSpace(room);
				roomslist.add(room);
				cellspacelist.add(cs);
				add2DGeometry(cs, room);
				// Map<CellSpaceType, Door> doorboundaries = add2DGeometrydoor(cs, room, door);
				addCellSpace(primalSpace, cs);
				StateType state = createState(room);
				setStatePos(state, room);
				addState(dualSpace.getNodes().get(0), state);
				TransitionType transition = createTransition(room);
				setDuality(cs, state);
			}
				
				
			}
			
			
			// findconnectedStates(findneighbourrooms(roomslist),cellspacelist);
			
		}
		return indoorFeatures;
	}


	private Map<CellSpaceType, Door> add2DGeometrydoor(CellSpaceType cs, Room room, Door door) {
		
		add2DGeometry(cs, room.asCoordinateList());
		Map<CellSpaceType, Door> doorboundaries = new HashMap<>();
		for (int i=0;i<room.asCoordinateList().size();i++)
		{
		Corner roomcorner =	room.getCorners().get(i);
		Corner doorcorner = door.getCorners().get(i);
		double crossZ =	roomcorner.getX()*doorcorner.getY() - roomcorner.getY()*doorcorner.getX();
		double crossY = roomcorner.getZ()*doorcorner.getX() - roomcorner.getX()*doorcorner.getZ();
		double crossX = roomcorner.getY()*doorcorner.getZ() - roomcorner.getZ()*doorcorner.getY();
		
		if (crossX== 0 && crossY == 0 && crossZ == 0)

		{
			doorboundaries.put(cs, door);
		}
		}
		
		return doorboundaries;
	}
	private void setCellspaceBoundary(CellSpaceType cs, Door door) {
		
		cs.setPartialboundedBy(null);
		
	}
	private TransitionType createTransition(Room room) {
		return createTransition("tran"+room.getId());
		
	}
	private void findconnectedStates(List<List> findneighbourrooms, List<CellSpaceType> cellspacelist) {
		
		for(int i=0;i<findneighbourrooms.size();i++)
		{
			 String cellspace1 = "cs"+ (findneighbourrooms.get(i).get(0)).toString();
			 String cellspace2 = "cs"+ (findneighbourrooms.get(i).get(1)).toString();
			 StatePropertyType state1 = new StatePropertyType();
			 StatePropertyType state2 = new StatePropertyType();
			 List<StatePropertyType> statelist = new ArrayList<>();
			 
			
			 for(int j=0;j<cellspacelist.size();j++)
			 {
				 if (cellspacelist.get(j).getId().equals(cellspace1))
				 {
					  state1= cellspacelist.get(j).getDuality();
					  statelist.add(state1);
					  
				 }
				 else if (cellspacelist.get(j).getId().equals(cellspace2))
				 {
					  state2= cellspacelist.get(j).getDuality();
					  statelist.add(state2);
				 }
				 
				
			
			 }
			 
			 	state1.getState().getGeometry().getPoint().getCoordinates();
				TransitionType transition = createTransition("tran"+state1.getState().getId().substring(1));
				transition.setConnects(statelist);
			 
	/*		int csindex1 = cellspacelist.indexOf(cellspace1);
			int csindex2 = cellspacelist.indexOf(cellspace2);
			if((csindex1!=-1)&& (csindex2!=-1))
			{
				StatePropertyType state1= cellspacelist.get(csindex1).getDuality();
				state1.getHref().substring(1);
				
				StatePropertyType state2= cellspacelist.get(csindex2).getDuality();
				state2.getHref().substring(1);
				
				
			}*/
					
			
			 
			 
		}
		
		
	}
	private List<List> findneighbourrooms(List<Room> roomslist) {
		// TODO Auto-generated method stub
		List<List> cellpairsList = new ArrayList<>();
		for (int i=0;i<roomslist.size()-1;i++)
		{
			List a = (roomslist.get(i).getCorners());
			 List b = (roomslist.get(i+1).getCorners());
			 List c = new ArrayList<>(b);
			 	c.retainAll(a);
			 	
			 	if (c.size()>=2)
			 	{
			 		List<Long> cellneighbours = Arrays.asList(roomslist.get(i).getId(),roomslist.get(i+1).getId());
			 		cellpairsList.add(cellneighbours);
			 		}
		}
		return cellpairsList;
		
	}
	private void findneighbours(List<List<CellSpaceType>> cellspacelist) {
		// TODO Auto-generated method stub
		int count = 0;
		List<List> ls = new ArrayList<>();
		int i =0;
		for (i=0;i<cellspacelist.size();i++)
		{
		//	if(cellspacelist.get(i).get(1).getCorners()
		}
		
		
	/*	for (Entry<CellSpaceType, Room> entry : cellspacelist.entrySet()) {
			 Entry<CellSpaceType, Room> prev = cellspacelist.lowerEntry(entry.getKey());
			 Entry<CellSpaceType, Room> next = cellspacelist.higherEntry(entry.getKey());
			if(next.getValue().getCorners().stream().anyMatch(prev.getValue().getCorners()::contains)== true)
			{
				count+=1;
			}
			
			if (count>=2)
			{
				ls.add(Arrays.asList(prev.getKey(),next.getKey()));
				count =0;
			}*/
			
			
			
				    
		
		
		
	}
	public void createAndWriteBuilding(Building building, OutputStream outStream) throws JAXBException {
		write(outStream, createIndoorFeatures(building));
	}

	public void write(OutputStream outStream, IndoorFeaturesType indoorFeatures) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(IndoorFeaturesType.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
			IndoorGMLNameSpaceMapper.DEFAULT_URI + " http://schemas.opengis.net/indoorgml/1.0/indoorgmlcore.xsd " +
				IndoorGMLNameSpaceMapper.NAVIGATION_URI + " http://schemas.opengis.net/indoorgml/1.0/indoorgmlnavi.xsd" +
				IndoorGMLNameSpaceMapper.XLINK_URI + " https://www.w3.org/XML/2008/06/xlink.xsd");
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new IndoorGMLNameSpaceMapper());
		marshaller.marshal(indoorObjectFactory.createIndoorFeatures(indoorFeatures), outStream);
	}

	public static class IndoorGMLNameSpaceMapper extends NamespacePrefixMapper {
		private static final String DEFAULT_URI = "http://www.opengis.net/indoorgml/1.0/core";
		private static final String NAVIGATION_URI = "http://www.opengis.net/indoorgml/1.0/navigation";
		private static final String GML_URI = "http://www.opengis.net/gml/3.2";
		private static final String XLINK_URI = "http://www.w3.org/1999/xlink";

		@Override
		public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
			if (DEFAULT_URI.equals(namespaceUri)) {
				return "core";
			} else if (NAVIGATION_URI.equals(namespaceUri)) {
				return "navi";
			} else if (GML_URI.equals(namespaceUri)) {
				return "gml";
			} else if (XLINK_URI.equals(namespaceUri)) {
				return "xlink";
			}
			return suggestion;
		}

		@Override
		public String[] getPreDeclaredNamespaceUris() {
			return new String[]{DEFAULT_URI, NAVIGATION_URI, GML_URI, XLINK_URI};
		}
	}


}
