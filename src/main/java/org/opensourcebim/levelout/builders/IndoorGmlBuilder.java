package org.opensourcebim.levelout.builders;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import net.opengis.gml.v_3_2_1.*;
import net.opengis.indoorgml.core.v_1_0.*;
import org.opensourcebim.levelout.intermediatemodel.Building;
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
import java.util.List;

public class IndoorGmlBuilder {
	private static final net.opengis.indoorgml.core.v_1_0.ObjectFactory indoorObjectFactory = new net.opengis.indoorgml.core.v_1_0.ObjectFactory();
	private static final net.opengis.gml.v_3_2_1.ObjectFactory gmlObjectFactory = new net.opengis.gml.v_3_2_1.ObjectFactory();

	public static CellSpaceType createIndoorGmlCellSpace(Room room) {
		CellSpaceType cellspace = new CellSpaceType();
		cellspace.setId("cs" + room.getId());
		ExternalObjectReferenceType extrefobj = new ExternalObjectReferenceType();
		extrefobj.setUri(null); // that's the default anyway, I guess
		ExternalReferenceType extreftyp = new ExternalReferenceType();
		extreftyp.setExternalObject(extrefobj);
		List<ExternalReferenceType> extreflist = new ArrayList<>();
		extreflist.add(extreftyp);
		cellspace.setExternalReference(extreflist);
		return cellspace;
	}

	public static StateType createIndoorGmlState(Room room) {
		PointPropertyType pointProperty = new PointPropertyType();
		PointType point = new PointType();
		DirectPositionType dirPos = new DirectPositionType();
		List<Double> centroid = room.computeCentroid();
		dirPos.withValue(centroid.get(0), centroid.get(1), centroid.get(2)).withSrsDimension(BigInteger.valueOf(3));
		point.setPos(dirPos);
		pointProperty.setPoint(point);

		StateType state = new StateType();
		state.setId("st" + room.getId());
		state.setGeometry(pointProperty);
		return state;
	}

	public void createCellSpaceMember(CellSpaceType cellSpace, List<CellSpaceMemberType> cellSpaceMembers) {
		CellSpaceMemberType cellSpaceMember = new CellSpaceMemberType();
		cellSpaceMembers.add(cellSpaceMember);
		cellSpaceMember.setCellSpace(indoorObjectFactory.createCellSpace(cellSpace));
	}

	public void createStateMember(StateType state, List<StateMemberType> stateMembers) {
		StateMemberType stateMember = new StateMemberType();
		stateMembers.add(stateMember);
		stateMember.setState(state);
	}
	
	public void createTransitionMember(TransitionType transition, List<TransitionMemberType> transitionMembers) {
		TransitionMemberType transitionMember = new TransitionMemberType();
		transitionMembers.add(transitionMember);
		transitionMember.setTransition(transition);
	}

	public CellSpaceType createCellSpace(String id, String uri) {
		CellSpaceType cellSpace = new CellSpaceType();
		cellSpace.setId(id);

		CellSpaceGeometryType cellSpaceGeometry = new CellSpaceGeometryType();
		SolidPropertyType solidProperty = new SolidPropertyType();

		SolidType solid = new SolidType();
		ShellPropertyType shellProperty = new ShellPropertyType();
		ShellType shell = new ShellType();
		SurfacePropertyType surfaceProperty = new SurfacePropertyType();

		shell.setSurfaceMember(List.of(surfaceProperty));
		shellProperty.setShell(shell);
		solid.setExterior(shellProperty);
		solidProperty.setAbstractSolid(gmlObjectFactory.createSolid(solid));
		cellSpaceGeometry.setGeometry3D(solidProperty);

		ExternalObjectReferenceType externalObjectReference = new ExternalObjectReferenceType();
		externalObjectReference.setUri("file://GMLID_"+id+".city.gml"); // (B) uri represents whole object
		externalObjectReference.setName("GMLID_"+id); // (A) reference to single object within foreign data set, e.g. CityGML
		ExternalReferenceType externalReference = new ExternalReferenceType();
		externalReference.setInformationSystem("test.city.gml"); // (A) uri represents foreign model with multiple objects, e.g. CityGML
		externalReference.setExternalObject(externalObjectReference);
		cellSpace.setExternalReference(List.of(externalReference));

		PolygonType polygon = new PolygonType();

	    LinearRingType linearRing = new LinearRingType();
		DirectPositionListType directPositions = new DirectPositionListType();
		linearRing.setPosList(directPositions);

		List<Double> coordinates = Arrays.asList(0.,0.,0.,1.,0.,0.,1.,1.,0.,0.,1.,0.);
		directPositions.setValue(coordinates);

		AbstractRingPropertyType abstractRingProperty = new AbstractRingPropertyType();
		abstractRingProperty.setAbstractRing(gmlObjectFactory.createLinearRing(linearRing));
		polygon.setExterior(abstractRingProperty);

		cellSpace.setCellSpaceGeometry(cellSpaceGeometry);
	//	cellSpaceGeometry.setGeometry2D(surfaceProperty);
		surfaceProperty.setAbstractSurface(gmlObjectFactory.createPolygon(polygon));
		return cellSpace;
	}

