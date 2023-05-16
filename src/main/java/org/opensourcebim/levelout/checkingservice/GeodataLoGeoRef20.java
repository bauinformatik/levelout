package org.opensourcebim.levelout.checkingservice;

import java.util.Arrays;
import java.util.List;

import org.bimserver.emf.IfcModelInterface;
import org.bimserver.models.ifc4.IfcSite;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public class GeodataLoGeoRef20 {
	
	public void validateGeodataLoGeoRef20(StringBuilder txt, IfcModelInterface model) {
		//Validates the presence of the respective IFC entities and IFC select types for the investigation
		IfcSite sites = model.getAll(IfcSite.class).stream().findFirst().orElse(null);
		 if (sites != null) {
			 validateIfcSite(txt, sites);
		 } else {
             txt.append("The IFC entity IfcSite is missing from the IFC model.\n");
         }
	}
	
	//Validates the IFC attribute values of a given IFC entity
	private void validateAttributes(StringBuilder txt, EClass eClass, EList<EStructuralFeature> eFeatures, List<String> validValues, EObject targetObject) {
		//Check1 for IFC attribute values of value != null
		boolean hasValue = eFeatures.stream()
				.filter(feature -> validValues.contains(feature.getName()))
	            .map(feature -> targetObject.eGet(feature))
	            .allMatch(value -> value != null);
		//Check2 for validity of geodata saved in IFC attributes
		if (hasValue) {
			//Valid statement
	        txt.append("The required geodata from ").append(eClass.getName()).append(" are valid in the IFC model.\n");
	    } else {
	    	//Invalid statement
	    	txt.append("The required geodata from ").append(eClass.getName()).append(" are invalid in the IFC model.\n");
	    	txt.append("\n").append("The following geodata must be added:\n");
	    	//Check3 for the necessity of adding IFC attribute values
	    	eFeatures.stream()
	    		.filter(feature -> targetObject.eGet(feature) == null)
                .forEach(feature -> missingAttributeMessage(txt, eClass.getName(), feature.getName()));
	    }
	}	

	//Writes a message for the user in case of missing IFC attribute values in the considered submodel
	private void missingAttributeMessage(StringBuilder txt, String className, String attributeName) {
		txt.append("\t").append(attributeName).append(" is missing in ").append(className).append("\n");
	}
	
	//Checks a respective set of attributes of IfcMapConversion for their presence
	private void validateIfcSite(StringBuilder txt, IfcSite sites) {
		EClass eClass = sites.eClass();
		EList<EStructuralFeature> eFeatures = eClass.getEAllStructuralFeatures();
        //Text output of the respective IFC entity name
        
		/*txt.append("\n").append("The attributes of " + eClass.getName().toString() + " are: \n");
        for (EStructuralFeature eFeature : eFeatures) {
        	String featureName = eFeature.getName();
            Object featureValue = sites.eGet(eFeature);
            txt.append("\t").append(featureName).append(": ").append(featureValue).append("\n");
        }
        txt.append("\n");
        */
        List<String> validValues = Arrays.asList("RefLatitude", "RefLongitude", "RefElevation");
        validateAttributes(txt, eClass, eFeatures, validValues, sites);
	}
}
