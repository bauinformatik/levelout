package org.opensourcebim.levelout.checkingservice;

import java.util.Arrays;
import java.util.List;

import org.bimserver.emf.IfcModelInterface;
import org.bimserver.models.ifc4.IfcCoordinateReferenceSystem;
import org.bimserver.models.ifc4.IfcCoordinateReferenceSystemSelect;
import org.bimserver.models.ifc4.IfcMapConversion;
import org.bimserver.models.ifc4.IfcProject;
import org.bimserver.models.ifc4.IfcRepresentationContext;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;


public class GeodataValidation {

	public void validateGeodata(StringBuilder txt, IfcModelInterface model) {
		txt.append("Geodata validation\n----------------\n");
		//Validates the presence of the respective IFC entities and select types for the investigation
		 IfcProject projects = model.getAll(IfcProject.class).stream().findAny().orElse(null);
         if (projects != null) {
            IfcMapConversion mapConversions = model.getAll(IfcMapConversion.class).stream().findAny().orElse(null);
	        if (mapConversions != null) {
	            validateIfcMapConversion(txt, mapConversions);
	            IfcCoordinateReferenceSystemSelect coordinateReferenceSystemSelects = mapConversions.getSourceCRS();
	            if (coordinateReferenceSystemSelects != null) {
	                validateIfcCoordinateReferenceSystemSelect(txt, coordinateReferenceSystemSelects);
	            } else {
	                txt.append("The IFC entity IfcGeometricRepresentationContext is missing from the IFC model.\n");
	            }
	            IfcCoordinateReferenceSystem coordinateReferenceSystems = mapConversions.getTargetCRS();
	            if (coordinateReferenceSystems != null) {
	                validateIfcCoordinateReferenceSystem(txt, coordinateReferenceSystems);
	            } else {
	                txt.append("The IFC entity IfcProjectedCRS is missing from the IFC model.\n");
	            }
            validateIfcContext(txt, model, projects, mapConversions, coordinateReferenceSystemSelects);
	        } else {
	            txt.append("The IfcMapConversion entity is missing from the IFC model.\n");
	        }
         } else {
             txt.append("The IFC entity IfcProject is missing from the IFC model.\n");
         }
    }
	
