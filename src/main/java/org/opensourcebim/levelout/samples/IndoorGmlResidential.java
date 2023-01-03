package org.opensourcebim.levelout.samples;

import net.opengis.indoorgml.core.v_1_0.IndoorFeaturesType;
import net.opengis.indoorgml.core.v_1_0.MultiLayeredGraphPropertyType;
import net.opengis.indoorgml.core.v_1_0.PrimalSpaceFeaturesPropertyType;
import net.opengis.indoorgml.core.v_1_0.PrimalSpaceFeaturesType;
import net.opengis.indoorgml.core.v_1_0.SpaceLayerMemberType;
import net.opengis.indoorgml.core.v_1_0.SpaceLayerType;
import net.opengis.indoorgml.core.v_1_0.SpaceLayersType;
import net.opengis.indoorgml.core.v_1_0.StatePropertyType;
import net.opengis.indoorgml.core.v_1_0.StateType;
import net.opengis.indoorgml.core.v_1_0.TransitionType;
import net.opengis.indoorgml.core.v_1_0.MultiLayeredGraphType;
import net.opengis.indoorgml.core.v_1_0.NodesType;
import net.opengis.indoorgml.core.v_1_0.CellSpaceType;
import net.opengis.indoorgml.core.v_1_0.EdgesType;

import org.locationtech.jts.io.ParseException;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import org.opensourcebim.levelout.builders.IndoorGmlBuilder;

import javax.xml.bind.JAXBException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IndoorGmlResidential {


	public static void main(String[] args) throws JAXBException, ParseException, FileNotFoundException {

		String fileName = "output/outindoor5_6.gml";
		FileOutputStream fout = new FileOutputStream(fileName);
		IndoorGmlBuilder indoorGmlBuilder = new IndoorGmlBuilder();

		CellSpaceType cs1 = indoorGmlBuilder.createCellSpace("c1");
		CellSpaceType cs2 = indoorGmlBuilder.createCellSpace("c2");
		CellSpaceType cs3 = indoorGmlBuilder.createCellSpace("c3");
		CellSpaceType cs4 = indoorGmlBuilder.createCellSpace("c4");
		CellSpaceType cs5 = indoorGmlBuilder.createCellSpace("c5");
		CellSpaceType cs6 = indoorGmlBuilder.createCellSpace("c6");

		// TODO use realistic values here
		List<Double> coordinates = Arrays.asList(0.,0.,0.,1.,0.,0.,1.,1.,0.,0.,1.,0.);
		for(CellSpaceType cs : List.of(cs1, cs2, cs3, cs4, cs5)){
			indoorGmlBuilder.add2DGeometry(cs, coordinates);
			indoorGmlBuilder.add3DGeometry(cs, coordinates);
		}

		PrimalSpaceFeaturesType primalSpaceFeature = new PrimalSpaceFeaturesType();
		primalSpaceFeature.setId("pf1");

		indoorGmlBuilder.addCellSpace(primalSpaceFeature, cs1);
		indoorGmlBuilder.addCellSpace(primalSpaceFeature, cs2);
		indoorGmlBuilder.addCellSpace(primalSpaceFeature, cs3);
		indoorGmlBuilder.addCellSpace(primalSpaceFeature, cs4);
		indoorGmlBuilder.addCellSpace(primalSpaceFeature, cs5);
		indoorGmlBuilder.addCellSpace(primalSpaceFeature, cs6);

		PrimalSpaceFeaturesPropertyType primalSpaceFeaturesProperty = new PrimalSpaceFeaturesPropertyType();
		primalSpaceFeaturesProperty.setPrimalSpaceFeatures(primalSpaceFeature);

		StateType st1 = indoorGmlBuilder.createState("s1");
		StateType st2 = indoorGmlBuilder.createState("s2");
		StateType st3 = indoorGmlBuilder.createState("s3");
		StateType st4 = indoorGmlBuilder.createState("s4");
		StateType st5 = indoorGmlBuilder.createState("s5");
		StateType st6 = indoorGmlBuilder.createState("s6");
		
		
		TransitionType t1 = indoorGmlBuilder.createTransition("t1");
		List<StatePropertyType> stateProplist = new ArrayList<>();
		
		
		StatePropertyType stateProp = new StatePropertyType ();
		stateProplist.add(stateProp);
		stateProp.setState(st1);
		
		StatePropertyType stateProp2 = new StatePropertyType ();
		stateProplist.add(stateProp2);
		stateProp2.setState(st2);
		
		
		t1.setConnects(stateProplist);

		indoorGmlBuilder.setTransitionPos(t1, Arrays.asList(5.,5.,5.,5.,5.,15.));

		indoorGmlBuilder.setStatePos(st1, 5.0, 5.0, 5.0);
		indoorGmlBuilder.setStatePos(st2, 5.0, 5.0, 15.0);
		indoorGmlBuilder.setStatePos(st3, 15.0, 2.5, 5.0);
		indoorGmlBuilder.setStatePos(st4, 15.0, 2.5, 15.0);
		indoorGmlBuilder.setStatePos(st5, 15.0, 7.5, 5.0);
		indoorGmlBuilder.setStatePos(st6, 15.0, 7.5, 15.0);

		NodesType nodes = new NodesType();
		nodes.setId("n1");
		indoorGmlBuilder.addState(nodes, st1);
		indoorGmlBuilder.addState(nodes, st2);
		indoorGmlBuilder.addState(nodes, st3);
		indoorGmlBuilder.addState(nodes, st4);
		indoorGmlBuilder.addState(nodes, st5);
		indoorGmlBuilder.addState(nodes, st6);
		
		EdgesType edges = new EdgesType();
		edges.setId("e1");
		indoorGmlBuilder.addTransition(edges, t1);

		indoorGmlBuilder.setDualityCellSpace(cs1, st1);
		indoorGmlBuilder.setDualityCellSpace(cs2, st2);
		indoorGmlBuilder.setDualityCellSpace(cs3, st3);
		indoorGmlBuilder.setDualityCellSpace(cs4, st4);
		indoorGmlBuilder.setDualityCellSpace(cs5, st5);
		indoorGmlBuilder.setDualityCellSpace(cs6, st6);

		indoorGmlBuilder.setDualityState(st1, cs1);
		indoorGmlBuilder.setDualityState(st2, cs2);
		indoorGmlBuilder.setDualityState(st3, cs3);
		indoorGmlBuilder.setDualityState(st4, cs4);
		indoorGmlBuilder.setDualityState(st5, cs5);
		indoorGmlBuilder.setDualityState(st6, cs6);

		SpaceLayerType spaceLayer = new SpaceLayerType();
		spaceLayer.setId("sl1");
		spaceLayer.setNodes(List.of(nodes));
		spaceLayer.setEdges(List.of(edges));

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

		IndoorFeaturesType indoorFeatures = new IndoorFeaturesType();
		indoorFeatures.setId("if1");
		indoorFeatures.setPrimalSpaceFeatures(primalSpaceFeaturesProperty);
		indoorFeatures.setMultiLayeredGraph(multiLayeredGraphProperty);

		new IndoorGmlBuilder().write(fout,indoorFeatures);
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