	public StateType createState(String id) {
		StateType name = new StateType();
		name.setId(id);
		return name;
	}
	
	public TransitionType createTransition(String id) {
		TransitionType name = new TransitionType();
		name.setId(id);
		return name;
	}
	
	public void setTransitionPos(TransitionType trans) {
		
		CurvePropertyType curveProp = new CurvePropertyType ();
		LineStringType linestring = new LineStringType();
		DirectPositionListType dirposlist = new DirectPositionListType();
		linestring.setPosList(dirposlist);

		List<Double> coordinates = Arrays.asList(5.,5.,5.,5.,5.,15.);
		dirposlist.setValue(coordinates);
		

		List<PointPropertyType> pointprop = new ArrayList<>();
		
	//	linestring.setPosOrPointPropertyOrPointRep(gmlObjectFactory.createPointProperty(pointprop));
		curveProp.setAbstractCurve(gmlObjectFactory.createLineString(linestring));
		trans.setGeometry(curveProp);
	}

	

	public void setDualityState(StateType state, CellSpaceType cellSpace) {
		CellSpacePropertyType cellSpaceProperty = new CellSpacePropertyType();
		cellSpaceProperty.setHref("#" + cellSpace.getId());
		state.setDuality(cellSpaceProperty);
	}

	public void setDualityCellSpace(CellSpaceType cellSpace, StateType state) {
		StatePropertyType stateProperty = new StatePropertyType();
		stateProperty.setHref("#" + state.getId());
		cellSpace.setDuality(stateProperty);
	}

	public void setStatePos(StateType state, double x, double y, double z) {
		PointPropertyType pointProperty = new PointPropertyType();
		PointType point = new PointType();
		DirectPositionType directPosition = new DirectPositionType();
		directPosition.withValue(x, y, z).withSrsDimension(BigInteger.valueOf(3));
		point.setPos(directPosition);
		pointProperty.setPoint(point);
		state.setGeometry(pointProperty);
	}
	

	private void createIndoorFeatures(Storey storey, List<StateMemberType> stateMembers, List<CellSpaceMemberType> cellSpaceMembers) {
		for (Room genericPolygon : storey.getPolygonList()) {
			CellSpaceType cs = createIndoorGmlCellSpace(genericPolygon);
			createCellSpaceMember(cs, cellSpaceMembers);

			StateType st = createIndoorGmlState(genericPolygon);
			createStateMember(st, stateMembers);

			setDualityCellSpace(cs, st);
			setDualityState(st, cs);
		}
	}

	public void createAndWriteBuilding(Building building, OutputStream outStream) throws JAXBException {
		write(outStream, createIndoorFeatures(building));
	}

	private IndoorFeaturesType createIndoorFeatures(Building building) {
		IndoorFeaturesType indoorFeatures = new IndoorFeaturesType(); // description
		indoorFeatures.setId("if");
		PrimalSpaceFeaturesType primalSpaceFeature = new PrimalSpaceFeaturesType();
		primalSpaceFeature.setId("pf");
		MultiLayeredGraphType multiLayeredGraph = new MultiLayeredGraphType();
		multiLayeredGraph.setId("mlg"); //	+ String.valueOf(id);

		SpaceLayersType spaceLayers = new SpaceLayersType();
		spaceLayers.setId("slayers");
		List<SpaceLayersType> spaceLayerslist = new ArrayList<>();
		spaceLayerslist.add(spaceLayers);

		SpaceLayerType spaceLayer = new SpaceLayerType();
		spaceLayer.setId("sl");
		List<SpaceLayerMemberType> spaceLayerMemberList = new ArrayList<>();
		SpaceLayerMemberType spaceLayerMember = new SpaceLayerMemberType();
		spaceLayerMember.setSpaceLayer(spaceLayer);
		spaceLayerMemberList.add(spaceLayerMember);

		NodesType nodes = new NodesType();
		nodes.setId("n");
		List<NodesType> nodesList = new ArrayList<>();
		nodesList.add(nodes);

		PrimalSpaceFeaturesPropertyType primalSpaceFeaturesProp = new PrimalSpaceFeaturesPropertyType();
		primalSpaceFeaturesProp.setPrimalSpaceFeatures(primalSpaceFeature);
		indoorFeatures.setPrimalSpaceFeatures(primalSpaceFeaturesProp);

		MultiLayeredGraphPropertyType multilayergraphProp = new MultiLayeredGraphPropertyType();
		multilayergraphProp.setMultiLayeredGraph(multiLayeredGraph);
		indoorFeatures.setMultiLayeredGraph(multilayergraphProp);

		multiLayeredGraph.setSpaceLayers(spaceLayerslist);

		spaceLayers.setSpaceLayerMember(spaceLayerMemberList);
		spaceLayer.setNodes(nodesList);

		List<StateMemberType> states = new ArrayList<>();
		List<CellSpaceMemberType> cellSpaceMembers = new ArrayList<>();

		for (Storey footPrint : building.getFootPrints()) {
			createIndoorFeatures(footPrint, states, cellSpaceMembers);
		}

		primalSpaceFeature.setCellSpaceMember(cellSpaceMembers);
		nodes.setStateMember(states);
		return indoorFeatures;
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
