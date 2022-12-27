package org.opensourcebim.levelout.samples;

import net.opengis.gml.v_3_2_1.*;
import net.opengis.indoorgml.core.v_1_0.CellSpaceGeometryType;
import net.opengis.indoorgml.core.v_1_0.CellSpaceMemberType;
import net.opengis.indoorgml.core.v_1_0.CellSpacePropertyType;
import net.opengis.indoorgml.core.v_1_0.IndoorFeaturesType;
import net.opengis.indoorgml.core.v_1_0.MultiLayeredGraphPropertyType;
import net.opengis.indoorgml.core.v_1_0.PrimalSpaceFeaturesPropertyType;
import net.opengis.indoorgml.core.v_1_0.PrimalSpaceFeaturesType;
import net.opengis.indoorgml.core.v_1_0.SpaceLayerMemberType;
import net.opengis.indoorgml.core.v_1_0.SpaceLayerType;
import net.opengis.indoorgml.core.v_1_0.SpaceLayersType;
import net.opengis.indoorgml.core.v_1_0.StateMemberType;
import net.opengis.indoorgml.core.v_1_0.StatePropertyType;
import net.opengis.indoorgml.core.v_1_0.StateType;
import net.opengis.indoorgml.core.v_1_0.MultiLayeredGraphType;
import net.opengis.indoorgml.core.v_1_0.NodesType;
import net.opengis.indoorgml.core.v_1_0.CellSpaceType;
import net.opengis.indoorgml.core.v_1_0.ExternalObjectReferenceType;
import net.opengis.indoorgml.core.v_1_0.ExternalReferenceType;

