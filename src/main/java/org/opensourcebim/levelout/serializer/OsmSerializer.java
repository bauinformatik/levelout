package org.opensourcebim.levelout.serializer;

import org.bimserver.plugins.serializers.ProgressReporter;
import org.bimserver.plugins.serializers.SerializerException;
import org.opensourcebim.levelout.builders.OsmBuilder;

import java.io.OutputStream;

public class OsmSerializer extends AbstractLevelOutSerializer {
    OsmSerializer(boolean extractionMethod) {
        super(extractionMethod);
    }

    @Override
    public void writeToOutputStream(OutputStream outputStream, ProgressReporter progressReporter) throws SerializerException {
        try {
            new OsmBuilder().createAndWriteBuilding(building, 4, outputStream);
        } catch (Exception e) {
            throw new SerializerException(e);
        }
    }
}
