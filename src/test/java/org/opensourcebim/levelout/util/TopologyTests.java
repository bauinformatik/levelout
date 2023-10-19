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

        Corner common1 = new Corner(4.0, 1.1);
        Corner common2 = new Corner(4.0, 1.5);
        Corner common3 = new Corner(4.0, 1.9);

        Topology topology = new Topology();
        topology.init(storey);
        List<Corner> corners1 = topology.getOutline(room1);
        Assert.assertEquals(9, corners1.size());
        Assert.assertEquals(corners1.get(2), door.getCorners().get(0));
        Assert.assertEquals(corners1.get(3), common1);
        Assert.assertEquals(corners1.get(4), common2);
        Assert.assertEquals(corners1.get(5), common3);
        Assert.assertEquals(corners1.get(6), door.getCorners().get(3));

        List<Corner> corners2 = topology.getOutline(room2);
        Assert.assertEquals(9, corners2.size());
        Assert.assertEquals(corners2.get(4), door.getCorners().get(2));
        Assert.assertEquals(corners2.get(5), common3);
        Assert.assertEquals(corners2.get(6), common2);
        Assert.assertEquals(corners2.get(7), common1);
        Assert.assertEquals(corners2.get(8), door.getCorners().get(1));

        Assert.assertEquals(topology.getPoint(door), common2);
    }
    @Test
    public void testCollinear(){
        List<Corner> corners = List.of(
                new Corner(0,1), new Corner(0,1),
                new Corner(0,-2), new Corner(0,-2), new Corner(1,-2), new Corner(2, -2),
                new Corner(3,-2),
                new Corner(3,1), new Corner(0,1) );
        List<Corner> clean = Topology.withoutCollinearCorners(corners);
        Assert.assertEquals(4, clean.size() );
    }

    @Test
    public void testCollinearThresholdSmall(){
        List<Corner> clean = Topology.withoutCollinearCorners(List.of(
                new Corner(0, 0),
                new Corner(0.00005, 2),
                new Corner(0, 4),
                new Corner(3,4)
        ));
        Assert.assertEquals(3, clean.size());
    }

    @Test
    public void testCollinearThresholdScale(){
        Topology.precision = 0.0001 / 0.01; // cm
        List<Corner> clean = Topology.withoutCollinearCorners(List.of(
                new Corner(0, 0),
                new Corner(0.005, 200),
                new Corner(0, 400),
                new Corner(300,400)
        ));
        Assert.assertEquals(3, clean.size());
        Topology.precision = 0.0001;  // back to meters
    }

    @Test
    public void testCollinearThresholdLarger(){
        List<Corner> clean = Topology.withoutCollinearCorners(List.of(
                new Corner(0, 0),
                new Corner(0.001, 2),
                new Corner(0, 4),
                new Corner(3,4)
        ));
        Assert.assertEquals(4, clean.size());
    }

    @Test
    public void testSmiley(){
        Door door = new Door("Keller 01-1", List.of(
                new Corner(4.824896812438965, 1.6608097553253174),
                new Corner(4.924896717071533, 1.660815954208374),
                new Corner(4.924951553344727, 0.7758159637451172),
                new Corner(4.824951648712158, 0.7758097648620605)
        ));
        Room room1 = new Room("01.0.2", List.of(
                new Corner(4.924987316131592, 0.20000000298023224),
                new Corner(4.924862384796143, 2.2100000381469727),
                new Corner(7.275000095367432, 2.2100000381469727),
                new Corner(7.275000095367432, 0.20000000298023224)
        ));
        Room room2 = new Room("01.0.3", List.of(
                new Corner(0.20000000298023224, 0.20000000298023224),
                new Corner(0.20000000298023224, 2.2100000381469727),
                new Corner(4.824862480163574, 2.2100000381469727),
                new Corner(4.824987411499023, 0.20000000298023224)
        ));
        door.setInternal(room1, room2);
        Storey storey = new Storey(0, 0, "EG", List.of(room1, room2), List.of(door));
        Topology topology = new Topology();
        topology.init(storey);
        Assert.assertEquals(9, topology.getOutline(room1).size());
        Assert.assertEquals(9, topology.getOutline(room2).size());
        Assert.assertTrue(topology.doorPoints.containsKey(door));
    }
}
