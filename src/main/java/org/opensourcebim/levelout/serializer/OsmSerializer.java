package org.opensourcebim.levelout.serializer;

import org.bimserver.plugins.serializers.ProgressReporter;
import org.bimserver.plugins.serializers.SerializerException;
import org.opensourcebim.levelout.builders.OsmBuilder;
import org.opensourcebim.levelout.util.CoordinateConversion.CoordinateReference;
import org.opensourcebim.levelout.util.CoordinateConversion.GeodeticOriginCRS;
import org.opensourcebim.levelout.util.CoordinateConversion.GeodeticPoint;

import java.io.OutputStream;

public class OsmSerializer extends AbstractLevelOutSerializer {
    OsmSerializer(boolean extractionMethod) {
        super(extractionMethod);
    }

    @Override
    public void writeToOutputStream(OutputStream outputStream, ProgressReporter progressReporter) throws SerializerException {
        try {
            CoordinateReference crs = new GeodeticOriginCRS(new GeodeticPoint(53.320555, -1.729000, 0), -0.13918031137);
            // TODO populate earlier and correctly for sample and IFC input
            new OsmBuilder().createAndWriteBuilding(building, crs, outputStream);
        } catch (Exception e) {
            throw new SerializerException(e);
        }
    }
}
