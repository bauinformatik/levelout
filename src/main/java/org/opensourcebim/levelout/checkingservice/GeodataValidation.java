package org.opensourcebim.levelout.checkingservice;

import org.bimserver.emf.IfcModelInterface;
import org.bimserver.models.ifc4.IfcMapConversion;
import org.bimserver.models.ifc4.IfcProject;
import org.bimserver.models.ifc4.IfcProjectedCRS;
import org.bimserver.models.ifc4.IfcSite;

public class GeodataValidation {

	// Declaration and initialization of an instance variable named geoLoGeoRef50 of type GeodataLoGeoRef50
	private GeodataLoGeoRef50 geoLoGeoRef50 = new GeodataLoGeoRef50();
	private GeodataLoGeoRef20 geoLoGeoRef20 = new GeodataLoGeoRef20();
	
	public void validateGeodata(StringBuilder txt, IfcModelInterface model) {
		txt.append("Geodata Validation\n-------------------\n");
		//Validates the presence of the respective IFC entities and select types for LoGeoRef50
		IfcProject projects = model.getAll(IfcProject.class).stream().findAny().orElse(null);
		IfcMapConversion mapConversions = model.getAll(IfcMapConversion.class).stream().findAny().orElse(null);
		IfcProjectedCRS projectedCRSs = model.getAll(IfcProjectedCRS.class).stream().findAny().orElse(null);
		/*EList<IfcRepresentationContext> representationContexts = projects.getRepresentationContexts();
		IfcGeometricRepresentationContext geometricRepresentationContexts = representationContexts.stream()
				.filter(feature -> feature instanceof IfcGeometricRepresentationContext && "Model".equals(feature.getContextType()))
				.map(feature -> (IfcGeometricRepresentationContext) feature)
				.findFirst()
				.orElse(null);*/
		if (projects != null && (mapConversions != null || projectedCRSs != null)) {
        	txt.append("A geodata analysis for LoGeoRef50 is conducted." + "\n");
        	txt.append("Check for the presence of the following IFC enitities and their attributes:\n" + "\tIfcProject,\n" + "\tIfcMapConversion,\n" + "\tIfcProjectedCRS,\n" + "\tIfcGeometricRepresentationContext\n\n"); 
        	//The validateGeodataLoGeoRef50 method is called on the geoLoGeoRef50 object with the given txt and model parameters.
        	geoLoGeoRef50.validateGeodataLoGeoRef50(txt, model);
    	} else {
    		//Validates the presence of the respective IFC entities and select types for LoGeoRef20
    		IfcSite sites = model.getAll(IfcSite.class).stream().findAny().orElse(null);
    		if (sites != null) {
        		txt.append("A geodata analysis for LoGeoRef20 is conducted." + "\n");
        		txt.append("Check for the presence of the following IFC enitities and their attributes:\n" + "\tIfcSite\n" + "\n\n");
            	//The validateGeodataLoGeoRef20 method is called on the geoLoGeoRef20 object with the given txt and model parameters.
                geoLoGeoRef20.validateGeodataLoGeoRef20(txt, model);
    		} else {
    			txt.append("A geodata analysis for LoGeoRef50 or LoGeoRef20 is impossible.");
    		}
        }
	}
}