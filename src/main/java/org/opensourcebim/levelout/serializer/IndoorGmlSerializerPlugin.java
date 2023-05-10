package org.opensourcebim.levelout.serializer;

import org.bimserver.plugins.PluginConfiguration;
import org.bimserver.plugins.serializers.Serializer;

public class IndoorGmlSerializerPlugin extends GmlSerializerPlugin {

    @Override
    public Serializer createSerializer(PluginConfiguration pluginConfiguration) {
        return new IndoorGmlSerializer(getOptions(pluginConfiguration));
    }

}
