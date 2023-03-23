package org.opensourcebim.levelout.builders;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import net.opengis.gml.v_3_2.*;
import net.opengis.indoorgml.core.v_1_0.*;
import org.opensourcebim.levelout.intermediatemodel.Building;
import org.opensourcebim.levelout.intermediatemodel.Door;
import org.opensourcebim.levelout.intermediatemodel.Room;
import org.opensourcebim.levelout.intermediatemodel.Storey;
import org.opensourcebim.levelout.util.SpatialAnalysis;

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

public class IndoorGmlBuilder {
	private static final net.opengis.indoorgml.core.v_1_0.ObjectFactory indoorObjectFactory = new net.opengis.indoorgml.core.v_1_0.ObjectFactory();
	private static final net.opengis.gml.v_3_2.ObjectFactory gmlObjectFactory = new net.opengis.gml.v_3_2.ObjectFactory();
	private final Map<Room, StateType> roomStateMap = new HashMap<>();
	private final Map<Room, CellSpaceType> roomCellMap = new HashMap<>();

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

	public CellSpaceType createCellSpace(String id) {
		CellSpaceType cellSpace = new CellSpaceType();
		cellSpace.setId(id);
		return cellSpace;
	}

	private CellSpaceType createCellSpace(Room room) {
		CellSpaceType cellSpace = createCellSpace("cs" + room.getId());
		roomCellMap.put(room, cellSpace);
		return cellSpace;
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

	public void addCellSpaceBoundaryMembers(PrimalSpaceFeaturesType primalSpaceFeatures,
			CellSpaceBoundaryType cellSpaceBoundary) {
		CellSpaceBoundaryMemberType cellSpaceBoundaryMember = new CellSpaceBoundaryMemberType();
		cellSpaceBoundaryMember.setCellSpaceBoundary(indoorObjectFactory.createCellSpaceBoundary(cellSpaceBoundary));
		primalSpaceFeatures.getCellSpaceBoundaryMember().add(cellSpaceBoundaryMember);
	}

	public StateType createState(String id) {
		StateType name = new StateType();
		name.setId(id);
		return name;
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
		List<Double> centroid = room.computeCentroid();
		if (centroid != null) {
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
		List<StatePropertyType> stateProplist = new ArrayList<>();
		StatePropertyType stateProp = new StatePropertyType();
		stateProplist.add(stateProp);
		stateProp.setHref("#" + state1.getId());
		StatePropertyType stateProp2 = new StatePropertyType();
		stateProplist.add(stateProp2);
		stateProp2.setHref("#" + state2.getId());
		transition.setConnects(stateProplist);
		return transition;
	}

	public TransitionType createTransition(Door door) {

		return createTransition("door" + door.getId(), roomStateMap.get(door.getRoom1()),roomStateMap.get(door.getRoom2()));
		
	}

	public void setTransitionPos(TransitionType trans, List<Double> coordinates) {
		LineStringType linestring = createLineString(coordinates);
		CurvePropertyType curveProp = new CurvePropertyType();
		curveProp.setAbstractCurve(gmlObjectFactory.createLineString(linestring));
		trans.setGeometry(curveProp);
	}

	public TransitionType setTransitionPos(TransitionType transition, Door door, double d) {
		List<Double> coordinates = Arrays.asList(roomStateMap.get(door.getRoom1()).getGeometry().getPoint().getPos().getValue().get(0),
				roomStateMap.get(door.getRoom1()).getGeometry().getPoint().getPos().getValue().get(1),
				roomStateMap.get(door.getRoom1()).getGeometry().getPoint().getPos().getValue().get(2),door.asCoordinateList().get(0),
				door.asCoordinateList().get(1),d,
				roomStateMap.get(door.getRoom2()).getGeometry().getPoint().getPos().getValue().get(0),
				roomStateMap.get(door.getRoom2()).getGeometry().getPoint().getPos().getValue().get(1),
				roomStateMap.get(door.getRoom2()).getGeometry().getPoint().getPos().getValue().get(2));
		LineStringType linestring = createLineString(coordinates);
		CurvePropertyType curveProp = new CurvePropertyType();
		curveProp.setAbstractCurve(gmlObjectFactory.createLineString(linestring));
		transition.setGeometry(curveProp);
		return transition;
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
				if (!storey.getRooms().contains(door.getRoom1()) || !storey.getRooms().contains(door.getRoom2())) {
					// TODO warning
				} else if (!door.isExternal()) {
					createAndAddCellSpaceBoundary(door, door.getRoom1(), door.getRoom2());
					TransitionType transition = createTransition(door);
					setTransitionPos(transition, door, storey.getZ());
					addTransition(dualSpace.getEdges().get(0), transition);
					// TODO duality boundary - transition
				}
				// TODO exterior doors
			}
		}
		return indoorFeatures;
	}

	public void createAndAddCellSpaceBoundary(CellSpaceType cell1, CellSpaceType cell2, List<Double> coordinates,
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
		cellspaceboundaryProp.setCellSpaceBoundary(indoorObjectFactory.createCellSpaceBoundary(cellSpaceBoundary));
		cell1.setPartialboundedBy(cellspaceboundaries); // in fact the boundaries are added, which is what we want
		cell2.setPartialboundedBy(cellspaceboundaries);
	}

	private void createAndAddCellSpaceBoundary(Door door, Room room1, Room room2) {
		createAndAddCellSpaceBoundary(roomCellMap.get(room1), roomCellMap.get(room2), door.asCoordinateList(),
				createCellspaceBoundary("csb-" + room1.getId() + "-" + room2.getId()));
	}

	public CellSpaceBoundaryType createCellspaceBoundary(String id) {
		CellSpaceBoundaryType cellSpaceBoundary = new CellSpaceBoundaryType();
		cellSpaceBoundary.setId(id);
		return cellSpaceBoundary;

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
