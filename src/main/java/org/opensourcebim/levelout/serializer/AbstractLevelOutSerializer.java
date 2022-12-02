package org.opensourcebim.levelout.serializer;

import org.bimserver.BimserverDatabaseException;
import org.bimserver.emf.IfcModelInterface;
import org.bimserver.plugins.serializers.ProjectInfo;
import org.bimserver.plugins.serializers.Serializer;
import org.bimserver.plugins.serializers.SerializerException;
import org.opensourcebim.levelout.intermediatemodel.FootPrint;
import org.opensourcebim.levelout.intermediatemodel.GenericBuilding;
import org.opensourcebim.levelout.intermediatemodel.GenericNode;
import org.opensourcebim.levelout.intermediatemodel.GenericPolygon;

import java.io.OutputStream;
import java.util.Arrays;

public abstract class AbstractLevelOutSerializer implements Serializer {
    GenericBuilding building;

    @Override
    public void init(IfcModelInterface ifcModelInterface, ProjectInfo projectInfo, boolean b) throws SerializerException {
        // TODO map IFC to intermediate model
        GenericNode p1 = new GenericNode(0,0,0.,0);
        GenericNode p2 = new GenericNode(1, 0, 10, 0);
        GenericNode p3 = new GenericNode(2, 10, 10, 0);
        GenericNode p4 = new GenericNode(3, 10,0,0);
        GenericNode p5 = new GenericNode(4, 15, 10, 0);
        GenericNode p6 = new GenericNode(5, 15, 0, 0);
        GenericNode p11 = new GenericNode(0,0,0.,3);
        GenericNode p12 = new GenericNode(1, 0, 10, 3);
        GenericNode p13 = new GenericNode(2, 10, 10, 3);
        GenericNode p14 = new GenericNode(3, 10,0,3);
        GenericNode p15 = new GenericNode(4, 15, 10, 3);
        GenericNode p16 = new GenericNode(5, 15, 0, 3);
        building = new GenericBuilding(Arrays.asList(
            new FootPrint(0, 0, Arrays.asList(
                    new GenericPolygon(0, "floor", 2, Arrays.asList( p1,p2,p3,p4 )),
                    new GenericPolygon(1, "floor", 2, Arrays.asList( p4,p3,p5,p6 ))
            )),
            new FootPrint(0, 0, Arrays.asList(
                new GenericPolygon(0, "floor", 2, Arrays.asList( p11,p12,p13,p14 )),
                new GenericPolygon(1, "floor", 2, Arrays.asList( p14,p13,p15,p16 ))
            ))
        ));
    }

    @Override
    public boolean write(OutputStream outputStream) throws SerializerException, BimserverDatabaseException {
        this.writeToOutputStream(outputStream, null);
        return true;
    }
}
