package org.opensourcebim.levelout.samples;


import org.opensourcebim.levelout.intermediatemodel.Building;
import org.opensourcebim.levelout.intermediatemodel.Corner;
import org.opensourcebim.levelout.intermediatemodel.Door;
import org.opensourcebim.levelout.intermediatemodel.Room;
import org.opensourcebim.levelout.intermediatemodel.Storey;

import java.util.Arrays;
import java.util.Collections;

public class IntermediateResidential {

	public static Building create(){
		Corner p1 = new Corner(0,0,0);
		Corner p2 = new Corner(6, 0, 0);
		Corner p3 = new Corner(6, 6, 0);
		Corner p4 = new Corner(0,6,0);
		Corner p5 = new Corner(10, 0, 0);
		Corner p6 = new Corner(10, 2, 0);
		Corner p7 = new Corner(6, 2, 0);
		Corner p8 = new Corner(10, 6, 0);
		Corner p11 = new Corner(0,0,3);
		Corner p12 = new Corner(6, 0, 3);
		Corner p13 = new Corner(6, 6, 3);
		Corner p14 = new Corner(0,6,3);
		Corner p15 = new Corner(10, 0, 3);
		Corner p16 = new Corner(10, 2, 3);
		Corner p17 = new Corner(6, 2, 3);
		Corner p18 = new Corner(10, 6, 3);
		Corner p21 = new Corner(1, 0, 0);
		Corner p22 = new Corner(2, 0, 0);
		Corner p23 = new Corner(6, 5, 0);
		Corner p24 = new Corner(6, 6, 0);
		// TODO SK 7: adjacency transitions only: a) use utility method to infer, b) explicit in model (wall?)
		// TODO SK 8: doors in basic form (direct creation), only then move on to create from intermediate
		return new Building(Arrays.asList(
			new Storey(1, Arrays.asList(
				new Room(Arrays.asList( p1,p2,p3,p4 )),
				new Room(Arrays.asList( p2,p5,p6,p7 )),
				new Room(Arrays.asList( p6,p5,p8,p3 ))
			), Arrays.asList(
				new Door(Arrays.asList(p21, p22)),
				new Door(Arrays.asList(p23, p24))
			)),
			new Storey(5, Arrays.asList(
				new Room(Arrays.asList( p11,p12,p13,p14 )),
				new Room(Arrays.asList( p12,p15,p16,p17 )),
				new Room(Arrays.asList( p16,p15,p18,p13 ))
					), Collections.emptyList()
			)
		));
	}

}

