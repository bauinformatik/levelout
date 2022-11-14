package org.opensourcebim.levelout.intermediatemodel;

import java.io.FileOutputStream;
import java.io.OutputStream;

import de.topobyte.osm4j.core.access.OsmOutputStream;
import de.topobyte.osm4j.xml.output.OsmXmlOutputStream;

public class Output {

	private String fileName;
	
	public Output(String fileName) {
		super();
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
}
 