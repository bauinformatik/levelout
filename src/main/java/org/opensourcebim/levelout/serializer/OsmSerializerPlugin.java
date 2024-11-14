package org.opensourcebim.levelout.serializer;

import org.bimserver.emf.Schema;
import org.bimserver.models.store.ObjectDefinition;
import org.bimserver.models.store.ParameterDefinition;
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
		return new OsmSerializer(super.getOptions(pluginConfiguration), pluginConfiguration.getBoolean("ConvertEpsg"));
	}

	@Override
	public ObjectDefinition getUserSettingsDefinition() {
		ObjectDefinition settings = super.getUserSettingsDefinition();
		ParameterDefinition convertEpsG = createBooleanParameter(
				"ConvertEpsg",
				"Convert with EPSG algorithm",
				"Choose whether to follow to EPSG algorithm according to EPSG method 9837 (https://epsg.io/9837-method)",
				false
		);
		settings.getParameters().add(convertEpsG);
		return settings;
	}

	@Override
	public String getOutputFormat(Schema schema) {
		return "XML";
	}
}
