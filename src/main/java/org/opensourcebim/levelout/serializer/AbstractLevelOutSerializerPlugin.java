package org.opensourcebim.levelout.serializer;

import org.bimserver.emf.Schema;
import org.bimserver.plugins.PluginConfiguration;
import org.bimserver.plugins.PluginContext;
import org.bimserver.plugins.serializers.AbstractSerializerPlugin;
import org.bimserver.shared.exceptions.PluginException;

import java.util.Set;

public abstract class AbstractLevelOutSerializerPlugin extends AbstractSerializerPlugin {

    @Override
    public Set<Schema> getSupportedSchemas() {
        return Schema.IFC4.toSet();
    }

    @Override
    public void init(PluginContext pluginContext, PluginConfiguration pluginConfiguration) throws PluginException {

    }
}
