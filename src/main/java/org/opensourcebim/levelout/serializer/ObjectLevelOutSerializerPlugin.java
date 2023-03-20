package org.opensourcebim.levelout.serializer;

import org.bimserver.emf.Schema;
import org.bimserver.plugins.PluginConfiguration;
import org.bimserver.plugins.serializers.Serializer;

public class ObjectLevelOutSerializerPlugin extends AbstractLevelOutSerializerPlugin {
	@Override
	public String getDefaultExtension() {
		return "obj";
	}

	@Override
	public String getDefaultContentType() {
		return "application/octet-stream";
	}

	@Override
	public Serializer createSerializer(PluginConfiguration plugin) {
		return new ObjectLevelOutSerializer(getIfcExtractionMethod(plugin));
	}

	@Override
	public String getOutputFormat(Schema schema) {
		return "BIN";
	}
}
