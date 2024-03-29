package org.opensourcebim.levelout.serializer;

import org.bimserver.plugins.serializers.ProgressReporter;
import org.bimserver.plugins.serializers.SerializerException;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class ObjectLevelOutSerializer extends AbstractLevelOutSerializer {

	ObjectLevelOutSerializer(AbstractLevelOutSerializerPlugin.Options options) {
		super(options);
	}

	@Override
	public void writeToOutputStream(OutputStream outputStream, ProgressReporter progressReporter) throws SerializerException {
		try {
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
			objectOutputStream.writeObject(building);
			objectOutputStream.flush();
		} catch (IOException e) {
			throw new SerializerException(e);
		}
	}
}
