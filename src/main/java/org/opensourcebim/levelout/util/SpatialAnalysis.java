package org.opensourcebim.levelout.util;

import net.opengis.indoorgml.core.v_1_0.CellSpaceBoundaryPropertyType;
import net.opengis.indoorgml.core.v_1_0.CellSpaceType;
import net.opengis.indoorgml.core.v_1_0.StatePropertyType;
import org.opensourcebim.levelout.intermediatemodel.Corner;
import org.opensourcebim.levelout.intermediatemodel.Door;
import org.opensourcebim.levelout.intermediatemodel.Room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpatialAnalysis {
	static boolean isDoorOnSegment(Door door, Corner corner1, Corner corner2) { // Corner p1, p2, p3
		return isColinear(door.getCorners().get(0), corner1, corner2)
				&& isColinear(door.getCorners().get(1), corner1, corner2);
	}

	public static Map<Door, List<Room>> analyzeRooms(List<Room> rooms, List<Door> doors) {

		Map<Door, List<Room>> assignment = new HashMap<>(); // (list of one or two rooms) or assign rooms to doors in
															// intermediate model
		for (Door door : doors) {
			for (Room room : rooms) {
				if (isDoorInRoom(room, door)) {

					if (!assignment.containsKey(door)) {
						assignment.put(door, new ArrayList<>());
					}

					assignment.get(door).add(room);
				}
			}
		}

		return assignment;
	}

	public static List<List<Long>> adjacentRooms(List<Room> rooms) {

		List<List<Corner>> cornerpairs = new ArrayList<>();

		List<List<List<Corner>>> cornerpairstotal = new ArrayList<>();
		List<Long> roomIds = new ArrayList<>();

		List<List<Long>> roomsadjacent = new ArrayList<>();

		for (int i = 0; i < rooms.size(); i++) {
			for (int j = 0; j < rooms.get(i).getCorners().size() - 1; j++) {

				int k = j % (rooms.get(i).getCorners().size());
				cornerpairs.add(createPair(rooms.get(i).getCorners().get(j), rooms.get(i).getCorners().get(k)));
				cornerpairstotal.add(cornerpairs);

			}

			cornerpairs.remove(cornerpairs);
			roomIds.add(rooms.get(i).getId());
		}

		int size = cornerpairstotal.size();
		for (int i = 0; i < cornerpairstotal.size() - 1; i++) {

			for (int j = 0; j < (j % cornerpairstotal.size() - 1); j++) {
				Corner c1 = cornerpairstotal.get(i).get(i).get(0);
				Corner c2 = cornerpairstotal.get(i).get(i).get(1);
				Corner c3 = cornerpairstotal.get(i).get(j).get(0);
				Corner c4 = cornerpairstotal.get(i).get(j).get(1);

				if ((c1.equals(c3) && c2.equals(c4)) || (c1.equals(c4) && c2.equals(c3))) {

					roomsadjacent.add(List.of(roomIds.get(i), roomIds.get(j)));

				}

			}

		}

		return roomsadjacent;

	}

	public static List<Corner> createPair(Corner corner1, Corner corner2) {

		return List.of(corner1, corner2);
	}

	public static boolean isDoorInRoom(Room room, Door door) {

		Corner doorcorner1 = door.getCorners().get(0);
		Corner doorcorner2 = door.getCorners().get(1);
		double slopedoor = (doorcorner2.getY() - doorcorner1.getY()) / (doorcorner2.getX() - doorcorner1.getX());
		for (int i = 0; i < room.getCorners().size(); i++) {
			// look at last point / pair as well
			// Check cross product formula

			int k = (i + 1) % (room.getCorners().size());
			Corner roomcorner1 = room.getCorners().get(i);
			Corner roomcorner2 = room.getCorners().get(k);
			double sloperoom = (roomcorner2.getY() - roomcorner1.getY()) / (roomcorner2.getX() - roomcorner1.getX());
			double crosspro = ((doorcorner1.getY() - roomcorner1.getY()) * (roomcorner2.getX() - roomcorner1.getX()))
					+ ((doorcorner1.getX() - roomcorner1.getX()) * (roomcorner2.getY() - roomcorner1.getY()));
			if (sloperoom == slopedoor || sloperoom == -slopedoor) // check how to avoid minus values , case infinity
																	// and -infinity// the lines are parallel or
																	// coincident
			{
				if ((roomcorner1.getX() == doorcorner1.getX() && roomcorner1.getX() == doorcorner2.getX())
						|| (roomcorner1.getY() == doorcorner1.getY() && roomcorner1.getY() == doorcorner2.getY())
						|| crosspro == 0 || crosspro == -0) {

					double dotpro1 = ((doorcorner1.getX() - roomcorner1.getX())
							* (roomcorner2.getX() - roomcorner1.getX()))
							+ ((doorcorner1.getY() - roomcorner1.getY()) * (roomcorner2.getY() - roomcorner1.getY()));

					double dotpro2 = ((doorcorner2.getX() - roomcorner1.getX())
							* (roomcorner2.getX() - roomcorner1.getX()))
							+ ((doorcorner2.getY() - roomcorner1.getY()) * (roomcorner2.getY() - roomcorner1.getY()));

					if (dotpro1 >= 0 && dotpro2 >= 0) {
						double walllength = ((roomcorner2.getX() - roomcorner1.getX())
								* (roomcorner2.getX() - roomcorner1.getX()))
								+ ((roomcorner2.getY() - roomcorner1.getY())
										* (roomcorner2.getY() - roomcorner1.getY())); // checks whether point lies
																						// between the wall corners

						if (dotpro1 <= walllength && dotpro2 <= walllength) {
							return true;
						}
					}

				}

			}

		}
		return false;
	}

	static boolean isColinear(Corner roomcorner, Corner doorcorner1, Corner doorcorner2) {
		double crossZ = roomcorner.getX() * doorcorner1.getY() - roomcorner.getY() * doorcorner1.getX();
		double crossY = roomcorner.getZ() * doorcorner1.getX() - roomcorner.getX() * doorcorner1.getZ();
		double crossX = roomcorner.getY() * doorcorner1.getZ() - roomcorner.getZ() * doorcorner1.getY();

		double crossZ2 = crossX * doorcorner2.getY() - crossY * doorcorner2.getX();
		double crossY2 = crossZ * doorcorner2.getX() - crossX * doorcorner2.getZ();
		double crossX2 = crossY * doorcorner2.getZ() - crossZ * doorcorner2.getY();
		return crossX2 == 0 && crossY2 == 0 && crossZ2 == 0;
	}

	private void findconnectedStates(List<CellSpaceType> cellspacelist) { // TODO change to room list

		for (int i = 0; i < cellspacelist.size() - 1; i++) {
			List<CellSpaceBoundaryPropertyType> boundarieslist1 = cellspacelist.get(i).getPartialboundedBy();
			List<CellSpaceBoundaryPropertyType> boundarieslist2 = cellspacelist.get(i + 1).getPartialboundedBy();
			List<CellSpaceBoundaryPropertyType> common = new ArrayList<>(boundarieslist1);
			if (common.retainAll(boundarieslist2) == true) {
				StatePropertyType state1 = cellspacelist.get(i).getDuality();
				StatePropertyType state2 = cellspacelist.get(i + 1).getDuality();
				List<StatePropertyType> statelist = new ArrayList<>();
				statelist.add(state1);
				statelist.add(state2);
				statelist.remove(statelist);

			}
		}

	}

}
