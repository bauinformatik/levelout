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
import java.util.stream.Collectors;
import java.util.ArrayList;

public class GeodataLoGeoRef {
	
	IfcProject project;
	
	protected void validateGeodataLoGeoRef (StringBuilder txt, IfcModelInterface model) {
		// TODO projects as list in case of multiple projects?
        List <IfcProject> projects = model.getAll(IfcProject.class); // TODO
        if (projects.isEmpty()) {
        	txt.append("\t\t" + "There is no IfcProject entity in the IFC model");
        	return;
        }
        
        project = projects.get(0);
        
        EList<IfcRelAggregates> eListRelAggregates = project.getIsDecomposedBy();
        if (eListRelAggregates == null || eListRelAggregates.isEmpty()) {
        	txt.append("\t\t" + "The DecomposedBy attribute is undefined for the IfcProject entity");
        	return;
        }
        List<IfcObjectDefinition> objectDefinitions = new ArrayList<>();
        for (IfcRelAggregates relAggregate : eListRelAggregates) {
            EList<IfcObjectDefinition> eListObjectDefinition = relAggregate.getRelatedObjects();
            if (eListObjectDefinition == null || eListObjectDefinition.isEmpty()) {
                txt.append("\t\t" + "The RelatedObjects attribute is undefined for the IfcRelAggregates entity");
                continue;
            }
            objectDefinitions.addAll(eListObjectDefinition);
        }
        List<IfcSite> sites = objectDefinitions.stream()
                .filter(feature -> feature instanceof IfcSite)
                .map(feature -> (IfcSite) feature)
                .collect(Collectors.toList());
        if (sites.isEmpty()) {
            txt.append("\t\t" + "There is no IfcSite entity in the IFC model");
            return;
        }
        //code from IfcProject to IfcGeometricRepresentationContext
        
        EList<IfcRepresentationContext> representationContexts = project.getRepresentationContexts();
 		IfcGeometricRepresentationContext geometricRepresentationContext = representationContexts.stream()
 				.filter(feature -> feature instanceof IfcGeometricRepresentationContext && "Model".equals(feature.getContextType()))
 				.map(feature -> (IfcGeometricRepresentationContext) feature)
 				.findFirst()
 				.orElse(null);
 		if (geometricRepresentationContext == null) {
        	txt.append("\t\t" + "The DecomposedBy attribute is undefined for the IfcProject entity");
        	return;
        }
        
        validateSites(sites, txt, model);
        validateUnits(projects, txt, model);
        //validate3DContext();
    }
		
	protected void validateProjects(List<IfcProject> projects, StringBuilder txt, IfcModelInterface model) {
		// TODO This is necessary in case of more than 1 project --> query a list of IfcProject and not one instance
		txt.append("\t" + "Project Validation" + "\n");
		//Count the numbers of project --> should be 1
		long numberOfProjects = model.getAll(IfcProject.class).size(); // TODO Is there any easier way for getting the amount of IfcProject?
		txt.append("\t\t" + "The number of projects is: " + numberOfProjects + "\n");
		if (numberOfProjects > 1) {
			txt.append("\t\t" + "Validation of the IFC model is impossible because there is more than one project.");
		}
	}
	
	protected void validateSites(List<IfcSite> sites, StringBuilder txt, IfcModelInterface model) {
		txt.append("\t" + "Site Validation" + "\n");
		IfcSite site = sites.stream().findAny().orElse(null);// TODO findAny gives a instance from the IfcSite list, orElse(null) is required bcs. Optional leads to problems with methods that are not practicable for this type
    	if (site == null) {
    		txt.append("\t\t" + "A geodata analysis for LoGeoRef50 or LoGeoRef20 is impossible." + "\n");
    	} else {
    		IfcElementCompositionEnum elementCompositionTypeEnum = site.getCompositionType();
			String elementCompositionTypeEnumMsg = "The CompositionType attribute for IfcSite is ";
			if (elementCompositionTypeEnum == null) {
				txt.append("\t\t" + elementCompositionTypeEnumMsg + "undefined." + "\n");
			} else {
				String elementCompositionTypeEnumString = elementCompositionTypeEnum.toString();
				txt.append("\t\t" + elementCompositionTypeEnumMsg + elementCompositionTypeEnumString + "\n");
				switch(elementCompositionTypeEnumString) {
				// TODO How could be dealt with the respective ElementCompositionTypeEnum?
					case "COMPLEX": txt.append("\t\t" + "There is more than one IfcSite entity in the IFC model" + "\n"); break;
					case "ELEMENT": txt.append("\t\t" + "There is one IfcSite entity in the IFC model" + "\n"); break;
					case "PARTIAL": txt.append("\t\t" + "The IfcSite entity is decomposed in parts." + "\n"); break;
				}
			}
		}
	}
	
