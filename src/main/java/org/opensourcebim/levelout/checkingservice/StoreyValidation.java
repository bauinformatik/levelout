package org.opensourcebim.levelout.checkingservice;

import java.util.List;

import org.bimserver.emf.IfcModelInterface;
import org.bimserver.models.ifc4.IfcBuildingStorey;

public class StoreyValidation extends Validation {

	public StoreyValidation(StringBuilder txt) {
		this.txt = txt;
	}
	
	public void validateStorey(IfcModelInterface model) {
		txt.append("Storey Validation\n-------------------\n");
		//TODO //Find a solution with relation to the IfcProject
		//TODO number of storeys (IfcBuildingStorey)
		List<IfcBuildingStorey> buildingStoreys = model.getAll(IfcBuildingStorey.class);
        if (buildingStoreys == null || buildingStoreys.isEmpty()) {
        	txt.append("The IFC entity IfcBuildingStorey is missing from the IFC model.\n");
        	return;
        	//TODO solution in case of missing building storeys
        }
        //Total number of building storeys
    	long totalNumberOfBuildingStoreys = buildingStoreys.size();
    	txt.append("The total number of IfcBuildingStorey instances is: " + totalNumberOfBuildingStoreys + "\n");
    	//TODO add a information about the unit, e.g. [m], [mm], etc.
		//List with building storey and elevation attribute
    	txt.append("\tList of building storeys (Name: Elevation)\n");
    	for (IfcBuildingStorey buildingStorey : buildingStoreys) {
			String buildingStoreyName = buildingStorey.getName();
			double buildingStoreyElevation = buildingStorey.getElevation();
			txt.append("\t\t" + buildingStoreyName + ": " + buildingStoreyElevation + "\n");
		}
	}
}
