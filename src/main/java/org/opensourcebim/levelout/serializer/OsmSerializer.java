package org.opensourcebim.levelout.serializer;

import org.bimserver.plugins.serializers.ProgressReporter;
import org.bimserver.plugins.serializers.SerializerException;
import org.opensourcebim.levelout.builders.OsmBuilder;

import java.io.OutputStream;

public class OsmSerializer extends AbstractLevelOutSerializer {
    private final boolean convertEpsg;
    OsmSerializer(AbstractLevelOutSerializerPlugin.Options options, boolean convertEpsg) {
        super(options);
        this.convertEpsg = convertEpsg;
    }

    @Override
    public void writeToOutputStream(OutputStream outputStream, ProgressReporter progressReporter) throws SerializerException {
        try {
            new OsmBuilder(convertEpsg).createAndWriteBuilding(building, outputStream);
        } catch (Exception e) {
            throw new SerializerException(e);
        }
    }
}
