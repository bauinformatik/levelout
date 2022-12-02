package org.opensourcebim.levelout.serializer;

import org.bimserver.emf.Schema;
import org.bimserver.plugins.PluginConfiguration;
import org.bimserver.plugins.serializers.Serializer;

public class OsmSerializerPlugin extends AbstractLevelOutSerializerPlugin {
	@Override
	public String getDefaultExtension() {
		return "osm";
	}

	@Override
	public String getDefaultContentType() {
		return "application/osm+xml";
	}

	@Override
	public Serializer createSerializer(PluginConfiguration pluginConfiguration) {
		return new OsmSerializer();
	}

	@Override
	public String getOutputFormat(Schema schema) {
		return "XML";
	}
}
