package org.opensourcebim.levelout.serializer;

import org.bimserver.plugins.serializers.ProgressReporter;
import org.bimserver.plugins.serializers.SerializerException;
import org.opensourcebim.levelout.builders.CityGmlBuilder;

import java.io.OutputStream;

public class CityGmlSerializer extends AbstractLevelOutSerializer {
    CityGmlSerializer(AbstractLevelOutSerializerPlugin.Options options) {
        super(options);
    }

    @Override
    public void writeToOutputStream(OutputStream outputStream, ProgressReporter progressReporter) throws SerializerException {
        try {
            new CityGmlBuilder().createAndWriteBuilding(building, outputStream);
        } catch (Exception e) {
            throw new SerializerException(e);
        }
    }
}
