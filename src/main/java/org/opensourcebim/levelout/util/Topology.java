package org.opensourcebim.levelout.util;

import org.opensourcebim.levelout.intermediatemodel.Corner;
import org.opensourcebim.levelout.intermediatemodel.Door;
import org.opensourcebim.levelout.intermediatemodel.Room;
import org.opensourcebim.levelout.intermediatemodel.Storey;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Topology {
    private final Map<Room, List<Door>> roomDoors = new HashMap<>();
    private final Map<Door, Corner> doorPoints = new HashMap<>();
    private final Map<Room, List<Corner>> roomOutLines = new HashMap<>();

    public void init(Storey storey) {
        roomDoors.clear();
        for(Room room: storey.getRooms()){
            roomDoors.put(room, new ArrayList<>());
        }
        for(Door door: storey.getDoors()){
            if(door.getRoom1()!=null) roomDoors.get(door.getRoom1()).add(door);
        }
    }

    public List<Corner> roomWithDoors(Room room){
        List<Door> assignedDoors = roomDoors.get(room);
        if (assignedDoors.isEmpty()) return room.getCorners();
        List<Corner> roomCorners = room.getCorners();
        for(Door door: assignedDoors){
            roomCorners = roomWithDoor(roomCorners, door);
        }
        return roomCorners;
    }

    List<Corner> roomWithDoor(List<Corner> roomCorners, Door door) {
        List<Corner> roomAndDoorCorners = new ArrayList<>();
        List<Corner> doorCorners = door.getCorners();
        int rs = roomCorners.size();
        int ds = doorCorners.size();
        for(int ri = 0; ri< rs; ri++){
            Corner roomCorner1 = roomCorners.get(ri);
            roomAndDoorCorners.add(roomCorner1);
            Corner roomCorner2 = roomCorners.get((ri+1)% roomCorners.size());
            for(int di = 0; di< ds; di++){
                Corner doorCorner1 = doorCorners.get(di);
                Corner doorCorner2 = doorCorners.get((di+1)% ds);
                if(isOnSegment(roomCorner1, roomCorner2, doorCorner1) && isOnSegment(roomCorner1, roomCorner2, doorCorner2)){
                    boolean correctOrder = isCloser(doorCorner1, doorCorner2, roomCorner1);
                    int increment = correctOrder ? -1 : 1; // TODO use predetermined polygon sense insteead
                    int start = correctOrder ? di : (di+1)%ds ;
                    Corner door1 = doorCorners.get((start + ds + increment) % ds);
                    Corner door2 = doorCorners.get((start + ds + (increment * 2)) % ds);
                    Corner commonDoorPoint = new Corner((door1.getX() +door2.getX())/2, (door1.getY()+door2.getY())/2);
                    roomAndDoorCorners.add(doorCorners.get(start));
                    roomAndDoorCorners.add(door1);
                    roomAndDoorCorners.add(commonDoorPoint);
                    roomAndDoorCorners.add(door2);
                    roomAndDoorCorners.add(doorCorners.get((start+ds+(increment*3))% ds));
                    doorPoints.put(door, commonDoorPoint);
                    // TODO leave early, door can only be on one segment
                }
            }
        }
        return roomAndDoorCorners;
    }

    public static List<Corner> withoutCollinearCorners(List<Corner> roomCorners) {
        List<Corner> cleanRoomCorners = new ArrayList<>();
        for(int i = 0; i< roomCorners.size(); i++){
            Corner corner1 = roomCorners.get((i-1+roomCorners.size())% roomCorners.size());
            Corner corner2 = roomCorners.get(i);
            Corner corner3 = roomCorners.get((i+1)% roomCorners.size());
            boolean isOnSegment = isOnSegment(corner1, corner3, corner2);
            if(!isOnSegment){
                cleanRoomCorners.add(corner2);
            }
        }
        return cleanRoomCorners;
    }

    private static boolean isCloser(Corner closer, Corner further, Corner reference){
        Point2D.Double referencePt = new Point2D.Double(reference.getX(), reference.getY());
        return referencePt.distance(closer.getX(), closer.getY()) < referencePt.distance(further.getX(), further.getY());
    }
    private static boolean isOnSegment(Corner segCorner1, Corner segCorner2, Corner corner) {
        Line2D.Double segment = new Line2D.Double(segCorner1.getX(), segCorner1.getY(), segCorner2.getX(), segCorner2.getY());
        return segment.ptSegDist(corner.getX(), corner.getY()) == 0;
    }

    public Corner getDoorPoint(Door door) {
        Corner onRoomBorder = doorPoints.get(door);
        List<Double> centroid = door.computeCentroid();
        return onRoomBorder!=null ? onRoomBorder : new Corner(centroid.get(0), centroid.get(1));
    }
}
