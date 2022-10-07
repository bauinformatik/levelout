package org.opensourcebim.levelout.samples;

import net.opengis.indoorgml.core.v_1_0.CellSpaceGeometryType;
import net.opengis.indoorgml.core.v_1_0.CellSpaceMemberType;
import net.opengis.indoorgml.core.v_1_0.IndoorFeaturesType;
import net.opengis.indoorgml.core.v_1_0.MultiLayeredGraphPropertyType;
import net.opengis.indoorgml.core.v_1_0.PrimalSpaceFeaturesPropertyType;
import net.opengis.indoorgml.core.v_1_0.PrimalSpaceFeaturesType;
import net.opengis.indoorgml.core.v_1_0.SpaceLayerType;
import net.opengis.indoorgml.core.v_1_0.SpaceLayersType;
import net.opengis.indoorgml.core.v_1_0.StateType;
import net.opengis.indoorgml.core.v_1_0.MultiLayeredGraphType;
import net.opengis.indoorgml.core.v_1_0.CellSpaceType;
import net.opengis.gml.v_3_2_1.BoundingShapeType;
import net.opengis.gml.v_3_2_1.DirectPositionType;
import net.opengis.gml.v_3_2_1.PointPropertyType;
import net.opengis.gml.v_3_2_1.PointType;
import net.opengis.gml.v_3_2_1.AbstractGeometryType;
import net.opengis.gml.v_3_2_1.SolidPropertyType;
import net.opengis.gml.v_3_2_1.SolidType;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import org.opensourcebim.levelout.samples.IndoorGmlSample.IndoorGMLNameSpaceMapper;
import org.xmlobjects.gml.model.geometry.primitives.Solid;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndoorGmlBuilding {
	public static void main(String[] args) throws JAXBException, ParseException {
		
		
		IndoorFeaturesType indoorFeatures = new IndoorFeaturesType(); // description 
		indoorFeatures.setId("if1");
		
		PrimalSpaceFeaturesType primalSpaceFeature = new PrimalSpaceFeaturesType();
		primalSpaceFeature.setId("pf1");
		
		
		MultiLayeredGraphType multiLayeredGraph = new MultiLayeredGraphType();
		multiLayeredGraph.setId("mlg1");
		
		SpaceLayersType spaceLayers = new SpaceLayersType();
		spaceLayers.setId("slayers1");
		
		SpaceLayerType spaceLayer = new SpaceLayerType();
		spaceLayer.setId("sl1");
		
		
		PrimalSpaceFeaturesPropertyType primalspacefeaturesProp = new PrimalSpaceFeaturesPropertyType();
		primalspacefeaturesProp.setPrimalSpaceFeatures(primalSpaceFeature);
		
		indoorFeatures.setPrimalSpaceFeatures(primalspacefeaturesProp);
		
		MultiLayeredGraphPropertyType  multilayergraphProp = new MultiLayeredGraphPropertyType();
		multilayergraphProp.setMultiLayeredGraph(multiLayeredGraph);
		
		indoorFeatures.setMultiLayeredGraph(multilayergraphProp);
		
		
		
		
		
		List<CellSpaceMemberType> cellspacemember = new ArrayList<CellSpaceMemberType>();
		CellSpaceMemberType c1 = new CellSpaceMemberType();
				cellspacemember.add(c1);		
		CellSpaceMemberType c2 = new CellSpaceMemberType();
				cellspacemember.add(c2);
		CellSpaceMemberType c3 = new CellSpaceMemberType();
				cellspacemember.add(c3);
		CellSpaceMemberType c4 = new CellSpaceMemberType();
				cellspacemember.add(c4);
		CellSpaceMemberType c5 = new CellSpaceMemberType();
				cellspacemember.add(c5);
		CellSpaceMemberType c6 = new CellSpaceMemberType();
			cellspacemember.add(c6);

		List<String> states = new ArrayList<String>();
		state.add("s1");
		state.add("s2");
		state.add("s3");
		state.add("s4");
		state.add("s5");
		state.add("s6");
		
		/* PRIMAL SPACE FEATURES*/

		
		primalSpaceFeature.setCellSpaceMember(cellspacemember);
		primalSpaceFeature.setCellSpaceBoundaryMember(null);
		primalSpaceFeature.setDescription("yes"); // JAXB`
		
		
		
		
		
		/*MULTI LAYERED GRAPH AND SPACE LAYERS */
		
	 // how to set parent 

		
		
		indoorFeatures.setPrimalSpaceFeatures(primalSpaceFeatureProperty);
		
		/* GEOMETRY */ 
		
		String wktsolid = "SOLID (( ((0 0 0, 0 0 10, 0 10 10, 0 10 0, 0 0 0)), ((0 0 0, 10 0 0, 10 0 10, 0 0 10, 0 0 0)), ((10 10 0, 10 10 10, 10 0 10, 10 0 0, 10 10 0)), ((0 10 0, 0 10 10, 10 10 10, 10 10 0, 0 10 0)),((0 0 0, 0 10 0, 10 10 0, 10 0 0, 0 0 0)), ((0 0 10, 10 0 10, 10 10 10, 0 10 10, 0 0 10)) ))";
		String wktsolid2 = "SOLID (( ((10 0 0, 20 0 0, 20 0 10, 10 0 10, 10 0 0)),((10 0 0, 10 0 10, 10 5 10, 10 5 0, 10 0 0)), ((10 5 0, 20 5 0, 20 5 10, 10 5 10, 10 5 0)), ((20 5 0, 20 5 10, 20 0 10, 20 0 0, 20 5 0)), ((10 0 0, 10 5 0, 20 5 0, 20 0 0, 10 0 0)), ((10 0 10, 20 0 10, 20 5 10, 10 5 10, 10 0 10)) ))";
		String wktsolid3 = "SOLID (( ((10 5 0, 20 5 0, 20 5 10, 10 5 10, 10 5 0)), ((10 5 0, 10 5 10, 10 10 10, 10 10 0, 10 5 0)), ((10 10 0, 20 10 0, 20 10 10, 10 10 10, 10 10 0)), ((20 10 0, 20 10 10, 20 5 10, 20 5 0, 20 10 0)), ((10 5 0, 10 10 0, 20 10 0, 20 5 0, 10 5 0)), ((10 5 10, 20 5 10, 20 10 10, 10 10 10, 10 5 10)) ))";
		String wktsolid4 = "SOLID (( ((0 0 10, 0 0 20, 0 10 20, 0 10 10, 0 0 10)), ((0 0 10, 10 0 10, 10 0 20, 0 0 20, 0 0 10)), ((10 10 10, 10 10 20, 10 0 20, 10 0 10, 10 10 10)), ((0 10 10, 0 10 20, 10 10 20, 10 10 10, 0 10 10)),((0 0 10, 0 10 10, 10 10 10, 10 0 10, 0 0 10)), ((0 0 20, 10 0 20, 10 10 20, 0 10 20, 0 0 20)) ))";
		String wktsolid5 = "SOLID (( ((10 0 10, 20 0 10, 20 0 20, 10 0 20, 10 0 10)),((10 0 10, 10 0 20, 10 5 20, 10 5 10, 10 0 10)), ((10 5 10, 20 5 10, 20 5 20, 10 5 20, 10 5 10)), ((20 5 10, 20 5 20, 20 0 20, 20 0 10, 20 5 10)), ((10 0 10, 10 5 10, 20 5 10, 20 0 10, 10 0 10)), ((10 0 20, 20 0 20, 20 5 20, 10 5 20, 10 0 20)) ))";
		String wktsolid6 = "SOLID (( ((10 5 10, 20 5 10, 20 5 20, 10 5 20, 10 5 10)), ((10 5 10, 10 5 20, 10 10 20, 10 10 10, 10 5 10)), ((10 10 10, 20 10 10, 20 10 20, 10 10 20, 10 10 10)), ((20 10 10, 20 10 20, 20 5 20, 20 5 10, 20 10 10)), ((10 5 10, 10 10 10, 20 10 10, 20 5 10, 10 5 10)), ((10 5 20, 20 5 20, 20 10 20, 10 10 20, 10 5 20)) ))";
		
		 WKTReader3D wktReader = new WKTReader3D();
		
		
		CellSpaceGeometryType cg1 = new CellSpaceGeometryType();
		cg1.setGeometry3D(null);
		cg1.s
				wkt.read(wktsolid);
		Geometry cg2 = wkt.read(wktsolid2);
		Geometry cg3 = wkt.read(wktsolid3);
		Geometry cg4 = wkt.read(wktsolid4);
		Geometry cg5 = wkt.read(wktsolid5);
		Geometry cg6 = wkt.read(wktsolid6);
		
		CellSpaceGeometryType cSGeomType = new CellSpaceGeometryType();
		
		
		double x =5.0;
		double y =5.0;
		double z = 5.0;
		StateType state = new StateType();
		state.setId("s1");
		
		setStatepos(state, x, y, z);
		

		
		pt.setPoint(null);
		
		
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
			setMetadata(sg6, "id", "sg6");
			
			
			//CELLSPACE 
			
			CellSpaceType cellspace = new CellSpaceType();
			cellspace.setId("c1");
			cellspace.setCellSpaceGeometry(cSGeomType);
			cellspace.setCellSpaceGeometry(cg1);
			cellspace.setExternalReference(null);// geometry external reference can be set
			cellspace.setex
			// State 
			
			
			state.setge
			
			
			
		
			
			
			
			
			
			
	
		 
		 
		
		// List<String>transition = new ArrayList<String>();

		// List<String>spacelayer = new ArrayList<String>();
		// spacelayer.add("1");

		List<String> cellspaceboundarymember = new ArrayList<String>();
		cellspaceboundarymember.add("csb1");
		// cellspaceboundarymember.add("csb2");
		// cellspaceboundarymember.add("csb3");
		
		JAXBContext context = JAXBContext.newInstance(IndoorFeaturesType.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
				IndoorGMLNameSpaceMapper.DEFAULT_URI + " http://schemas.opengis.net/indoorgml/1.0/indoorgmlcore.xsd " +
				IndoorGMLNameSpaceMapper.NAVIGATION_URI + " http://schemas.opengis.net/indoorgml/1.0/indoorgmlnavi.xsd" +
				IndoorGMLNameSpaceMapper.XLINK_URI + " https://www.w3.org/XML/2008/06/xlink.xsd");
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new IndoorGMLNameSpaceMapper());
		marshaller.marshal(new net.opengis.indoorgml.core.v_1_0.ObjectFactory().createIndoorFeatures(indoorFeatures), System.out);


	}

	private static void setStatepos(StateType state, double x, double y, double z) {
		PointPropertyType pointProp = new PointPropertyType();
		
		PointType point = new PointType();
		
		
		DirectPositionType dirPos = new DirectPositionType();
		
	//	dirPos.setValue(Arrays.asList(5.0,5.0,5.0));
	//	dirPos.setSrsDimension(BigInteger.valueOf(3));
		
		
		dirPos.withValue(x,y,z).withSrsDimension(BigInteger.valueOf(3));
		
		point.setPos(dirPos);
		pointProp.setPoint(point);
		
		
		state.setGeometry(pointProp);
	}

	private static void setMetadata(Geometry g, String metadata, Object value) {
		
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
}
}
