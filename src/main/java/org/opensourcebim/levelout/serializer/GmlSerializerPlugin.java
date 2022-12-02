package org.opensourcebim.levelout.serializer;

import org.bimserver.emf.Schema;

public abstract class GmlSerializerPlugin extends AbstractLevelOutSerializerPlugin {
	@Override
	public String getDefaultExtension() {
		return "gml";
	}

	@Override
	public String getDefaultContentType() {
		return "application/gml+xml";
	}

	@Override
	public String getOutputFormat(Schema schema) {
		return "XML";
	}
}
