package org.opensourcebim.levelout.samples;

import net.opengis.indoorgml.core.v_1_0.IndoorFeaturesType;
import net.opengis.indoorgml.core.v_1_0.PrimalSpaceFeaturesType;
import net.opengis.indoorgml.core.v_1_0.StatePropertyType;
import net.opengis.indoorgml.core.v_1_0.StateType;
import net.opengis.indoorgml.core.v_1_0.TransitionType;
import net.opengis.indoorgml.core.v_1_0.NodesType;
import net.opengis.indoorgml.core.v_1_0.CellSpaceType;
import net.opengis.indoorgml.core.v_1_0.EdgesType;

import org.locationtech.jts.io.ParseException;

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

		IndoorFeaturesType indoorFeatures = indoorGmlBuilder.createIndoorFeatures();
		PrimalSpaceFeaturesType primalSpaceFeature = indoorGmlBuilder.getPrimalSpace(indoorFeatures);

		indoorGmlBuilder.addCellSpace(primalSpaceFeature, cs1);
		indoorGmlBuilder.addCellSpace(primalSpaceFeature, cs2);
		indoorGmlBuilder.addCellSpace(primalSpaceFeature, cs3);
		indoorGmlBuilder.addCellSpace(primalSpaceFeature, cs4);
		indoorGmlBuilder.addCellSpace(primalSpaceFeature, cs5);
		indoorGmlBuilder.addCellSpace(primalSpaceFeature, cs6);

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

		NodesType nodes = indoorGmlBuilder.getFirstDualSpaceLayer(indoorFeatures).getNodes().get(0);
		nodes.setId("n1");

		indoorGmlBuilder.addState(nodes, st1);
		indoorGmlBuilder.addState(nodes, st2);
		indoorGmlBuilder.addState(nodes, st3);
		indoorGmlBuilder.addState(nodes, st4);
		indoorGmlBuilder.addState(nodes, st5);
		indoorGmlBuilder.addState(nodes, st6);

		EdgesType edges = indoorGmlBuilder.getFirstDualSpaceLayer(indoorFeatures).getEdges().get(0);
		edges.setId("e1");
		indoorGmlBuilder.addTransition(edges, t1);

		indoorGmlBuilder.setDuality(cs1, st1);
		indoorGmlBuilder.setDuality(cs2, st2);
		indoorGmlBuilder.setDuality(cs3, st3);
		indoorGmlBuilder.setDuality(cs4, st4);
		indoorGmlBuilder.setDuality(cs5, st5);
		indoorGmlBuilder.setDuality(cs6, st6);

		new IndoorGmlBuilder().write(fout,indoorFeatures);
	}

}
