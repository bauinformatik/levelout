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

public class IndoorGmlResidential {

	private static final net.opengis.indoorgml.core.v_1_0.ObjectFactory indoorObjectFactory = new net.opengis.indoorgml.core.v_1_0.ObjectFactory();
	private static final net.opengis.gml.v_3_2_1.ObjectFactory gmlObjectFactory = new net.opengis.gml.v_3_2_1.ObjectFactory();

	public static void main(String[] args) throws JAXBException, ParseException, FileNotFoundException {

		String fileName = "output/outindoor5.gml";
		FileOutputStream fout = new FileOutputStream(fileName);

		CellSpaceType cs1 = createCellSpace("c1", null);
		CellSpaceType cs2 = createCellSpace("c2", null);
		CellSpaceType cs3 = createCellSpace("c3", null);
		CellSpaceType cs4 = createCellSpace("c4", null);
		CellSpaceType cs5 = createCellSpace("c5", null);
		CellSpaceType cs6 = createCellSpace("c6", null);

		List<CellSpaceMemberType> cellspacemembers = new ArrayList<>();
		createCellSpaceMember(cs1, cellspacemembers);
		createCellSpaceMember(cs2, cellspacemembers);
		createCellSpaceMember(cs3, cellspacemembers);
		createCellSpaceMember(cs4, cellspacemembers);
		createCellSpaceMember(cs5, cellspacemembers);
		createCellSpaceMember(cs6, cellspacemembers);

		PrimalSpaceFeaturesType primalSpaceFeature = new PrimalSpaceFeaturesType();
		primalSpaceFeature.setId("pf1");
		primalSpaceFeature.setCellSpaceMember(cellspacemembers);

		PrimalSpaceFeaturesPropertyType primalSpaceFeaturesProperty = new PrimalSpaceFeaturesPropertyType();
		primalSpaceFeaturesProperty.setPrimalSpaceFeatures(primalSpaceFeature);

		StateType st1 = createState("s1");
		StateType st2 = createState("s2");
		StateType st3 = createState("s3");
		StateType st4 = createState("s4");
		StateType st5 = createState("s5");
		StateType st6 = createState("s6");

		setStatePos(st1, 5.0, 5.0, 5.0);
		setStatePos(st2, 5.0, 5.0, 15.0);
		setStatePos(st3, 15.0, 2.5, 5.0);
		setStatePos(st4, 15.0, 2.5, 15.0);
		setStatePos(st5, 15.0, 7.5, 5.0);
		setStatePos(st6, 15.0, 7.5, 15.0);

		List<StateMemberType> states = new ArrayList<>();
		createStateMember(st1, states);
		createStateMember(st2, states);
		createStateMember(st3, states);
		createStateMember(st4, states);
		createStateMember(st5, states);
		createStateMember(st6, states);

		setDualityCellSpace(cs1, st1);
		setDualityCellSpace(cs2, st2);
		setDualityCellSpace(cs3, st3);
		setDualityCellSpace(cs4, st4);
		setDualityCellSpace(cs5, st5);
		setDualityCellSpace(cs6, st6);

		setDualityState(st1, cs1);
		setDualityState(st2, cs2);
		setDualityState(st3, cs3);
		setDualityState(st4, cs4);
		setDualityState(st5, cs5);
		setDualityState(st6, cs6);

		NodesType nodes = new NodesType();
		nodes.setId("n1");
		nodes.setStateMember(states);
		List<NodesType> nodesList = List.of(nodes);

		SpaceLayerType spaceLayer = new SpaceLayerType();
		spaceLayer.setId("sl1");
		spaceLayer.setNodes(nodesList);

		SpaceLayerMemberType spaceLayerMember = new SpaceLayerMemberType();
		spaceLayerMember.setSpaceLayer(spaceLayer);
		List<SpaceLayerMemberType> spaceLayerMembers = List.of(spaceLayerMember);

		SpaceLayersType spaceLayers = new SpaceLayersType();
		spaceLayers.setId("slayers1");
		spaceLayers.setSpaceLayerMember(spaceLayerMembers);
		List<SpaceLayersType> spaceLayersList = List.of(spaceLayers);

		MultiLayeredGraphType multiLayeredGraph = new MultiLayeredGraphType();
		multiLayeredGraph.setId("mlg1");
		multiLayeredGraph.setSpaceLayers(spaceLayersList);

		MultiLayeredGraphPropertyType multiLayeredGraphProperty = new MultiLayeredGraphPropertyType();
		multiLayeredGraphProperty.setMultiLayeredGraph(multiLayeredGraph);

		IndoorFeaturesType indoorFeatures = new IndoorFeaturesType(); // description
		indoorFeatures.setId("if1");
		indoorFeatures.setPrimalSpaceFeatures(primalSpaceFeaturesProperty);
		indoorFeatures.setMultiLayeredGraph(multiLayeredGraphProperty);

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

	public static void setDualityState(StateType state, CellSpaceType cellSpace) {
		CellSpacePropertyType cellSpaceProperty = new CellSpacePropertyType();
		cellSpaceProperty.setHref("#" + cellSpace.getId());
		state.setDuality(cellSpaceProperty);
	}

	public static void setDualityCellSpace(CellSpaceType cellSpace, StateType state) {
		StatePropertyType stateProperty = new StatePropertyType();
		stateProperty.setHref("#" + state.getId());
		cellSpace.setDuality(stateProperty);
	}

	public static void createCellSpaceMember(CellSpaceType cellSpace, List<CellSpaceMemberType> cellSpaceMembers) {
		CellSpaceMemberType cellSpaceMember = new CellSpaceMemberType();
		cellSpaceMembers.add(cellSpaceMember);
		cellSpaceMember.setCellSpace(indoorObjectFactory.createCellSpace(cellSpace));
	}

	public static void createStateMember(StateType state, List<StateMemberType> stateMembers) {
		StateMemberType stateMember = new StateMemberType();
		stateMembers.add(stateMember);
		stateMember.setState(state);
	}

	private static CellSpaceType createCellSpace(String id, String uri) {
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
		cellSpaceGeometry.setGeometry2D(surfaceProperty);
		surfaceProperty.setAbstractSurface(gmlObjectFactory.createPolygon(polygon));
		return cellSpace;
	}

	private static StateType createState(String id) {
		StateType name = new StateType();
		name.setId(id);
		return name;
	}

	private static void setStatePos(StateType state, double x, double y, double z) {
		PointPropertyType pointProperty = new PointPropertyType();
		PointType point = new PointType();
		DirectPositionType directPosition = new DirectPositionType();
		directPosition.withValue(x, y, z).withSrsDimension(BigInteger.valueOf(3));
		point.setPos(directPosition);
		pointProperty.setPoint(point);
		state.setGeometry(pointProperty);
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
