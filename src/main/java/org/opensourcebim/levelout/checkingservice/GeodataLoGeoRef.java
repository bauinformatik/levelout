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
import java.util.ArrayList;
import java.util.stream.Collectors;

public class GeodataLoGeoRef {
	
	protected void validateGeodataLoGeoRef (StringBuilder txt, IfcModelInterface model) {
		IfcProject project = validateProjects(txt, model);
		if(project == null) return;
		IfcSite site = validateSites(txt, project);
		if(site == null) return;
		validateSite(txt, site);
		IfcGeometricRepresentationContext context = validateContext(txt, project);
		if(context == null) return;
        validateUnits(txt, project);
    }

	private IfcGeometricRepresentationContext validateContext(StringBuilder txt, IfcProject project) {
		txt.append("\t" + "Context Validation" + "\n");
		EList<IfcRepresentationContext> representationContexts = project.getRepresentationContexts();
		List<IfcGeometricRepresentationContext> geometricRepresentationContexts = representationContexts.stream()
				.filter(feature -> feature instanceof IfcGeometricRepresentationContext
						&& "Model".equals(feature.getContextType())
						&& "Body".equals(feature.getContextIdentifier())
						&& ((IfcGeometricRepresentationContext) feature).getCoordinateSpaceDimension() == 3
				)
				.map(feature -> (IfcGeometricRepresentationContext) feature)
				.collect(Collectors.toList());
		if (geometricRepresentationContexts.isEmpty()) {
			txt.append("\t\tThere is no 3D model context for the project.");
			return null;
		} else if (geometricRepresentationContexts.size() == 1){
			txt.append("\t\tExactly one 3D model context for the project.");
		} else {
			txt.append("\t\tThere is more than one 3D model context, using first context.");
		}
		return geometricRepresentationContexts.get(0);
	}

	protected IfcProject validateProjects(StringBuilder txt, IfcModelInterface model) {
		txt.append("\t" + "Project Validation" + "\n");
		List <IfcProject> projects = model.getAll(IfcProject.class); // TODO
		if (projects.isEmpty()) {
			txt.append("\t\t" + "There is no IfcProject entity in the IFC model.");
			return null;
		} else if ( projects.size() == 1 ){
			txt.append("\t\t" + "Exactly one project: ").append(projects.get(0).getName());
		} else  {
			txt.append("\t\t" + "There is more than one project, using first project: ").append(projects.get(0).getName());
		}
		return projects.get(0);
	}
	
	protected IfcSite validateSites(StringBuilder txt, IfcProject project) {
		txt.append("\t" + "Site Validation" + "\n");
		List<IfcSite> sites = new ArrayList<>();
		for (IfcRelAggregates relAggregate : project.getIsDecomposedBy()) {
			for (IfcObjectDefinition aggregated : relAggregate.getRelatedObjects()) {
				if (aggregated instanceof IfcSite) {
					sites.add((IfcSite) aggregated);
				}
			}
		}
		if (sites.isEmpty()) {
			txt.append("\t\t" + "There is no IfcSite entity in the IFC model");
			return null;
		} else if (sites.size() == 1) {
			txt.append("\t\t" + "Exactly one site: ").append(sites.get(0).getName());
		} else {
			txt.append("\t\t" + "There is more than one site, using first site: ").append(sites.get(0).getName());
		}
		return sites.get(0);
	}
	protected void validateSite(StringBuilder txt, IfcSite site){
    	IfcElementCompositionEnum elementCompositionType = site.getCompositionType();
		if (elementCompositionType == IfcElementCompositionEnum.COMPLEX ||elementCompositionType == IfcElementCompositionEnum.PARTIAL) {
			txt.append("\t\t" + "The CompositionType attribute for the selected size is " + elementCompositionType.getName() + "\n");
		}
	}
	
    protected void validateUnits(StringBuilder txt, IfcProject project) {
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

	//Validates the IFC attribute values of a given IFC entity
	protected void validateAttributes(StringBuilder txt, List<String> validValues, EObject targetObject) {
		//Check1 for IFC attribute values of value != null
		EClass eClass = targetObject.eClass();
		EList<EStructuralFeature> eFeatures = eClass.getEAllStructuralFeatures();
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
