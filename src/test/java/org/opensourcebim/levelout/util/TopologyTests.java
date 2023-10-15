package org.opensourcebim.levelout.util;

import org.junit.Assert;
import org.junit.Test;
import org.opensourcebim.levelout.intermediatemodel.Corner;
import org.opensourcebim.levelout.intermediatemodel.Door;
import org.opensourcebim.levelout.intermediatemodel.Room;
import org.opensourcebim.levelout.intermediatemodel.Storey;

import java.util.List;

public class TopologyTests {
    @Test
    public void testDoorWithRoom(){
        /*
            +----+ +----+ y=3.9
            |    | |    |
            | r1 |d|    | y=1.1-1.9
            +----+ +----+ y=0.1
        x= 0.1-3.9 4.1-6.9
        * */
        Door door = new Door("d", List.of(
                new Corner(3.9, 1.1),
                new Corner(4.1, 1.1),
                new Corner(4.1, 1.9),
                new Corner(3.9, 1.9)
        ));
        Room room1 = new Room("r1", List.of(
                new Corner(0.1, 0.1),
                new Corner(3.9, 0.1),
                new Corner(3.9, 3.9),
                new Corner(0.1, 3.9)
        ));
        Room room2 = new Room("r2", List.of(
                new Corner(4.1, 0.1),
                new Corner(6.9, 0.1),
                new Corner(6.9, 3.9),
                new Corner(4.1, 3.9)
        ));
        door.setInternal(room1, room2);
        Storey storey = new Storey(0, 0, "EG", List.of(room1, room2), List.of(door));

        Topology topology = new Topology();
        topology.init(storey);
        List<Corner> corners1 = topology.getOutline(room1);
        Assert.assertEquals(9, corners1.size());
        Assert.assertEquals(corners1.get(2), door.getCorners().get(0));
        Assert.assertEquals(corners1.get(3), door.getCorners().get(1));
        Corner doorPoint1 = new Corner(4.1, 1.5);
        Assert.assertEquals(corners1.get(4),  doorPoint1);
        Assert.assertEquals(corners1.get(5), door.getCorners().get(2));
        Assert.assertEquals(corners1.get(6), door.getCorners().get(3));
        Assert.assertEquals(topology.getPoint(door), doorPoint1);

        door.setInternal(room2, room1);
        topology.init(storey);
        List<Corner> corners2 = topology.getOutline(room2);
        Assert.assertEquals(9, corners2.size());
        Assert.assertEquals(corners2.get(4), door.getCorners().get(2));
        Assert.assertEquals(corners2.get(5), door.getCorners().get(3));
        Corner doorPoint2 = new Corner(3.9, 1.5);
        Assert.assertEquals(corners2.get(6), doorPoint2);
        Assert.assertEquals(corners2.get(7), door.getCorners().get(0));
        Assert.assertEquals(corners2.get(8), door.getCorners().get(1));
        Assert.assertEquals(topology.getPoint(door), doorPoint2);
    }
}
