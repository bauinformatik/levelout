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
    private final Map<Door, Corner> doorPoints = new HashMap<>();
    private final Map<Room, List<Corner>> roomOutLines = new HashMap<>();

    public void init(Storey storey) {
        roomOutLines.clear();
        for(Room room: storey.getRooms()){
            roomOutLines.put(room, room.getCorners());
        }
        for(Door door: storey.getDoors()){
            enrichRoomOutlines(door);
        }
   }

    private void enrichRoomOutlines(Door doorElement) {
        if(doorElement.getRoom1()==null) return;
        List<Corner> room = roomOutLines.get(doorElement.getRoom1());
        List<Corner> roomNew = new ArrayList<>();
        List<Corner> door = doorElement.getCorners();
        int rs = room.size();
        int ds = door.size();
        for(int ri = 0; ri< rs; ri++){
            Corner room1 = room.get(ri);
            roomNew.add(room1);
            Corner room2 = room.get((ri+1)% rs);
            for(int di = 0; di< ds; di++){
                Corner door1 = door.get(di);
                Corner door2 = door.get((di+1)% ds);
                if(isOnSegment(room1, room2, door1) && isOnSegment(room1, room2, door2)){
                    boolean correctOrder = isCloser(door1, door2, room1);
                    int increment = correctOrder ? -1 : 1; // TODO use predetermined polygon sense insteead
                    int start = correctOrder ? di : (di+1)%ds ;
                    Corner d1 = door.get(start);
                    Corner d3 = door.get((start + ds + increment) % ds);
                    Corner d2 = midPoint(d1, d3);
                    Corner d4 = door.get((start + ds + (increment * 2)) % ds);
                    Corner d6 = door.get((start + ds + (increment * 3)) % ds);
                    Corner d5 = midPoint(d4, d6);
                    Corner common = midPoint(d2, d5);
                    roomNew.add(d1);
                    roomNew.add(d2);
                    roomNew.add(common);
                    roomNew.add(d5);
                    roomNew.add(d6);
                    doorPoints.put(doorElement, common);
                    if(doorElement.getRoom2()!=null){
                        List<Corner> opposite = roomOutLines.get(doorElement.getRoom2());
                        List<Corner> oppositeNew = new ArrayList<>();
                        int r2s = opposite.size();
                        for(int r2i = 0; r2i < r2s; r2i++){
                            Corner opposite1 = opposite.get(r2i);
                            oppositeNew.add(opposite1);
                            Corner opposite2 = opposite.get((r2i+1)%r2s);
                            if(isOnSegment(opposite1, opposite2, d3) && isOnSegment(opposite1, opposite2, d4)){  // also valid for common point
                                boolean order = isCloser(d3, d4, opposite1); // TODO use predetermined polygon sense insteead
                                oppositeNew.add(order ? d3 : d4);
                                oppositeNew.add(order ? d2 : d5);
                                oppositeNew.add(common);
                                oppositeNew.add(order ? d5 : d2);
                                oppositeNew.add(order ? d4 : d3);
                            }
                        }
                        roomOutLines.put(doorElement.getRoom2(), oppositeNew);
                    }
                    for(int rem=ri+1; rem<rs; rem++){ // finish new polygon with existing points
                        roomNew.add(room.get(rem));
                    }
                    roomOutLines.put(doorElement.getRoom1(), roomNew);
                    return; // leave early, door can only be on one segment
                }
            }
        }
    }

    private static Corner midPoint(Corner d1, Corner d2) {
        return new Corner((d1.getX() + d2.getX()) / 2, (d1.getY() + d2.getY()) / 2);
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

    public Corner getPoint(Door door) {
        Corner onRoomBorder = doorPoints.get(door);
        List<Double> centroid = door.computeCentroid();
        return onRoomBorder!=null ? onRoomBorder : new Corner(centroid.get(0), centroid.get(1));
    }

    public List<Corner> getOutline(Room room){
        return roomOutLines.containsKey(room) ? roomOutLines.get(room) : room.getCorners();
    }
}
