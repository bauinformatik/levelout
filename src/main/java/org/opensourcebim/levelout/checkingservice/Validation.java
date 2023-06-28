package org.opensourcebim.levelout.checkingservice;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.List;
import java.util.stream.Collectors;

public abstract class Validation {

	StringBuilder txt;
	
	//Validates the IFC attribute values of a given IFC entity
	protected void validateAttributes(List<String> requiredAttributes, EObject targetObject) {
		//Check1 for IFC attribute values of value != null
		EClass eClass = targetObject.eClass();
		EList<EStructuralFeature> eFeatures = eClass.getEAllStructuralFeatures();
		List<EStructuralFeature> missingRequiredAttributes = eFeatures.stream()
				.filter(feature -> requiredAttributes.contains(feature.getName()) && targetObject.eGet(feature)==null)
				.collect(Collectors.toList());
		if (missingRequiredAttributes.isEmpty()) {
	        txt.append("\t\tThe required attributes for ").append(eClass.getName()).append(" are all set.\n");
	    } else {
	    	txt.append("\t\tThe required attributes for ").append(eClass.getName()).append(" are not all set.\n");
	    	txt.append("\t\tThe following attributes must be added:\n");
            missingRequiredAttributes.forEach(feature -> missingAttributeMessage(eClass.getName(), feature.getName()));
	    }
	}
	
	//Writes a message for the user in case of missing IFC attribute values in the considered submodel
	private void missingAttributeMessage(String className, String attributeName) {
		txt.append("\t").append(attributeName).append(" is missing in ").append(className).append("\n");
	}

	protected void printAttributes(EObject object) {
		EClass eClass = object.eClass();
		txt.append("\n").append("The attributes of " + eClass.getName() + " are: \n");
		for (EStructuralFeature eFeature : eClass.getEAllStructuralFeatures()) {
			String featureName = eFeature.getName();
			Object featureValue = object.eGet(eFeature);
			txt.append("\t").append(featureName).append(": ").append(featureValue).append("\n");
		}
		txt.append("\n");
	}
}
