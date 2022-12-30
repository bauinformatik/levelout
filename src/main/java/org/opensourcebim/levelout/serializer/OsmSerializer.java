package org.opensourcebim.levelout.serializer;

import org.bimserver.plugins.serializers.ProgressReporter;
import org.bimserver.plugins.serializers.SerializerException;
import org.opensourcebim.levelout.builders.OsmBuilder;

import java.io.OutputStream;

public class OsmSerializer extends AbstractLevelOutSerializer {
    @Override
    public void writeToOutputStream(OutputStream outputStream, ProgressReporter progressReporter) throws SerializerException {
        try {
            new OsmBuilder().createOsmBuilding(outputStream, building);
        } catch (Exception e) {
            throw new SerializerException(e);
        }
    }
}