	//Validates the IFC attribute values of a given IFC entity
	private void validateAttributes(StringBuilder txt, EClass eClass, EList<EStructuralFeature> eFeatures, List<String> validValues, EObject targetObject) {
		//Check1 for IFC attribute values of value != null
		//Iteration process with stream over the list eFeatures
		boolean hasValue = eFeatures.stream()
				//Filters the validValues list for all IFC attributes with a name in this list
				.filter(feature -> validValues.contains(feature.getName()))
	            //Extracts the value of each IFC attribute from targetObject
				.map(feature -> targetObject.eGet(feature))
	            //Checks that value of all IFC attributes is not null
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
	    	//Iteration process with stream over the list eFeatures
	    	eFeatures.stream()
	    		//Filters the targetObjekt list for all IFC attribute values that are null
                .filter(feature -> targetObject.eGet(feature) == null)
                //Calls the method missingAttributeMessage for all IFC attribute values that are null
                .forEach(feature -> missingAttributeMessage(txt, eClass.getName(), feature.getName()));
	    }
	}
	
	//Writes a message for the user in case of missing IFC attribute values in the considered submodel
	private void missingAttributeMessage(StringBuilder txt, String className, String attributeName) {
		txt.append("\t").append(attributeName).append(" is missing in ").append(className).append("\n");
	}
	
	//Checks the relationship between IfcContext and IfcGeometricRepresentationContext
	private void validateIfcContext(StringBuilder txt, IfcModelInterface model, IfcProject projects, IfcMapConversion mapConversions, IfcCoordinateReferenceSystemSelect coordinateReferenceSystemSelects) {
	    //Creates the list representationContexts with all values of the attribute RepresentationContexts from IfcProject
		List<IfcRepresentationContext> representationContexts = projects.getRepresentationContexts();
	    //Iteration process with stream over the list representationContexts
	    boolean hasValue = representationContexts.stream()
	    		//Checks the matching case with the variable coordinateReferencSystemSelect
	    		.anyMatch(coordinateReferenceSystemSelects::equals);
	    if (hasValue) {
	        //Valid statement
	    	txt.append("\n").append("IfcContext has a relationship to IfcGeometricRepresentationContext\n");
	    } else {
	    	//Invalid statement
	        txt.append("\n").append("The relationship between IfcContext and IfcGeometricRepresentationContext is missing\n");
	    }
	}
	
	//Checks a respective set of attributes of IfcMapConversion for their presence
	private void validateIfcMapConversion(StringBuilder txt, IfcMapConversion mapConversions) {
		//Executes the eClass() method on mapconversions, the result is assigned to the variable eClass
		EClass eClass = mapConversions.eClass();
		//Creates the list eFeatures with all  attribute information from IfcMapConversion
        EList<EStructuralFeature> eFeatures = eClass.getEAllStructuralFeatures();
        //Text output of the respective IFC entity name
        txt.append("\n").append("The attributes of " + eClass.getName().toString() + " are: \n");
        for (EStructuralFeature eFeature : eFeatures) {
            //Storing of all attribute names from IfcMapConversion
        	String featureName = eFeature.getName();
        	//Storing of all attribute values from IfcMapConversion
            Object featureValue = mapConversions.eGet(eFeature);
            //Text output of all attribute names and assignment of their attribute values
            txt.append("\t").append(featureName).append(": ").append(featureValue).append("\n");
        }
        txt.append("\n");
        //Saves the attributes to be checked in list validValues with attributes necessary for geolocation.
        List<String> validValues = Arrays.asList("SourceCRS", "TargetCRS", "Eastings","Northings", "OrthogonalHeight", "XAxisAbscissa", "XAxisOrdinate", "Scale");
        //Executes the method validateAttributes explained above
        validateAttributes(txt, eClass, eFeatures, validValues, mapConversions);
    }
	
	//Checks a respective set of attributes of IfcCoordinateReferenceSystemSelect for their presence
	private void validateIfcCoordinateReferenceSystemSelect(StringBuilder txt, IfcCoordinateReferenceSystemSelect coordinateReferenceSystemSelects) {
		//Executes the eClass() method on coordinateReferenceSystemSelects, the result is assigned to the variable eClass
		EClass eClass = coordinateReferenceSystemSelects.eClass();
		//Creates the list eFeatures with all attribute information from IfcCoordinateReferenceSystemSelect
		EList<EStructuralFeature> eFeatures = eClass.getEAllStructuralFeatures();
		//Text output of the respective IFC entity name
        txt.append("\n").append("The attributes of " + eClass.getName().toString() + " are: \n");
        for (EStructuralFeature eFeature : eFeatures) {
        	//Storing of all attribute names from IfcCoordinateReferenceSystemSelect
        	String featureName = eFeature.getName();
        	//Storing of all attribute values from IfcCoordinateReferenceSystemSelect
        	Object featureValue = coordinateReferenceSystemSelects.eGet(eFeature);
        	//Text output of all attribute names and assignment of their attribute values
        	txt.append("\t").append(featureName).append(": ").append(featureValue).append("\n");
		}
		txt.append("\n");
		//Saves the attributes to be checked in list validValues with attributes necessary for geolocation.
		List<String> validValues = Arrays.asList("WorldCoordinateSystem", "TrueNorth", "HasCoordinateOperation");
		//Executes the method validateAttributes explained above
        validateAttributes(txt, eClass, eFeatures, validValues, coordinateReferenceSystemSelects);
	}   
	    
	//Checks a respective set of attributes of IfcCoordinateReferenceSystem for their presence
	private void validateIfcCoordinateReferenceSystem(StringBuilder txt, IfcCoordinateReferenceSystem coordinateReferenceSystems) {
		//Executes the eClass() method on coordinateReferenceSystems, the result is assigned to the variable eClass
		EClass eClass = coordinateReferenceSystems.eClass();
		//Creates the list eFeatures with all attribute information from IfcCoordinateReferenceSystem
		EList<EStructuralFeature> eFeatures = eClass.getEAllStructuralFeatures();
		//Text output of the respective IFC entity name
		txt.append("\n").append("The attributes of " + eClass.getName().toString() + " are: \n");
        for (EStructuralFeature eFeature : eFeatures) {
        	//Storing of all attribute names from IfcCoordinateReferenceSystem
        	String featureName = eFeature.getName();
        	//Storing of all attribute values from IfcCoordinateReferenceSystem
        	Object featureValue = coordinateReferenceSystems.eGet(eFeature);
        	//Text output of all attribute names and assignment of their attribute values
        	txt.append("\t").append(featureName).append(": ").append(featureValue).append("\n");
        }
        txt.append("\n");
        //Saves the attributes to be checked in list validValues with attributes necessary for geolocation.
        List<String> validValues = Arrays.asList("GeodeticDatum", "VerticalDatum", "HasCoordinateOperation", "MapProjection", "MapZone", "MapUnit");
        //Executes the method validateAttributes explained above
        validateAttributes(txt, eClass, eFeatures, validValues, coordinateReferenceSystems);
	}
}

