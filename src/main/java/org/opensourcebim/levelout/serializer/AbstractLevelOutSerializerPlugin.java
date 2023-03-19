package org.opensourcebim.levelout.serializer;

import org.bimserver.emf.Schema;
import org.bimserver.models.store.*;
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

    protected boolean getIfcExtractionMethod(PluginConfiguration pluginConfiguration){
        return pluginConfiguration.getBoolean("IfcExtractionMethod");
    }

    @Override
    public ObjectDefinition getUserSettingsDefinition() {
        ObjectDefinition settings = super.getUserSettingsDefinition();
        ParameterDefinition parameter = StoreFactory.eINSTANCE.createParameterDefinition();
        parameter.setIdentifier("IfcExtractionMethod");
        parameter.setDescription("Choose from various methods how to extract the indoor data from IFC, for now just static sample (unticked) or basic extraction (ticked).");
        parameter.setName("IFC Extraction Method");
        PrimitiveDefinition type = StoreFactory.eINSTANCE.createPrimitiveDefinition();
        type.setType(PrimitiveEnum.BOOLEAN);
        parameter.setType(type);
        BooleanType defaultValue = StoreFactory.eINSTANCE.createBooleanType();
        defaultValue.setValue(true);
        parameter.setDefaultValue(defaultValue);
        settings.getParameters().add(parameter);
        return settings;
    }
}
