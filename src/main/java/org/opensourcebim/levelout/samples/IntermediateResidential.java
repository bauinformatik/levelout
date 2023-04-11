package org.opensourcebim.levelout.samples;


import org.opensourcebim.levelout.intermediatemodel.Building;
import org.opensourcebim.levelout.intermediatemodel.Corner;
import org.opensourcebim.levelout.intermediatemodel.Door;
import org.opensourcebim.levelout.intermediatemodel.Room;
import org.opensourcebim.levelout.intermediatemodel.Storey;
import org.opensourcebim.levelout.intermediatemodel.geo.GeodeticOriginCRS;
import org.opensourcebim.levelout.intermediatemodel.geo.GeodeticPoint;

import java.util.Arrays;
import java.util.Collections;

public class IntermediateResidential {

	public static Building create(){
		/*
		  4         3     8
		  +---------+-----+
		  |      D2 ┼ R3  |
		  |  R1   7 +-----+ 6
		  |         | R2  |
		  +---++----+-----+
		  1   D1    2     5
		*/

		Corner p1 = new Corner(0,0);
		Corner p2 = new Corner(6, 0);
		Corner p3 = new Corner(6, 6);
		Corner p4 = new Corner(0,6);
		Corner p5 = new Corner(10, 0);
		Corner p6 = new Corner(10, 2);
		Corner p7 = new Corner(6, 2);
		Corner p8 = new Corner(10, 6);
		Corner p11 = new Corner(0,0);
		Corner p12 = new Corner(6, 0);
		Corner p13 = new Corner(6, 6);
		Corner p14 = new Corner(0,6);
		Corner p15 = new Corner(10, 0);
		Corner p16 = new Corner(10, 2);
		Corner p17 = new Corner(6, 2);
		Corner p18 = new Corner(10, 6);
		Corner p21 = new Corner(1, 0);
		Corner p22 = new Corner(2, 0);
		Corner p23 = new Corner(6, 5);
		Corner p24 = new Corner(6, 6);
		Corner p25 = new Corner(1, 0);
		Corner p26 = new Corner(2, 0);
		Corner p27 = new Corner(6, 5);
		Corner p28 = new Corner(6, 6);
		// TODO SK 7: adjacency transitions only: a) use utility method to infer, b) explicit in model (wall?)
		// TODO SK 8: doors in basic form (direct creation), only then move on to create from intermediate
		Room room1 = new Room("0.1", Arrays.asList(p1, p2, p3, p4));
		Room room2 = new Room("0.2", Arrays.asList(p2, p5, p6, p7));
		Room room3 = new Room("0.3", Arrays.asList(p7, p6, p8, p3));
		Door door1 = new Door(Arrays.asList(p21, p22,p26,p25));
		door1.setExternal(room1);
		Door door2 = new Door(Arrays.asList(p23, p24,p28,p27));
		door2.setInternal(room1, room3);
		return new Building(Arrays.asList(
			new Storey(0, 0, "EG", Arrays.asList(
				room1, room2, room3
			), Arrays.asList(
				door1, door2
			)),
			new Storey(1, 3, "OG", Arrays.asList(
				new Room("1.1", Arrays.asList( p11,p12,p13,p14 )),
				new Room("1.2", Arrays.asList( p12,p15,p16,p17 )),
				new Room("1.3", Arrays.asList( p17,p16,p18,p13 ))
					),
				Collections.emptyList())
		), Arrays.asList( p1, p5, p8, p4 ),
			new GeodeticOriginCRS(new GeodeticPoint(50.9772, 11.3465, 0),0.23886154 ));
	}
}

