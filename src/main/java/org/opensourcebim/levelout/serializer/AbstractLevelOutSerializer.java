package org.opensourcebim.levelout.serializer;

import org.bimserver.BimserverDatabaseException;
import org.bimserver.emf.IfcModelInterface;
import org.bimserver.models.ifc4.*;
import org.bimserver.plugins.serializers.ProjectInfo;
import org.bimserver.plugins.serializers.Serializer;
import org.bimserver.plugins.serializers.SerializerException;
import org.opensourcebim.levelout.intermediatemodel.Storey;
import org.opensourcebim.levelout.intermediatemodel.Building;
import org.opensourcebim.levelout.intermediatemodel.Corner;
import org.opensourcebim.levelout.intermediatemodel.Room;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class AbstractLevelOutSerializer implements Serializer {
    boolean extractionMethod = false;
    Building building;

    AbstractLevelOutSerializer(boolean extractionMethod){
        this.extractionMethod = extractionMethod;
    }
    @Override
    public void init(IfcModelInterface ifcModelInterface, ProjectInfo projectInfo, boolean b) throws SerializerException {
        if(extractionMethod) initStructure(ifcModelInterface); else initSample(); // TODO: depending on serializer settings use other init function
    }
    private void initSample(){
        Corner p1 = new Corner(0,0,0.,0);
        Corner p2 = new Corner(1, 0, 10, 0);
        Corner p3 = new Corner(2, 10, 10, 0);
        Corner p4 = new Corner(3, 10,0,0);
        Corner p5 = new Corner(4, 15, 10, 0);
        Corner p6 = new Corner(5, 15, 0, 0);
        Corner p11 = new Corner(0,0,0,3);
        Corner p12 = new Corner(1, 0, 10, 3);
        Corner p13 = new Corner(2, 10, 10, 3);
        Corner p14 = new Corner(3, 10,0,3);
        Corner p15 = new Corner(4, 15, 10, 3);
        Corner p16 = new Corner(5, 15, 0, 3);
        building = new Building(1, Arrays.asList(
            new Storey(0, Arrays.asList(
                    new Room(2, "floor", Arrays.asList( p1,p2,p3,p4 )),
                    new Room(3, "floor", Arrays.asList( p4,p3,p5,p6 ))
            ), Collections.emptyList()),
            new Storey(0, Arrays.asList(
                new Room(4, "floor", Arrays.asList( p11,p12,p13,p14 )),
                new Room(5, "floor", Arrays.asList( p14,p13,p15,p16 ))
            ), Collections.emptyList())
        ));
    }
    private void initStructure(IfcModelInterface ifcModelInterface){
        List<Storey> loStoreys = new ArrayList<>();
        building = new Building(1, loStoreys);
        List<IfcBuildingStorey> storeys = ifcModelInterface.getAllWithSubTypes(IfcBuildingStorey.class);
        int level = 0; // TODO sort by elevation, 0 is closest elevation to 0, from there increment up and down
        int roomId = 1;
        for(IfcBuildingStorey storey: storeys){
            List<Room> rooms = new ArrayList<>();
            Storey loStorey = new Storey(level++, rooms, Collections.emptyList());
            loStoreys.add(loStorey);
            storey.getElevation(); // TODO: use for sorting and in intermediate model
            storey.getName(); /* TODO: use in intermediate model (OSM has "name" tag, but also "level:ref" for short keys) */for (IfcRelAggregates contained: storey.getIsDecomposedBy()){
                for (IfcSpace space : contained.getRelatedObjects().stream().filter( IfcSpace.class::isInstance ).map(IfcSpace.class::cast).toArray(IfcSpace[]::new)) {
                    Room room = new Room(roomId++, "floor", Collections.emptyList());
                    rooms.add(room);
                    space.getName(); // TODO: use in intermediate model
                }
            }
        }
    }
    @Override
    public boolean write(OutputStream outputStream) throws SerializerException, BimserverDatabaseException {
        this.writeToOutputStream(outputStream, null);
        return true;
    }
}
