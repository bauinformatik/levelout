package org.opensourcebim.levelout.checkingservice;

import org.bimserver.emf.IfcModelInterface;
import org.bimserver.models.ifc4.*;
import org.bimserver.utils.IfcUtils;
import org.eclipse.emf.common.util.EList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GeodataValidation extends Validation {

	public GeodataValidation(StringBuilder txt) {
		this.txt = txt;
	}

	public void validateGeodata(IfcModelInterface model) {
		txt.append("Geodata Validation\n-------------------\n");
		IfcProject project = validateProjects(model);
		if(project == null) return;
		IfcSite site = validateSites(project);
		if (site == null) return;
		validateSite(site);
		IfcGeometricRepresentationContext context = validateContext(project);
		if (context == null) return;
		validateUnits(project);
	    new GeodataLoGeoRef50(txt).validateGeodataLoGeoRef50(context);
	    new GeodataLoGeoRef20_40(txt).validateGeodataLoGeoRef20_40(site, context);
	}

	private IfcGeometricRepresentationContext validateContext(IfcProject project) {
		txt.append("\t" + "Context Validation" + "\n");
		EList<IfcRepresentationContext> representationContexts = project.getRepresentationContexts();
		List<IfcGeometricRepresentationContext> geometricRepresentationContexts = representationContexts.stream()
				.filter(feature -> feature instanceof IfcGeometricRepresentationContext
						&& !(feature instanceof  IfcGeometricRepresentationSubContext)
						&& "Model".equals(feature.getContextType())
						&& ((IfcGeometricRepresentationContext) feature).getCoordinateSpaceDimension() == 3)
				.map(feature -> (IfcGeometricRepresentationContext) feature)
				.collect(Collectors.toList());
		if (geometricRepresentationContexts.isEmpty()) {
			txt.append("\t\tThere is no 3D model context for the project.\n");
			return null;
		} else if (geometricRepresentationContexts.size() == 1){
			txt.append("\t\tExactly one 3D model context for the project.\n");
		} else {
			txt.append("\t\tThere is more than one 3D model context, using first context.\n");
		}
		return geometricRepresentationContexts.get(0);
	}

	protected IfcProject validateProjects(IfcModelInterface model) {
		txt.append("\t" + "Project Validation" + "\n");
		List <IfcProject> projects = model.getAll(IfcProject.class); // TODO
		if (projects.isEmpty()) {
			txt.append("\t\t" + "There is no IfcProject entity in the IFC model.\n");
			return null;
		} else if ( projects.size() == 1 ){
			txt.append("\t\t" + "Exactly one project: ").append(projects.get(0).getName()).append("\n");
		} else  {
			txt.append("\t\t" + "There is more than one project, using first project: ").append(projects.get(0).getName()).append("\n");
		}
		return projects.get(0);
	}

	protected IfcSite validateSites(IfcProject project) {
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
			txt.append("\t\t" + "There is no IfcSite entity in the IFC model.\n");
			return null;
		} else if (sites.size() == 1) {
			txt.append("\t\t" + "Exactly one site: ").append(sites.get(0).getName()).append("\n");
		} else {
			txt.append("\t\t" + "There is more than one site, using first site: ").append(sites.get(0).getName()).append("\n");
		}
		return sites.get(0);
	}

	protected void validateSite(IfcSite site){
		IfcElementCompositionEnum elementCompositionType = site.getCompositionType();
		if (elementCompositionType == IfcElementCompositionEnum.COMPLEX ||elementCompositionType == IfcElementCompositionEnum.PARTIAL) {
			txt.append("\t\t" + "The CompositionType attribute for the selected size is " + elementCompositionType.getName() + "\n");
		}
	}

	protected void validateUnits(IfcProject project) {
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
					String noSIUnitmsg = "\t\tThe SI unit is not METRE, but ";
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

}