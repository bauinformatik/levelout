package org.opensourcebim.levelout.checkingservice;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.List;

public class Validation {
	
	//Validates the IFC attribute values of a given IFC entity
	protected void validateAttributes(StringBuilder txt, List<String> requiredAttributes, EObject targetObject) {
		//Check1 for IFC attribute values of value != null
		EClass eClass = targetObject.eClass();
		EList<EStructuralFeature> eFeatures = eClass.getEAllStructuralFeatures();
		boolean hasValue = eFeatures.stream()
				.filter(feature -> requiredAttributes.contains(feature.getName()))
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

	private void printAttributes(StringBuilder txt, EObject object) {
		EClass eClass = object.eClass();
		txt.append("\n").append("The attributes of " + eClass.getName().toString() + " are: \n");
		for (EStructuralFeature eFeature : eClass.getEAllStructuralFeatures()) {
			String featureName = eFeature.getName();
			Object featureValue = object.eGet(eFeature);
			txt.append("\t").append(featureName).append(": ").append(featureValue).append("\n");
		}
		txt.append("\n");
	}
}
