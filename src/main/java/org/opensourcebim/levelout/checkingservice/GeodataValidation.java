package org.opensourcebim.levelout.checkingservice;

import org.bimserver.emf.IfcModelInterface;

public class GeodataValidation {

	public void validateGeodata(StringBuilder txt, IfcModelInterface model) {
		txt.append("Geodata Validation\n-------------------\n");
		new GeodataLoGeoRef().validateGeodataLoGeoRef(txt, model);
	    new GeodataLoGeoRef50().validateGeodataLoGeoRef50(txt, model);
	    new GeodataLoGeoRef20_40().validateGeodataLoGeoRef20_40(txt, model);
	}
}