import org.locationtech.jts.io.ParseException;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.Boolean;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IndoorGmlBuilding {

	private static final net.opengis.indoorgml.core.v_1_0.ObjectFactory indoorObjectFactory = new net.opengis.indoorgml.core.v_1_0.ObjectFactory();
	private static final net.opengis.gml.v_3_2_1.ObjectFactory gmlObjectFactory = new net.opengis.gml.v_3_2_1.ObjectFactory();

	public static void main(String[] args) throws JAXBException, ParseException, FileNotFoundException {

		String fileName = "output/outindoor5.gml";
		FileOutputStream fout = new FileOutputStream(fileName);
		IndoorFeaturesType indoorFeatures = new IndoorFeaturesType(); // description 
		indoorFeatures.setId("if1");

		PrimalSpaceFeaturesType primalSpaceFeature = new PrimalSpaceFeaturesType();
		primalSpaceFeature.setId("pf1");

		MultiLayeredGraphType multiLayeredGraph = new MultiLayeredGraphType();
		multiLayeredGraph.setId("mlg1");

		SpaceLayersType spaceLayers = new SpaceLayersType();
		spaceLayers.setId("slayers1");
		List<SpaceLayersType> spaceLayerslist = new ArrayList<SpaceLayersType>();
		spaceLayerslist.add(spaceLayers);

		SpaceLayerType spaceLayer = new SpaceLayerType();
		spaceLayer.setId("sl1");
		List<SpaceLayerMemberType> spaceLayermemberlist = new ArrayList<SpaceLayerMemberType>();
		SpaceLayerMemberType sLayermember = new SpaceLayerMemberType();
		sLayermember.setSpaceLayer(spaceLayer);
		spaceLayermemberlist.add(sLayermember);

		NodesType nodes = new NodesType();
		nodes.setId("n1");
		List<NodesType> nodesList = new ArrayList<NodesType>();
		nodesList.add(nodes);


		PrimalSpaceFeaturesPropertyType primalspacefeaturesProp = new PrimalSpaceFeaturesPropertyType();
		primalspacefeaturesProp.setPrimalSpaceFeatures(primalSpaceFeature);

		indoorFeatures.setPrimalSpaceFeatures(primalspacefeaturesProp);

		MultiLayeredGraphPropertyType multilayergraphProp = new MultiLayeredGraphPropertyType();
		multilayergraphProp.setMultiLayeredGraph(multiLayeredGraph);

		indoorFeatures.setMultiLayeredGraph(multilayergraphProp);

		multiLayeredGraph.setSpaceLayers(spaceLayerslist);

		spaceLayers.setSpaceLayerMember(spaceLayermemberlist);
		spaceLayer.setNodes(nodesList);





		/* PRIMAL SPACE FEATURES*/
		//primalSpaceFeature.setCellSpaceBoundaryMember(null);
		//primalSpaceFeature.withDescription("yes"); // JAXB`


		//CELLSPACE

		CellSpaceType cs1 = createCellspace("c1", null);
		CellSpaceType cs2 = createCellspace("c2", null);
		CellSpaceType cs3 = createCellspace("c3", null);
		CellSpaceType cs4 = createCellspace("c4", null);
		CellSpaceType cs5 = createCellspace("c5", null);
		CellSpaceType cs6 = createCellspace("c6", null);

		List<CellSpaceMemberType> cellspacemembers = new ArrayList<CellSpaceMemberType>();
		createCellspaceMember(cs1, cellspacemembers);
		createCellspaceMember(cs2, cellspacemembers);
		createCellspaceMember(cs3, cellspacemembers);
		createCellspaceMember(cs4, cellspacemembers);
		createCellspaceMember(cs5, cellspacemembers);
		createCellspaceMember(cs6, cellspacemembers);


		primalSpaceFeature.setCellSpaceMember(cellspacemembers);


		//State 

		StateType st1 = createState("s1");
		StateType st2 = createState("s2");
		StateType st3 = createState("s3");
		StateType st4 = createState("s4");
		StateType st5 = createState("s5");
		StateType st6 = createState("s6");


		setStatepos(st1, 5.0, 5.0, 5.0);
		setStatepos(st2, 5.0, 5.0, 15.0);
		setStatepos(st3, 15.0, 2.5, 5.0);
		setStatepos(st4, 15.0, 2.5, 15.0);
		setStatepos(st5, 15.0, 7.5, 5.0);
		setStatepos(st6, 15.0, 7.5, 15.0);

		List<StateMemberType> states = new ArrayList<StateMemberType>();
		createStateMember(st1, states);
		createStateMember(st2, states);
		createStateMember(st3, states);
		createStateMember(st4, states);
		createStateMember(st5, states);
		createStateMember(st6, states);

		// set duality - cellspace

		setDualitycellspace(cs1, st1);
		setDualitycellspace(cs2, st2);
		setDualitycellspace(cs3, st3);
		setDualitycellspace(cs4, st4);
		setDualitycellspace(cs5, st5);
		setDualitycellspace(cs6, st6);

		// set duality - state

		setdualityState(cs1, st1);
		setdualityState(cs2, st2);
		setdualityState(cs3, st3);
		setdualityState(cs4, st4);
		setdualityState(cs5, st5);
		setdualityState(cs6, st6);

		nodes.setStateMember(states);

		JAXBContext context = JAXBContext.newInstance(IndoorFeaturesType.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
				IndoorGMLNameSpaceMapper.DEFAULT_URI + " http://schemas.opengis.net/indoorgml/1.0/indoorgmlcore.xsd " +
				IndoorGMLNameSpaceMapper.NAVIGATION_URI + " http://schemas.opengis.net/indoorgml/1.0/indoorgmlnavi.xsd" +
				IndoorGMLNameSpaceMapper.XLINK_URI + " https://www.w3.org/XML/2008/06/xlink.xsd");
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new IndoorGMLNameSpaceMapper());
		marshaller.marshal(indoorObjectFactory.createIndoorFeatures(indoorFeatures), fout);


	}

	public static void setdualityState(CellSpaceType cellspace, StateType state) {
		CellSpacePropertyType cellspaceProp = new CellSpacePropertyType();
		cellspaceProp.setHref("#" + cellspace.getId());
		state.setDuality(cellspaceProp);
	}

	public static void setDualitycellspace(CellSpaceType cellspace, StateType state) {
		StatePropertyType stateProp = new StatePropertyType();
		stateProp.setHref("#" + state.getId());
		cellspace.setDuality(stateProp);
	}

	public static void createCellspaceMember(CellSpaceType cellspace, List<CellSpaceMemberType> cellspacemembers) {
		CellSpaceMemberType cellspacemember1 = new CellSpaceMemberType();
		cellspacemembers.add(cellspacemember1);
		cellspacemember1.setCellSpace(indoorObjectFactory.createCellSpace(cellspace));
	}

	public static void createStateMember(StateType state, List<StateMemberType> states) {
		StateMemberType statemember1 = new StateMemberType();
		states.add(statemember1);
		statemember1.setState(state);
	}

	private static CellSpaceType createCellspace(String id, String uri) {
		CellSpaceType cellspace = new CellSpaceType();
		cellspace.setId(id);

		CellSpaceGeometryType cg = new CellSpaceGeometryType();
		SolidPropertyType sp = new SolidPropertyType();

		SolidType solid = new SolidType();
		ShellPropertyType shellProperty = new ShellPropertyType();
		ShellType shell = new ShellType();
		SurfacePropertyType sur = new SurfacePropertyType();

		shell.setSurfaceMember(List.of(sur));
		shellProperty.setShell(shell);
		solid.setExterior(shellProperty);
		sp.setAbstractSolid(gmlObjectFactory.createSolid(solid));
		cg.setGeometry3D(sp);

		ExternalObjectReferenceType extRefObj = new ExternalObjectReferenceType();
		extRefObj.setUri("file://GMLID_"+id+".city.gml"); // (B) uri represents whole object
		extRefObj.setName("GMLID_"+id); // (A) reference to single object within foreign data set, e.g. CityGML
		ExternalReferenceType extRef = new ExternalReferenceType();
		extRef.setInformationSystem("test.city.gml"); // (A) uri represents foreign model with multiple objects, e.g. CityGML
		extRef.setExternalObject(extRefObj);
		cellspace.setExternalReference(List.of(extRef));

		PolygonType polygon = new PolygonType();

		LinearRingType linearRing = new LinearRingType();
		DirectPositionListType directPositions = new DirectPositionListType();
		linearRing.setPosList(directPositions);

		List<Double> coordinates = Arrays.asList(0.,0.,0.,1.,0.,0.,1.,1.,0.,0.,1.,0.);
		directPositions.setValue(coordinates);

		AbstractRingPropertyType abstractRingProperty = new AbstractRingPropertyType();
		abstractRingProperty.setAbstractRing(gmlObjectFactory.createLinearRing(linearRing));
		polygon.setExterior(abstractRingProperty);

		cellspace.setCellSpaceGeometry(cg);
		cg.setGeometry2D(sur);
		sur.setAbstractSurface(gmlObjectFactory.createPolygon(polygon));
		return cellspace;
	}

	private static StateType createState(String id) {
		StateType name = new StateType();
		name.setId(id);
		return name;
	}

	private static void setStatepos(StateType state, double x, double y, double z) {
		PointPropertyType pointProp = new PointPropertyType();
		PointType point = new PointType();
		DirectPositionType dirPos = new DirectPositionType();
		dirPos.withValue(x, y, z).withSrsDimension(BigInteger.valueOf(3));
		point.setPos(dirPos);
		pointProp.setPoint(point);
		state.setGeometry(pointProp);
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
