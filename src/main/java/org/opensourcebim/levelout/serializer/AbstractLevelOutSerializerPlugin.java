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

	protected Options getOptions(PluginConfiguration pluginConfiguration) {
		Options options = new Options();
		options.extractionMethod = pluginConfiguration.getBoolean("IfcExtractionMethod");
		options.ignoreDeadRooms = pluginConfiguration.getBoolean("IgnoreDeadRooms");
		options.ignoreAbstractElements = pluginConfiguration.getBoolean("IgnoreAbstractElements");
		return options;
	}

	@Override
	public ObjectDefinition getUserSettingsDefinition() {
		ObjectDefinition settings = super.getUserSettingsDefinition();
        ParameterDefinition ifcExtractionMethod = createBooleanParameter(
            "IfcExtractionMethod",
            "IFC Extraction Method",
            "Choose from various methods how to extract the indoor data from IFC, for now just static sample (unticked) or basic extraction (ticked).",
            true
        );
		settings.getParameters().add(ifcExtractionMethod);
        ParameterDefinition emptySpaces = createBooleanParameter(
            "IgnoreAbstractElements",
            "Ignore Abstract Elements",
            "Choose whether elements, e.g. doors and rooms, without geometry are ignored for conversion or not.",
            false
        );
        settings.getParameters().add(emptySpaces);
        ParameterDefinition deadRooms = createBooleanParameter(
            "IgnoreDeadRooms",
            "Ignore Dead Rooms",
            "Choose whether apparently inaccessible spaces/rooms - those without detected connection to doors are created or not. Note that in some models the connection via doors is not detected with the current space-boundary based method.",
            false
        );
		settings.getParameters().add(deadRooms);
		return settings;
	}

	protected static ParameterDefinition createBooleanParameter(String identifier, String name, String description, boolean defaultVal) {
		ParameterDefinition parameter = StoreFactory.eINSTANCE.createParameterDefinition();
		parameter.setIdentifier(identifier);
		parameter.setDescription(description);
		parameter.setName(name);
		PrimitiveDefinition type = StoreFactory.eINSTANCE.createPrimitiveDefinition();
		type.setType(PrimitiveEnum.BOOLEAN);
		parameter.setType(type);
		BooleanType defaultValue = StoreFactory.eINSTANCE.createBooleanType();
		defaultValue.setValue(defaultVal);
		parameter.setDefaultValue(defaultValue);
		return parameter;
	}

	@Override
	public Set<String> getRequiredGeometryFields() {
		return Set.of("vertices", "indices");
	}

    public static class Options {
        public boolean extractionMethod;
        public boolean ignoreAbstractElements;
        public boolean ignoreDeadRooms;
		public boolean convertEpsg;
	}
}
