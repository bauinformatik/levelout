package org.opensourcebim.levelout.serializer;

import org.bimserver.BimserverDatabaseException;
import org.bimserver.emf.IfcModelInterface;
import org.bimserver.plugins.serializers.ProjectInfo;
import org.bimserver.plugins.serializers.Serializer;
import org.bimserver.plugins.serializers.SerializerException;
import org.opensourcebim.levelout.intermediatemodel.Storey;
import org.opensourcebim.levelout.intermediatemodel.GenericBuilding;
import org.opensourcebim.levelout.intermediatemodel.Corner;
import org.opensourcebim.levelout.intermediatemodel.Room;

import java.io.OutputStream;
import java.util.Arrays;

public abstract class AbstractLevelOutSerializer implements Serializer {
    GenericBuilding building;

    @Override
    public void init(IfcModelInterface ifcModelInterface, ProjectInfo projectInfo, boolean b) throws SerializerException {
        // TODO map IFC to intermediate model
        Corner p1 = new Corner(0,0,0.,0);
        Corner p2 = new Corner(1, 0, 10, 0);
        Corner p3 = new Corner(2, 10, 10, 0);
        Corner p4 = new Corner(3, 10,0,0);
        Corner p5 = new Corner(4, 15, 10, 0);
        Corner p6 = new Corner(5, 15, 0, 0);
        Corner p11 = new Corner(0,0,0.,3);
        Corner p12 = new Corner(1, 0, 10, 3);
        Corner p13 = new Corner(2, 10, 10, 3);
        Corner p14 = new Corner(3, 10,0,3);
        Corner p15 = new Corner(4, 15, 10, 3);
        Corner p16 = new Corner(5, 15, 0, 3);
        building = new GenericBuilding(Arrays.asList(
            new Storey(0, 0, Arrays.asList(
                    new Room(0, "floor", 2, Arrays.asList( p1,p2,p3,p4 )),
                    new Room(1, "floor", 2, Arrays.asList( p4,p3,p5,p6 ))
            )),
            new Storey(0, 0, Arrays.asList(
                new Room(0, "floor", 2, Arrays.asList( p11,p12,p13,p14 )),
                new Room(1, "floor", 2, Arrays.asList( p14,p13,p15,p16 ))
            ))
        ));
    }

    @Override
    public boolean write(OutputStream outputStream) throws SerializerException, BimserverDatabaseException {
        this.writeToOutputStream(outputStream, null);
        return true;
    }
}
