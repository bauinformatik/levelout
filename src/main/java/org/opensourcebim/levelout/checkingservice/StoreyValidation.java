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
        	txt.append("\tThe IFC entity IfcBuildingStorey is missing from the IFC model.\n");
        	return;
        	//TODO solution in case of missing building storeys
        }
        //Total number of building storeys
    	long totalNumberOfBuildingStoreys = buildingStoreys.size();
    	txt.append("\tThe total number of IfcBuildingStorey instances is: " + totalNumberOfBuildingStoreys + "\n");
    	//TODO add a information about the unit, e.g. [m], [mm], etc.
		//List with building storey and elevation attribute
    	txt.append("\n" + "\tList of building storeys (Name: Elevation)\n");
    	for (IfcBuildingStorey buildingStorey : buildingStoreys) {
			String buildingStoreyName = buildingStorey.getName();
			String  buildingStoreyElevationString = buildingStorey.getElevationAsString();
			if (buildingStoreyElevationString == null || buildingStoreyElevationString.equals("null")) {
				txt.append("\tThe value of the attribute Elevation is missing from building storey: " + buildingStoreyName + "\n");
				return;
			}
			double buildingStoreyElevation = buildingStorey.getElevation();
			txt.append("\t\t" + buildingStoreyName + ": " + buildingStoreyElevation + "\n");
		}
	}
}
