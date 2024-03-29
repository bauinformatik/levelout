package org.opensourcebim.levelout.builders;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import net.opengis.gml.v_3_2.*;
import net.opengis.indoorgml.core.v_1_0.*;

import org.opensourcebim.levelout.intermediatemodel.Building;
import org.opensourcebim.levelout.intermediatemodel.Door;
import org.opensourcebim.levelout.intermediatemodel.Room;
import org.opensourcebim.levelout.intermediatemodel.Storey;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.OutputStream;
import java.lang.Boolean;
import java.math.BigInteger;
import java.util.*;

public class IndoorGmlBuilder {
	private static final net.opengis.indoorgml.core.v_1_0.ObjectFactory indoorObjectFactory = new net.opengis.indoorgml.core.v_1_0.ObjectFactory();
	private static final net.opengis.gml.v_3_2.ObjectFactory gmlObjectFactory = new net.opengis.gml.v_3_2.ObjectFactory();
	private final Map<Room, StateType> roomStateMap = new HashMap<>();
	private final Map<Door, StateType> doorStateMap = new HashMap<>();
	private final Map<CellSpaceType, List<CellSpaceBoundaryPropertyType>> cellspaceboundariesMap = new HashMap<>();

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
		// resource at external URI represents multiple objects, one of which is
		// identified by externalId
		ExternalObjectReferenceType externalObjectReference = new ExternalObjectReferenceType();
		externalObjectReference.setName(externalId);
		ExternalReferenceType externalReference = new ExternalReferenceType();
		externalReference.setInformationSystem(externalUri);
		externalReference.setExternalObject(externalObjectReference);
		return externalReference;
	}

	private void addExternalReference(CellSpaceType cellspace, ExternalReferenceType externalReference) {
		cellspace.getExternalReference().add(externalReference);
	}

	public CellSpaceType createCellSpace(String id, String name) {
		CellSpaceType cellSpace = new CellSpaceType();
		cellSpace.setId(id);
		cellSpace.setName(List.of(new CodeType().withValue(name)));
		return cellSpace;
	}
	public CellSpaceType createCellSpace(String id) {
		CellSpaceType cellSpace = new CellSpaceType();
		cellSpace.setId(id);
		return cellSpace;
	}

	private CellSpaceType createCellSpace(Room room) {
		return createCellSpace("cs" + room.getId(), room.getName());
	}

	private CellSpaceType createCellSpace(Door door) {
		return createCellSpace("cs-door" + door.getId(), door.getName());
	}

	public CellSpaceBoundaryType createCellspaceBoundary(String id) {
		CellSpaceBoundaryType cellSpaceBoundary = new CellSpaceBoundaryType();
		cellSpaceBoundary.setId(id);
		return cellSpaceBoundary;

	}

	public void add2DGeometry(CellSpaceType cellSpace, List<Double> coordinates) {
		PolygonType polygon = createSurface(coordinates);
		add2DGeometry(cellSpace, polygon);
	}

	public void add2DGeometry(CellSpaceBoundaryType cellSpaceBoundary, List<Double> coordinates) {
		LineStringType linestring = createLineString(coordinates);
		add2DGeometry(cellSpaceBoundary, linestring);
	}

	private void add2DGeometry(CellSpaceBoundaryType cellSpaceBoundary, LineStringType linestring) {
		CellSpaceBoundaryGeometryType csbgeom = new CellSpaceBoundaryGeometryType();
		CurvePropertyType curveProp = new CurvePropertyType();
		curveProp.setAbstractCurve(gmlObjectFactory.createLineString(linestring));
		csbgeom.setGeometry2D(curveProp);
		cellSpaceBoundary.setCellSpaceBoundaryGeometry(csbgeom);
	}

	private void add2DGeometry(CellSpaceType cellSpace, PolygonType polygon) {
		CellSpaceGeometryType cellSpaceGeometry = new CellSpaceGeometryType();
		SurfacePropertyType surfaceProperty = new SurfacePropertyType();
		surfaceProperty.setAbstractSurface(gmlObjectFactory.createPolygon(polygon));
		cellSpaceGeometry.setGeometry2D(surfaceProperty);
		cellSpace.setCellSpaceGeometry(cellSpaceGeometry);
	}

	private void add2DGeometry(CellSpaceType cellSpace, Room room) {
		if(room.hasGeometry()) add2DGeometry(cellSpace, room.asCoordinateList());
	}

	private void add2DGeometry(CellSpaceType cellSpace, Door door) {
		if(door.hasGeometry()) add2DGeometry(cellSpace, door.asCoordinateList());

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

	public void addCellSpaceBoundaryMembers(PrimalSpaceFeaturesType primalSpaceFeatures,
			CellSpaceBoundaryType cellSpaceBoundary) {
		CellSpaceBoundaryMemberType cellSpaceBoundaryMember = new CellSpaceBoundaryMemberType();
		cellSpaceBoundaryMember.setCellSpaceBoundary(indoorObjectFactory.createCellSpaceBoundary(cellSpaceBoundary));
		primalSpaceFeatures.getCellSpaceBoundaryMember().add(cellSpaceBoundaryMember);
	}

	public void createAndAddCellSpaceBoundary(List<CellSpaceType> cells, List<Double> coordinates,
			CellSpaceBoundaryType cellSpaceBoundary) {
		CellSpaceBoundaryGeometryType csbgeom = new CellSpaceBoundaryGeometryType();
		LineStringType linestring = createLineString(coordinates);
		CurvePropertyType curveProp = new CurvePropertyType();
		curveProp.setAbstractCurve(gmlObjectFactory.createLineString(linestring));
		csbgeom.setGeometry2D(curveProp);
		cellSpaceBoundary.setCellSpaceBoundaryGeometry(csbgeom);
		List<CellSpaceBoundaryPropertyType> cellspaceboundaries = new ArrayList<>();
		CellSpaceBoundaryPropertyType cellspaceboundaryProp = new CellSpaceBoundaryPropertyType();
		cellspaceboundaries.add(cellspaceboundaryProp);
		// cellspaceboundaryProp.setHref("#" + cellSpaceBoundary.getId());
		cellspaceboundaryProp.setCellSpaceBoundary(indoorObjectFactory.createCellSpaceBoundary(cellSpaceBoundary));
		for (CellSpaceType cell : cells) {
			if (!cellspaceboundariesMap.containsKey(cell)) {
				cellspaceboundariesMap.put(cell, cellspaceboundaries);
			} else {
				cellspaceboundariesMap.get(cell).addAll(cellspaceboundaries);
			} // in fact the boundaries are added, which is what we want
		}

	}

	public StateType createState(String id) {
		StateType name = new StateType();
		name.setId(id);
		return name;
	}

	private StateType createState(Door door) {
		StateType state = createState("st-door" + door.getId());
		state.setName(List.of(new CodeType().withValue(door.getName())));
		doorStateMap.put(door, state);
		return state;
	}

	private StateType createState(Room room) {
		StateType state = createState("st" + room.getId());
		roomStateMap.put(room, state);
		return state;
	}

	public void setStatePos(StateType state, double x, double y, double z) {
		PointType point = createPoint(x, y, z);
		PointPropertyType pointProperty = new PointPropertyType();
		pointProperty.setPoint(point);
		state.setGeometry(pointProperty);
	}

	private void setStatePos(StateType state, Room room) {
		if (room.hasGeometry()) {
			List<Double> centroid = room.computeCentroid();
			setStatePos(state, centroid.get(0), centroid.get(1), centroid.get(2));
		}
	}

	private void setStatePos(StateType state, Door door) {
		if (door.hasGeometry()) {
			List<Double> centroid = door.computeCentroid();
			setStatePos(state, centroid.get(0), centroid.get(1), centroid.get(2));
		}
	}

	public void addState(NodesType nodes, StateType state) {
		StateMemberType stateMember = new StateMemberType();
		stateMember.setState(state);
		nodes.getStateMember().add(stateMember);
	}

	public TransitionType createTransition(String id) {
		TransitionType transition = new TransitionType();
		transition.setId(id);
		return transition;
	}

	public TransitionType createTransition(String id, StateType state1, StateType state2) {
		TransitionType transition = createTransition(id);
		setStateConnects(state1, transition);
		setStateConnects(state2, transition);
		setTransitionConnects(transition, state1, state2);
		return transition;
	}

	public TransitionType createTransition(Door door, Room room) {
		TransitionType transition = createTransition("door" + door.getId() + room.getId(), doorStateMap.get(door), roomStateMap.get(room));
		if(door.hasGeometry() && room.hasGeometry()) setTransitionPos(transition, door.computeCentroid(), room.computeCentroid());
		return transition;
	}

	private TransitionType createTransitionReverse(Door door, Room room) {
		TransitionType transition = createTransition("door" + door.getId() + room.getId() + "REVERSE", roomStateMap.get(room), doorStateMap.get(door));
		if(door.hasGeometry() && room.hasGeometry()) setTransitionPos(transition, room.computeCentroid(), door.computeCentroid());
		return transition;
	}

	public void setTransitionPos(TransitionType trans, List<Double> coordinates) {
		LineStringType linestring = createLineString(coordinates);
		CurvePropertyType curveProp = new CurvePropertyType();
		curveProp.setAbstractCurve(gmlObjectFactory.createLineString(linestring));
		trans.setGeometry(curveProp);
	}

	private void setTransitionPos(TransitionType transition, List<Double> start, List<Double> end){
		List<Double> coordinates = new ArrayList<>();
		coordinates.addAll(start);
		coordinates.addAll(end);
		setTransitionPos(transition, coordinates);
	}

	public void addTransition(EdgesType edges, TransitionType transition) {
		TransitionMemberType transitionMember = new TransitionMemberType();
		transitionMember.setTransition(transition);
		edges.getTransitionMember().add(transitionMember);
	}

	public void createAndAddCellSpaceBoundary(CellSpaceType cellspace,
			List<CellSpaceBoundaryType> cellspaceBoundaries) {
		List<CellSpaceBoundaryPropertyType> cellspaceboundarieslist = new ArrayList<>();

		for (CellSpaceBoundaryType csb : cellspaceBoundaries) {
			CellSpaceBoundaryPropertyType cellspaceboundaryProp = new CellSpaceBoundaryPropertyType();
			cellspaceboundaryProp.setHref("#" + csb.getId());
			cellspaceboundarieslist.add(cellspaceboundaryProp);
		}
		cellspace.setPartialboundedBy(cellspaceboundarieslist);
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

	public void setDuality(CellSpaceType cellSpace, StateType state) {
		setDualCellSpaceForState(state, cellSpace);
		setDualStateForCellSpace(cellSpace, state);
	}

	private void setStateConnects(StateType state, TransitionType transition) {
		TransitionPropertyType transitionProp = new TransitionPropertyType();
		transitionProp.setHref("#" + transition.getId());
		state.getConnects().add(transitionProp);
	}

	private void setTransitionConnects(TransitionType transition, StateType state1, StateType state2) {
		StatePropertyType stateProp = new StatePropertyType();
		stateProp.setHref("#" + state1.getId());
		StatePropertyType stateProp2 = new StatePropertyType();
		stateProp2.setHref("#" + state2.getId());
		List<StatePropertyType> stateProplist = Arrays.asList(stateProp, stateProp2);
		transition.setConnects(stateProplist);
	}

	public SpaceLayerType getFirstDualSpaceLayer(IndoorFeaturesType indoorFeatures) {
		return indoorFeatures.getMultiLayeredGraph().getMultiLayeredGraph().getSpaceLayers().get(0)
				.getSpaceLayerMember().get(0).getSpaceLayer();

	}

	public PrimalSpaceFeaturesType getPrimalSpace(IndoorFeaturesType indoorFeatures) {
		return indoorFeatures.getPrimalSpaceFeatures().getPrimalSpaceFeatures();
	}

	public IndoorFeaturesType createIndoorFeatures() {
		// TODO: do this in builder constructor and keep it private, to ensure proper
		// creation of primal and dual space
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
		for (Storey storey : building.getStoreys()) {
			for (Room room : storey.getRooms()) {
				CellSpaceType cs = createCellSpace(room);
				addCellSpace(primalSpace, cs);
				StateType state = createState(room);
				setStatePos(state, room);
				addState(dualSpace.getNodes().get(0), state);
				setDuality(cs, state);
				add2DGeometry(cs, room);
			}
			for (Door door : storey.getDoors()) {
				if (!storey.getRooms().contains(door.getRoom1()) && !storey.getRooms().contains(door.getRoom2())) {
					// TODO warning
				} else {
					CellSpaceType cs = createCellSpace(door);
					addCellSpace(primalSpace, cs);
					StateType state = createState(door);
					setStatePos(state, door);
					addState(dualSpace.getNodes().get(0), state);
					setDuality(cs, state);
					add2DGeometry(cs, door);

					addTransition(dualSpace.getEdges().get(0), createTransition(door, door.getRoom1()));
					addTransition(dualSpace.getEdges().get(0), createTransitionReverse(door, door.getRoom1()));
					if (!door.isExternal()) {
						addTransition(dualSpace.getEdges().get(0), createTransition(door, door.getRoom2()));
						addTransition(dualSpace.getEdges().get(0), createTransitionReverse(door, door.getRoom2()));
					}
				}
			}
		}
		return indoorFeatures;
	}

	public void createAndWriteBuilding(Building building, OutputStream outStream) throws JAXBException {
		write(outStream, createIndoorFeatures(building));
	}

	public void write(OutputStream outStream, IndoorFeaturesType indoorFeatures) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(IndoorFeaturesType.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
				IndoorGMLNameSpaceMapper.DEFAULT_URI + " http://schemas.opengis.net/indoorgml/1.0/indoorgmlcore.xsd "
						+ IndoorGMLNameSpaceMapper.NAVIGATION_URI
						+ " http://schemas.opengis.net/indoorgml/1.0/indoorgmlnavi.xsd"
						+ IndoorGMLNameSpaceMapper.XLINK_URI + " https://www.w3.org/XML/2008/06/xlink.xsd");
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
			return new String[] { DEFAULT_URI, NAVIGATION_URI, GML_URI, XLINK_URI };
		}
	}

}
