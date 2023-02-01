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
	static boolean testColinear(Door door, Corner corner1, Corner corner2) {
		return false;
	}

	public static Map<Room, Door> analyzeRooms(List<Room> rooms, List<Door> doors) {
		Map<Room, Door> result = new HashMap<>();
		Map<Door, List<Room>> assignment; // (list of one or two rooms) or assign rooms to doors in intermediate model
		for (Room room : rooms) {
			for (Door door : doors) {
				if (isDoorInRoom(room, door)) result.put(room, door);
			}
		}
		return result;
	}

	private static boolean isDoorInRoom(Room room, Door door) {

		for (int i = 0; i < room.getCorners().size() - 1; i++) {
			// look at last point / pair as well
			//Check cross product formula
			Corner roomcorner = room.getCorners().get(i);
			Corner doorcorner1 = door.getCorners().get(0);
			Corner doorcorner2 = door.getCorners().get(1);
			double crossZ = roomcorner.getX() * doorcorner1.getY() - roomcorner.getY() * doorcorner1.getX();
			double crossY = roomcorner.getZ() * doorcorner1.getX() - roomcorner.getX() * doorcorner1.getZ();
			double crossX = roomcorner.getY() * doorcorner1.getZ() - roomcorner.getZ() * doorcorner1.getY();

			double crossZ2 = crossX * doorcorner2.getY() - crossY * doorcorner2.getX();
			double crossY2 = crossZ * doorcorner2.getX() - crossX * doorcorner2.getZ();
			double crossX2 = crossY * doorcorner2.getZ() - crossZ * doorcorner2.getY();
			if (crossX2 == 0 && crossY2 == 0 && crossZ2 == 0) {
				return true;
			}

		}
		return false;
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
