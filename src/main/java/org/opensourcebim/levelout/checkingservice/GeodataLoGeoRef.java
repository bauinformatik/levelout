package org.opensourcebim.levelout.checkingservice;

import org.bimserver.emf.IfcModelInterface;
import org.bimserver.models.ifc4.*;
import org.bimserver.utils.IfcUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.List;
import java.util.Optional;

public class GeodataLoGeoRef {
	
    protected void validateUnits(StringBuilder txt, IfcModelInterface model) {
    	IfcProject project = model.getAll(IfcProject.class).stream().findAny().orElse(null);
    	if (project != null) {
    		if (project.getUnitsInContext() != null || project.getUnitsInContext().getUnits() != null) {
    			Optional<IfcUnit> unit = project.getUnitsInContext().getUnits().stream()
    					.filter(u -> u instanceof IfcNamedUnit && ((IfcNamedUnit) u).getUnitType() == IfcUnitEnum.LENGTHUNIT)
    					.findAny();
                if (unit.isEmpty()) {
                    // no length unit found
                	txt.append("The legnth unit is undefined." + "\n");
                } else {
                    IfcSIUnit ifcSIUnit = (IfcSIUnit) unit.get();
                    IfcSIUnitName ifcSIUnitName = ifcSIUnit.getName();  // METRE?
                    if (ifcSIUnitName.toString() == "METRE") {
                    	IfcSIPrefix prefix = ifcSIUnit.getPrefix();
                    	if (prefix.toString() == "NULL") {
                    		txt.append("The prefix is undefined. The fullname of the length unit is: " + ifcSIUnitName.getName().toString() + "\n");
                    	} else {
                    		String fullUnit = prefix.getName()  +  ifcSIUnitName.getName();// TODO report
                    		txt.append("The fullname of the unit is: " + fullUnit + "\n");
                    	}
                    	float factor = IfcUtils.getLengthUnitPrefixMm(ifcSIUnit.getPrefix().getName());
                		txt.append("The prefix factor is: " + factor + "\n");
                    } else {
                    	txt.append("The SI unit is not METRE, but " + ifcSIUnitName + "\n");
                    }
                }
            }
    	} else {
    		txt.append("The project is missing");
    	}
    }
    
	//Validates the IFC attribute values of a given IFC entity
	protected void validateAttributes(StringBuilder txt, EClass eClass, EList<EStructuralFeature> eFeatures, List<String> validValues, EObject targetObject) {
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

    
}
