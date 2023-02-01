package org.opensourcebim.levelout.samples;

import net.opengis.indoorgml.core.v_1_0.IndoorFeaturesType;
import net.opengis.indoorgml.core.v_1_0.PrimalSpaceFeaturesType;
import net.opengis.indoorgml.core.v_1_0.StatePropertyType;
import net.opengis.indoorgml.core.v_1_0.StateType;
import net.opengis.indoorgml.core.v_1_0.TransitionType;
import net.opengis.indoorgml.core.v_1_0.NodesType;
import net.opengis.indoorgml.core.v_1_0.CellSpaceType;
import net.opengis.indoorgml.core.v_1_0.EdgesType;

import org.opensourcebim.levelout.builders.IndoorGmlBuilder;

import javax.xml.bind.JAXBException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IndoorGmlResidential {

	public static void main(String[] args) throws FileNotFoundException, JAXBException {

		String fileName = "output/outindoor5_6.gml";
		FileOutputStream fout = new FileOutputStream(fileName);
		IndoorGmlBuilder indoorGmlBuilder = new IndoorGmlBuilder();

		CellSpaceType cs1 = indoorGmlBuilder.createCellSpace("c1");
		CellSpaceType cs2 = indoorGmlBuilder.createCellSpace("c2");
		CellSpaceType cs3 = indoorGmlBuilder.createCellSpace("c3");
		CellSpaceType cs4 = indoorGmlBuilder.createCellSpace("c4");
		CellSpaceType cs5 = indoorGmlBuilder.createCellSpace("c5");
		CellSpaceType cs6 = indoorGmlBuilder.createCellSpace("c6");
		
		List<CellSpaceType> cellspacelist = List.of(cs1, cs2, cs3, cs4, cs5, cs6);

		List<Double> coordinates1 = Arrays.asList(0., 0., 0., 0., 6., 0., 6., 6., 0., 6., 0., 0.);
		List<Double> coordinates2 = Arrays.asList(6., 0., 0., 6., 2., 0., 10., 2., 0., 10., 0., 0.);
		List<Double> coordinates3 = Arrays.asList(6., 2., 0., 6., 6., 0., 10., 6., 0., 10., 2., 0.);
		List<Double> coordinates4 = Arrays.asList(0., 0., 3., 0., 6., 3., 6., 6., 3., 6., 0., 3.);
		List<Double> coordinates5 = Arrays.asList(6., 0., 3., 6., 2., 3., 10., 2., 3., 10., 0., 3.);
		List<Double> coordinates6 = Arrays.asList(6., 2., 3., 6., 6., 3., 10., 6., 3., 10., 2., 3.);
		
		List<List> coordinateslist =  List.of(coordinates1, coordinates2, coordinates3, coordinates4, coordinates5,
				coordinates6);
		for (int i=0;i<cellspacelist.size();i++)
		{
			indoorGmlBuilder.add2DGeometry(cellspacelist.get(i), coordinateslist.get(i));
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
		// TODO SK 1: move the following to builder method createTransition("t1", st1,
		// st2)
		// createTransition(Room room1, Room room2) - look up states for rooms
		// createTransition(Door door) - look up rooms for door (from analysis)
		List<StatePropertyType> stateProplist = new ArrayList<>();

		StatePropertyType stateProp = new StatePropertyType();
		stateProplist.add(stateProp);
		stateProp.setState(st1);

		StatePropertyType stateProp2 = new StatePropertyType();
		stateProplist.add(stateProp2);
		stateProp2.setState(st2);

		t1.setConnects(stateProplist);

		indoorGmlBuilder.setTransitionPos(t1, Arrays.asList(5., 5., 5., 5., 5., 15.));

		indoorGmlBuilder.setStatePos(st1, 3.0, 3.0, 1.5);
		indoorGmlBuilder.setStatePos(st2,8.0, 1.0, 1.5 );
		indoorGmlBuilder.setStatePos(st3, 8.0, 4, 1.5);
		indoorGmlBuilder.setStatePos(st4, 3.0, 3.0, 4.5);
		indoorGmlBuilder.setStatePos(st5,8.0, 1.0, 4.5 );
		indoorGmlBuilder.setStatePos(st6, 8.0, 4, 4.5);

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

		new IndoorGmlBuilder().write(fout, indoorFeatures);
	}

}
