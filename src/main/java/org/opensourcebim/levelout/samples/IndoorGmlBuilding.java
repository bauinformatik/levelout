package org.opensourcebim.levelout.samples;

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
import net.opengis.indoorgml.core.v_1_0.ObjectFactory;
import net.opengis.indoorgml.core.v_1_0.CellSpaceType;
import net.opengis.indoorgml.core.v_1_0.ExternalObjectReferenceType;
import net.opengis.indoorgml.core.v_1_0.ExternalReferenceType;
import net.opengis.gml.v_3_2_1.BoundingShapeType;
import net.opengis.gml.v_3_2_1.DirectPositionListType;
import net.opengis.gml.v_3_2_1.DirectPositionType;
import net.opengis.gml.v_3_2_1.LinearRingType;
import net.opengis.gml.v_3_2_1.PointPropertyType;
import net.opengis.gml.v_3_2_1.PointType;
import net.opengis.gml.v_3_2_1.PolygonType;
import net.opengis.gml.v_3_2_1.RingPropertyType;
import net.opengis.gml.v_3_2_1.ShellPropertyType;
import net.opengis.gml.v_3_2_1.ShellType;
import net.opengis.gml.v_3_2_1.AbstractGeometryType;
import net.opengis.gml.v_3_2_1.AbstractRingPropertyType;
import net.opengis.gml.v_3_2_1.AbstractRingType;
import net.opengis.gml.v_3_2_1.AbstractSolidType;
import net.opengis.gml.v_3_2_1.AbstractSurfaceType;
import net.opengis.gml.v_3_2_1.SolidPropertyType;
import net.opengis.gml.v_3_2_1.SolidType;
import net.opengis.gml.v_3_2_1.SurfacePropertyType;
import net.opengis.gml.v_3_2_1.SurfaceType;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import org.opensourcebim.levelout.samples.IndoorGmlSample.IndoorGMLNameSpaceMapper;
import org.xmlobjects.gml.model.geometry.primitives.LinearRing;
import org.xmlobjects.gml.model.geometry.primitives.Ring;
import org.xmlobjects.gml.model.geometry.primitives.Solid;
import org.xmlobjects.gml.model.geometry.primitives.Surface;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndoorGmlBuilding {
	
	private static net.opengis.indoorgml.core.v_1_0.ObjectFactory objectFactory = new net.opengis.indoorgml.core.v_1_0.ObjectFactory();
	
	private static net.opengis.gml.v_3_2_1.ObjectFactory obj = new net.opengis.gml.v_3_2_1.ObjectFactory();
	
	
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
		

		NodesType nodes  = new NodesType();
		nodes.setId("n1");
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


		
		

		/* PRIMAL SPACE FEATURES*/
		//primalSpaceFeature.setCellSpaceBoundaryMember(null);
		//primalSpaceFeature.withDescription("yes"); // JAXB`







		//CELLSPACE 

		CellSpaceType cs1 = createCellspace("c1",null);
		CellSpaceType cs2 = createCellspace("c2",null);
		CellSpaceType cs3 = createCellspace("c3",null);
		CellSpaceType cs4 = createCellspace("c4",null);
		CellSpaceType cs5 = createCellspace("c5",null);
		CellSpaceType cs6 = createCellspace("c6",null);
		
		List<CellSpaceMemberType> cellspacemembers = new ArrayList<CellSpaceMemberType>();
		createCellspaceMember(cs1,cellspacemembers);
		createCellspaceMember(cs2,cellspacemembers);
		createCellspaceMember(cs3,cellspacemembers);
		createCellspaceMember(cs4,cellspacemembers);
		createCellspaceMember(cs5,cellspacemembers);
		createCellspaceMember(cs6,cellspacemembers);
		
		
		


		primalSpaceFeature.setCellSpaceMember(cellspacemembers);

	
		//State 

		StateType st1 = createState("s1");
		StateType st2 = createState("s2");
		StateType st3 = createState("s3");
		StateType st4 = createState("s4");
		StateType st5 = createState("s5");
		StateType st6 = createState("s6");

				
		setStatepos(st1, 5.0, 5.0, 5.0);
		setStatepos(st2,5.0,5.0,15.0);
		setStatepos(st3,15.0,2.5,5.0);
		setStatepos(st4,15.0,2.5,15.0);
		setStatepos(st5,15.0,7.5,5.0);
		setStatepos(st6,15.0,7.5,15.0);

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
		marshaller.marshal(objectFactory.createIndoorFeatures(indoorFeatures), fout);


	}

	public static void setdualityState(CellSpaceType cellspace, StateType state) {
		CellSpacePropertyType cellspaceProp = new CellSpacePropertyType();
		cellspaceProp.setHref("#"+cellspace.getId());
		state.setDuality(cellspaceProp);
	}

	public static void setDualitycellspace(CellSpaceType cellspace, StateType state) {
		StatePropertyType stateProp = new StatePropertyType();
		stateProp.setHref("#"+state.getId());
		cellspace.setDuality(stateProp);
	}

	public static void createCellspaceMember(CellSpaceType cellspace, List<CellSpaceMemberType> cellspacemembers) {
		CellSpaceMemberType cellspacemember1 = new CellSpaceMemberType();
		cellspacemembers.add(cellspacemember1);	
		cellspacemember1.setCellSpace(objectFactory.createCellSpace(cellspace));
	}

	public static void createStateMember(StateType state, List<StateMemberType> states) {
		StateMemberType statemember1 = new StateMemberType();
		states.add(statemember1);
		statemember1.setState(state);
	}

	public static CellSpaceType createCellspace(String id, String uri) {
		CellSpaceType cellspace = new CellSpaceType();
		cellspace.setId(id);
		
		
		SolidPropertyType  sp = new SolidPropertyType ();
		AbstractSolidType ab = new AbstractSolidType();
		ab.setSrsDimension(BigInteger.valueOf(3));
		//ab.set
		CellSpaceGeometryType cg = new CellSpaceGeometryType();
		
		SolidType sld = new SolidType();
		ShellPropertyType shell = new ShellPropertyType ();
		ShellType sheltyp = new ShellType();
		List<SurfacePropertyType> surlis = new ArrayList<> ();
		sheltyp.setSurfaceMember(surlis);
		SurfacePropertyType sur = new SurfacePropertyType();
		surlis.add(sur);
	//	sur.setAbstractSurface);
		
	shell.setShell(sheltyp);
		sld.setExterior(shell);
		//AbstractSurfaceType abssurtyp = new AbstractSurfaceType();
	//	abssurtyp.set
	//	AbstractSurfaceType abs =
		sur.setAbstractSurface(obj.createAbstractSurface( AbstractSurfaceType()));
		cg.setGeometry2D(sur);
		cg.setGeometry3D(sp);
		
		cellspace.setCellSpaceGeometry(cg);
		cellspace.setCellSpaceGeometry(objectFactory.createCellSpaceGeometryType());
		ExternalObjectReferenceType extrefobj = new ExternalObjectReferenceType();
		extrefobj.setUri(uri);
		ExternalReferenceType extreftyp = new ExternalReferenceType();
		extreftyp.setExternalObject(extrefobj);
		List<ExternalReferenceType> extreflist = new ArrayList<ExternalReferenceType>();
		cellspace.setExternalReference(extreflist);
		
		
		
		
		
		PolygonType pt = new PolygonType();

		LinearRingType linringtyp = new LinearRingType();
		DirectPositionListType dirposlis = new DirectPositionListType();
		linringtyp.setPosList(dirposlis);
	
		DirectPositionType dirPos = new DirectPositionType();
	
	
		
		List <Double> l = new ArrayList();
		l.add(null);
		dirposlis.setValue(l);
		
		AbstractRingPropertyType absringProp = new AbstractRingPropertyType();
		absringProp.setAbstractRing(obj.createAbstractRing(linringtyp));
		pt.setExterior(absringProp);
		
		cellspace.setCellSpaceGeometry(cg);
		
		
		
		cg.setGeometry2D(sur);
		sur.setAbstractSurface(obj.createAbstractSurface(pt));
		
	
		
		
		return cellspace;
	}

	public static StateType createState(String id) {
		StateType name = new StateType();
		name.setId(id);
		return name;
	}

	public static void setStatepos(StateType state, double x, double y, double z) {
		PointPropertyType pointProp = new PointPropertyType();

		PointType point = new PointType();
		
		//point.set

		

		DirectPositionType dirPos = new DirectPositionType();
		
	
		
		
	

		//	dirPos.setValue(Arrays.asList(5.0,5.0,5.0));
		//	dirPos.setSrsDimension(BigInteger.valueOf(3));
		

		dirPos.withValue(x,y,z).withSrsDimension(BigInteger.valueOf(3));

		point.setPos(dirPos);
		pointProp.setPoint(point);


		state.setGeometry(pointProp);
	
	}

	public static void setMetadata(Geometry g, String metadata, Object value) {

		// SET METADATA GEOMETRY

		Map userData = null;

		if (g.getUserData() != null) {
			if (g.getUserData() instanceof Map) {
				userData = (Map) g.getUserData();
			} else {
				userData = new HashMap();
				userData.put(g.getUserData().getClass(), g.getUserData());
			}
		} else {
			userData = new HashMap();
		}

		userData.put(metadata, value);
		g.setUserData(userData);
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




	public static void old() {
		/*
	final GeometryFactory geometryFactory = new GeometryFactory();
	WKTReader3D wkt = new WKTReader3D();
	IndoorGMLMap map = Container.createDocument("testing");

	edu.pnu.stem.dao.IndoorFeaturesDAO.createIndoorFeatures(map, "if1", "indoorfeatures", "testdata", null,
			null, "pf1");


	edu.pnu.stem.dao.PrimalSpaceFeaturesDAO.createPrimalSpaceFeatures(map, "if1", "pf1", null, null,
			cellspacemember, cellspaceboundarymember);

	edu.pnu.stem.dao.MultiLayeredGraphDAO.createMultiLayeredGraph(map, "if1", "mlg1", null, null, null, null);
	edu.pnu.stem.dao.SpaceLayersDAO.createSpaceLayers(map, "mlg1", "slayers1", null, null, null);
	edu.pnu.stem.dao.SpaceLayerDAO.createSpaceLayer(map, "slayers1", "sl1", null, null, null, null);
	edu.pnu.stem.dao.NodesDAO.createNodes(map, "sl1", "n1", null, null, null);

	List<String> partialboundedby = new ArrayList<String>();
	partialboundedby.add("csb1");


	// Define the WKT geometry in String.
	String wktsolid = "SOLID (( ((0 0 0, 0 0 10, 0 10 10, 0 10 0, 0 0 0)), ((0 0 0, 10 0 0, 10 0 10, 0 0 10, 0 0 0)), ((10 10 0, 10 10 10, 10 0 10, 10 0 0, 10 10 0)), ((0 10 0, 0 10 10, 10 10 10, 10 10 0, 0 10 0)),((0 0 0, 0 10 0, 10 10 0, 10 0 0, 0 0 0)), ((0 0 10, 10 0 10, 10 10 10, 0 10 10, 0 0 10)) ))";
	String wktsolid2 = "SOLID (( ((10 0 0, 20 0 0, 20 0 10, 10 0 10, 10 0 0)),((10 0 0, 10 0 10, 10 5 10, 10 5 0, 10 0 0)), ((10 5 0, 20 5 0, 20 5 10, 10 5 10, 10 5 0)), ((20 5 0, 20 5 10, 20 0 10, 20 0 0, 20 5 0)), ((10 0 0, 10 5 0, 20 5 0, 20 0 0, 10 0 0)), ((10 0 10, 20 0 10, 20 5 10, 10 5 10, 10 0 10)) ))";
	String wktsolid3 = "SOLID (( ((10 5 0, 20 5 0, 20 5 10, 10 5 10, 10 5 0)), ((10 5 0, 10 5 10, 10 10 10, 10 10 0, 10 5 0)), ((10 10 0, 20 10 0, 20 10 10, 10 10 10, 10 10 0)), ((20 10 0, 20 10 10, 20 5 10, 20 5 0, 20 10 0)), ((10 5 0, 10 10 0, 20 10 0, 20 5 0, 10 5 0)), ((10 5 10, 20 5 10, 20 10 10, 10 10 10, 10 5 10)) ))";
	String wktsolid4 = "SOLID (( ((0 0 10, 0 0 20, 0 10 20, 0 10 10, 0 0 10)), ((0 0 10, 10 0 10, 10 0 20, 0 0 20, 0 0 10)), ((10 10 10, 10 10 20, 10 0 20, 10 0 10, 10 10 10)), ((0 10 10, 0 10 20, 10 10 20, 10 10 10, 0 10 10)),((0 0 10, 0 10 10, 10 10 10, 10 0 10, 0 0 10)), ((0 0 20, 10 0 20, 10 10 20, 0 10 20, 0 0 20)) ))";
	String wktsolid5 = "SOLID (( ((10 0 10, 20 0 10, 20 0 20, 10 0 20, 10 0 10)),((10 0 10, 10 0 20, 10 5 20, 10 5 10, 10 0 10)), ((10 5 10, 20 5 10, 20 5 20, 10 5 20, 10 5 10)), ((20 5 10, 20 5 20, 20 0 20, 20 0 10, 20 5 10)), ((10 0 10, 10 5 10, 20 5 10, 20 0 10, 10 0 10)), ((10 0 20, 20 0 20, 20 5 20, 10 5 20, 10 0 20)) ))";
	String wktsolid6 = "SOLID (( ((10 5 10, 20 5 10, 20 5 20, 10 5 20, 10 5 10)), ((10 5 10, 10 5 20, 10 10 20, 10 10 10, 10 5 10)), ((10 10 10, 20 10 10, 20 10 20, 10 10 20, 10 10 10)), ((20 10 10, 20 10 20, 20 5 20, 20 5 10, 20 10 10)), ((10 5 10, 10 10 10, 20 10 10, 20 5 10, 10 5 10)), ((10 5 20, 20 5 20, 20 10 20, 10 10 20, 10 5 20)) ))";
	Geometry cg1 = wkt.read(wktsolid);
	Geometry cg2 = wkt.read(wktsolid2);
	Geometry cg3 = wkt.read(wktsolid3);
	Geometry cg4 = wkt.read(wktsolid4);
	Geometry cg5 = wkt.read(wktsolid5);
	Geometry cg6 = wkt.read(wktsolid6);

	String point1 = "POINT (5 5 5)";
	String point2 = "POINT (5 5 15)";
	String point3 = "POINT (15 2.5 5)";
	String point4 = "POINT (15 2.5 15)";
	String point5 = "POINT (15 7.5 5)";
	String point6 = "POINT (15 7.5 15)";

	Geometry sg1 = wkt.read(point1);
	Geometry sg2 = wkt.read(point2);
	Geometry sg3 = wkt.read(point3);
	Geometry sg4 = wkt.read(point4);
	Geometry sg5 = wkt.read(point5);
	Geometry sg6 = wkt.read(point6);

	edu.pnu.stem.util.GeometryUtil.setMetadata(cg1, "id", "cg1");
	edu.pnu.stem.util.GeometryUtil.setMetadata(cg2, "id", "cg2");
	edu.pnu.stem.util.GeometryUtil.setMetadata(cg3, "id", "cg3");
	edu.pnu.stem.util.GeometryUtil.setMetadata(cg4, "id", "cg4");
	edu.pnu.stem.util.GeometryUtil.setMetadata(cg5, "id", "cg5");
	edu.pnu.stem.util.GeometryUtil.setMetadata(cg6, "id", "cg6");

	edu.pnu.stem.util.GeometryUtil.setMetadata(sg1, "id", "sg1");
	edu.pnu.stem.util.GeometryUtil.setMetadata(sg2, "id", "sg2");
	edu.pnu.stem.util.GeometryUtil.setMetadata(sg3, "id", "sg3");
	edu.pnu.stem.util.GeometryUtil.setMetadata(sg4, "id", "sg4");
	edu.pnu.stem.util.GeometryUtil.setMetadata(sg5, "id", "sg5");
	edu.pnu.stem.util.GeometryUtil.setMetadata(sg6, "id", "sg6");

	edu.pnu.stem.dao.CellSpaceDAO.createCellSpace(map, "pf1", "c1", null, null, cg1, null, partialboundedby);
	edu.pnu.stem.dao.CellSpaceDAO.createCellSpace(map, "pf1", "c2", null, null, cg2, null, partialboundedby);
	edu.pnu.stem.dao.CellSpaceDAO.createCellSpace(map, "pf1", "c3", null, null, cg3, null, partialboundedby);
	edu.pnu.stem.dao.CellSpaceDAO.createCellSpace(map, "pf1", "c4", null, null, cg4, null, partialboundedby);
	edu.pnu.stem.dao.CellSpaceDAO.createCellSpace(map, "pf1", "c5", null, null, cg5, null, partialboundedby);
	edu.pnu.stem.dao.CellSpaceDAO.createCellSpace(map, "pf1", "c6", null, null, cg6, null, partialboundedby);

	edu.pnu.stem.dao.StateDAO.createState(map, "n1", "s1", null, null, sg1, null, null);
	edu.pnu.stem.dao.StateDAO.createState(map, "n1", "s2", null, null, sg2, null, null);
	edu.pnu.stem.dao.StateDAO.createState(map, "n1", "s3", null, null, sg3, null, null);
	edu.pnu.stem.dao.StateDAO.createState(map, "n1", "s4", null, null, sg4, null, null);
	edu.pnu.stem.dao.StateDAO.createState(map, "n1", "s5", null, null, sg5, null, null);
	edu.pnu.stem.dao.StateDAO.createState(map, "n1", "s6", null, null, sg6, null, null);

	edu.pnu.stem.util.GeometryUtil.setMetadata(cbg1, "id", "cbg1");

	edu.pnu.stem.dao.CellSpaceBoundaryDAO.createCellSpaceBoundary(map, "pf1", "csb1", null, null, cbg1, null);

	edu.pnu.stem.dao.CellSpaceDAO.updateCellSpace(map, "pf1", "c1", "room1", "commonroom", cg1, null,
			partialboundedby);
	edu.pnu.stem.dao.CellSpaceDAO.updateCellSpace(map, "pf1", "c2", "room2", "commonroom2", cg2, null,
			partialboundedby);
	edu.pnu.stem.dao.CellSpaceDAO.updateCellSpace(map, "pf1", "c3", "room3", "commonroom3", cg3, null,
			partialboundedby);
	edu.pnu.stem.dao.CellSpaceDAO.updateCellSpace(map, "pf1", "c4", "room1", "commonroom4", cg4, null,
			partialboundedby);
	edu.pnu.stem.dao.CellSpaceDAO.updateCellSpace(map, "pf1", "c5", "room2", "commonroom5", cg5, null,
			partialboundedby);
	edu.pnu.stem.dao.CellSpaceDAO.updateCellSpace(map, "pf1", "c6", "room3", "commonroom6", cg6, null,
			partialboundedby);

	edu.pnu.stem.binder.Mashaller.marshalDocument(null, Container.getDocument("testing"));

		 */

		/*MULTI LAYERED GRAPH AND SPACE LAYERS */

		// how to set parent 



		//indoorFeatures.setPrimalSpaceFeatures(primalSpaceFeatureProperty);

		/* GEOMETRY */ 

		/*String wktsolid = "SOLID (( ((0 0 0, 0 0 10, 0 10 10, 0 10 0, 0 0 0)), ((0 0 0, 10 0 0, 10 0 10, 0 0 10, 0 0 0)), ((10 10 0, 10 10 10, 10 0 10, 10 0 0, 10 10 0)), ((0 10 0, 0 10 10, 10 10 10, 10 10 0, 0 10 0)),((0 0 0, 0 10 0, 10 10 0, 10 0 0, 0 0 0)), ((0 0 10, 10 0 10, 10 10 10, 0 10 10, 0 0 10)) ))";
		String wktsolid2 = "SOLID (( ((10 0 0, 20 0 0, 20 0 10, 10 0 10, 10 0 0)),((10 0 0, 10 0 10, 10 5 10, 10 5 0, 10 0 0)), ((10 5 0, 20 5 0, 20 5 10, 10 5 10, 10 5 0)), ((20 5 0, 20 5 10, 20 0 10, 20 0 0, 20 5 0)), ((10 0 0, 10 5 0, 20 5 0, 20 0 0, 10 0 0)), ((10 0 10, 20 0 10, 20 5 10, 10 5 10, 10 0 10)) ))";
		String wktsolid3 = "SOLID (( ((10 5 0, 20 5 0, 20 5 10, 10 5 10, 10 5 0)), ((10 5 0, 10 5 10, 10 10 10, 10 10 0, 10 5 0)), ((10 10 0, 20 10 0, 20 10 10, 10 10 10, 10 10 0)), ((20 10 0, 20 10 10, 20 5 10, 20 5 0, 20 10 0)), ((10 5 0, 10 10 0, 20 10 0, 20 5 0, 10 5 0)), ((10 5 10, 20 5 10, 20 10 10, 10 10 10, 10 5 10)) ))";
		String wktsolid4 = "SOLID (( ((0 0 10, 0 0 20, 0 10 20, 0 10 10, 0 0 10)), ((0 0 10, 10 0 10, 10 0 20, 0 0 20, 0 0 10)), ((10 10 10, 10 10 20, 10 0 20, 10 0 10, 10 10 10)), ((0 10 10, 0 10 20, 10 10 20, 10 10 10, 0 10 10)),((0 0 10, 0 10 10, 10 10 10, 10 0 10, 0 0 10)), ((0 0 20, 10 0 20, 10 10 20, 0 10 20, 0 0 20)) ))";
		String wktsolid5 = "SOLID (( ((10 0 10, 20 0 10, 20 0 20, 10 0 20, 10 0 10)),((10 0 10, 10 0 20, 10 5 20, 10 5 10, 10 0 10)), ((10 5 10, 20 5 10, 20 5 20, 10 5 20, 10 5 10)), ((20 5 10, 20 5 20, 20 0 20, 20 0 10, 20 5 10)), ((10 0 10, 10 5 10, 20 5 10, 20 0 10, 10 0 10)), ((10 0 20, 20 0 20, 20 5 20, 10 5 20, 10 0 20)) ))";
		String wktsolid6 = "SOLID (( ((10 5 10, 20 5 10, 20 5 20, 10 5 20, 10 5 10)), ((10 5 10, 10 5 20, 10 10 20, 10 10 10, 10 5 10)), ((10 10 10, 20 10 10, 20 10 20, 10 10 20, 10 10 10)), ((20 10 10, 20 10 20, 20 5 20, 20 5 10, 20 10 10)), ((10 5 10, 10 10 10, 20 10 10, 20 5 10, 10 5 10)), ((10 5 20, 20 5 20, 20 10 20, 10 10 20, 10 5 20)) ))";

		 WKTReader wktReader = new WKTReader();


		CellSpaceGeometryType cg1 = new CellSpaceGeometryType();
		cg1.setGeometry3D(null);
		cg1.s
				wkt.read(wktsolid);
		Geometry cg2 = wkt.read(wktsolid2);
		Geometry cg3 = wkt.read(wktsolid3);
		Geometry cg4 = wkt.read(wktsolid4);
		Geometry cg5 = wkt.read(wktsolid5);
		Geometry cg6 = wkt.read(wktsolid6);*/





		/*	double x =5.0;
		double y =5.0;
		double z = 5.0;
		StateType state = new StateType();
		state.setId("s1");*/




		/*CellSpaceMemberType c2 = new CellSpaceMemberType();
		cellspacemember.add(c2);
		CellSpaceMemberType c3 = new CellSpaceMemberType();
		cellspacemember.add(c3);
		CellSpaceMemberType c4 = new CellSpaceMemberType();
		cellspacemember.add(c4);
		CellSpaceMemberType c5 = new CellSpaceMemberType();
		cellspacemember.add(c5);
		CellSpaceMemberType c6 = new CellSpaceMemberType();
		cellspacemember.add(c6);*/

		/*String point1 = "POINT (5 5 5)";
		String point2 = "POINT (5 5 15)";
		String point3 = "POINT (15 2.5 5)";
		String point4 = "POINT (15 2.5 15)";
		String point5 = "POINT (15 7.5 5)";
		String point6 = "POINT (15 7.5 15)";

		 Geometry sg1 = wkt.read(point1);
		 Geometry sg2 = wkt.read(point2);
		 Geometry sg3 = wkt.read(point3);
		 Geometry sg4 = wkt.read(point4);
		 Geometry sg5 = wkt.read(point5);
		 Geometry sg6 = wkt.read(point6);

			setMetadata(cg1, "id", "cg1");
			setMetadata(cg2, "id", "cg2");
			setMetadata(cg3, "id", "cg3");
			setMetadata(cg4, "id", "cg4");
			setMetadata(cg5, "id", "cg5");
			setMetadata(cg6, "id", "cg6");


			setMetadata(sg1, "id", "sg1");
			setMetadata(sg2, "id", "sg2");
			setMetadata(sg3, "id", "sg3");
			setMetadata(sg4, "id", "sg4");
			setMetadata(sg5, "id", "sg5");
			setMetadata(sg6, "id", "sg6");*/
	}
}
