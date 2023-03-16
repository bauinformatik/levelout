package org.opensourcebim.levelout.util;

import org.opensourcebim.levelout.intermediatemodel.Corner;
import org.opensourcebim.levelout.intermediatemodel.Door;
import org.opensourcebim.levelout.intermediatemodel.Room;

import java.util.*;

public class SpatialAnalysis {

	public static void enrichRooms(List<Room> rooms, List<Door> doors){
		for (Map.Entry<Door, List<Room>> entry: analyzeRooms(rooms, doors).entrySet()){
			Door door = entry.getKey();
			List<Room> connectedRooms = entry.getValue();
			if(connectedRooms.size()==1){
				door.setExternal(rooms.get(0));
			} else if(connectedRooms.size()==2){
				door.setInternal(rooms.get(0), rooms.get(1));
			} else {
				throw new IllegalArgumentException("more or less than 1 or 2 rooms reachable from door");
			}
		}
	}

	static Map<Door, List<Room>> analyzeRooms(List<Room> rooms, List<Door> doors) {
		Map<Door, List<Room>> assignment = new HashMap<>();
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

	static boolean isDoorInRoom(Room room, Door door) {
		for (int i = 0; i < room.getCorners().size(); i++) {
			int k = (i + 1) % (room.getCorners().size());
			Corner corner1 = room.getCorners().get(i);
			Corner corner2 = room.getCorners().get(k);
			if (isDoorOnLine(door, corner1, corner2) && isDoorBetween(door, corner1, corner2)) {
				return true;
			}
		}
		return false;
	}

	static boolean isDoorBetween(Door door, Corner corner1, Corner corner2) {
		Corner doorcorner1 = door.getCorners().get(0);
		Corner doorcorner2 = door.getCorners().get(1);
		double x1 = corner1.getX(), x2 = corner2.getX(), y2 = corner2.getY(), y1 = corner1.getY();
		double dotpro1 = (doorcorner1.getX() - x1) * (x2 - x1) + (doorcorner1.getY() - y1) * (y2 - y1);
		double dotpro2 = (doorcorner2.getX() - x1) * (x2 - x1) + (doorcorner2.getY() - y1) * (y2 - y1);
		double walllength = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
		return dotpro1 >= 0 && dotpro2 >= 0 && dotpro1 <= walllength && dotpro2 <= walllength;
	}

	static boolean isDoorOnLine(Door door, Corner corner1, Corner corner2) {
		return isCollinear(door.getCorners().get(0), corner1, corner2) && isCollinear(door.getCorners().get(1), corner1, corner2);
	}

	static boolean isCollinear(Corner corner1, Corner corner2, Corner corner3) {
		double x1 = corner1.getX(), y1= corner1.getY(), x2 = corner2.getX(), y2 = corner2.getY(), x3 = corner3.getX(), y3 = corner3.getY();
		return x1 * (y3-y2) + x2 * (y1-y3) + x3 * (y2-y1) == 0;
	}

}