    protected void validateUnits(List<IfcProject> projects, StringBuilder txt, IfcModelInterface model) {
    	txt.append("\t" +"Unit Validation" + "\n");
		//Unit check
		if (project.getUnitsInContext() != null && project.getUnitsInContext().getUnits() != null) {
			//Length unit check
			Optional<IfcUnit> unit = project.getUnitsInContext().getUnits().stream()
					.filter(namedUnit -> namedUnit instanceof IfcNamedUnit && ((IfcNamedUnit) namedUnit).getUnitType() == IfcUnitEnum.LENGTHUNIT)
					.findAny();
            if (unit.isEmpty()) {
                // no length unit found
            	txt.append("\t\t" + "The length unit is undefined. The deposited unit is unusable" + "\n");
            } else {
            	//SI unit check
            	if (unit.get() instanceof IfcSIUnit) {
            		IfcSIUnit ifcSIUnit = (IfcSIUnit) unit.get();
                    IfcSIUnitName ifcSIUnitName = ifcSIUnit.getName();  // METRE?
            		if (ifcSIUnitName.toString().equals("METRE")) { //Is this if-statement still necessary?
                    	IfcSIPrefix prefix = ifcSIUnit.getPrefix();
                    	if (prefix == null || prefix.toString().equals("NULL")) { //This is necessary bcs. prefix is not null, but NULL
                    		txt.append("\t\t" + "The unit's prefix is undefined. The fullname of the length unit is: " + ifcSIUnitName.getName().toString() + "\n");
                    	} else {
                    		String fullUnit = prefix.getName()  +  ifcSIUnitName.getName();// TODO report
                    		txt.append("\t\t" + "The unit's prefix is defined. The fullname of the length unit is: " + fullUnit + "\n");
                    		float factor = IfcUtils.getLengthUnitPrefixMm(ifcSIUnit.getPrefix().getName());
                    		txt.append("\t\t" + "The unit's prefix factor is: " + factor + "\n");
                    	}
            		} else {
            			txt.append("\t\t" + "There is an SI unit, but the unit is not METRE" + "\n");
            		}
            	} else {
            		//in case of an non-SI length unit
            		String noSIUnitmsg = "The SI unit is not METRE, but ";
                    Object kindOfUnit = unit.get();
                    if (kindOfUnit instanceof IfcContextDependentUnit) {
                        txt.append("\t\t" + noSIUnitmsg + ((IfcContextDependentUnit) kindOfUnit).getName() + "\n");
                    } else if (kindOfUnit instanceof IfcConversionBasedUnit) {
                        txt.append("\t\t" + noSIUnitmsg + ((IfcConversionBasedUnit) kindOfUnit).getName() + "\n");
                    }
            	}
            }
        }
    }
    /*
    protected void validate3DContext() {
    	// TODO Where is the correct placement for the IfcGeometricRepresentationContext check?
		IfcGeometricRepresentationContext geometricRepresentationContext = model.getAll(IfcGeometricRepresentationContext.class).stream()
				.filter(feature -> feature instanceof IfcGeometricRepresentationContext && "Model".equals(feature.getContextType()))
				.map(feature -> (IfcGeometricRepresentationContext) feature)
				.findFirst()
				.orElse(null);
		if (sites != null || geometricRepresentationContext != null) {
			long dimensionCount = geometricRepresentationContext.getCoordinateSpaceDimension();
    		txt.append("The dimension context in the IFC model is: " + dimensionCount + "\n");
    		switch((int)dimensionCount) {
			// TODO How could be dealt with the respective CoordinateSpaceDimension?
				case 1: txt.append("A 1D context is present. The IFC model is unsuitable for further investigation." + "\n"); break;
				case 2: txt.append("A 2D context is present. The IFC model is unsuitable for further investigation." + "\n"); break;
				case 3: txt.append("A 3D context is present. The IFC model is suitable for further investigation." + "\n"); break;
			}
			txt.append("A geodata analysis for LoGeoRef20 is conducted." + "\n");
    		txt.append("Check for the presence of the following IFC enitities and their attributes: "+ "\n"
    				 + "\tIfcSite\n" + "IfcGeometricRepresentationContext\n\n");
            geodataLoGeoRef20_40.validateGeodataLoGeoRef20_40(txt, model);
		} else {
			txt.append("A geodata analysis for Variant 1 (LoGeoRef50) or Variant 2 (LoGeoRef20/40) is impossible.");
		}
    }
    */
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
