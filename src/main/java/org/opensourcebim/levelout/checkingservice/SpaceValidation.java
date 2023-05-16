package org.opensourcebim.levelout.checkingservice;

import org.bimserver.emf.IfcModelInterface;
import org.bimserver.models.ifc4.IfcBuildingStorey;
import org.bimserver.models.ifc4.IfcRelAggregates;
import org.bimserver.models.ifc4.IfcSpace;

public class SpaceValidation {
	
	public void validateSpace(StringBuilder txt, IfcModelInterface model) {
		txt.append("Space Validation\n-------------------\n");
		//Validates the presence of the respective IFC entities and IFC select types for the investigation
		 IfcSpace spaces = model.getAll(IfcSpace.class).stream().findFirst().orElse(null);
        if (spaces != null) {
        	//Total number of spaces 
        	long TotalNumberOfSpaces = model.getAll(IfcSpace.class).size();
        	txt.append("The total number of IfcSpace is: ").append(TotalNumberOfSpaces).append("\n");
        	
        	//Number of spaces per storey
        	for (IfcBuildingStorey buildingStorey : model.getAll(IfcBuildingStorey.class)) {
        		long numberOfSpaces = 0;
        		for (IfcRelAggregates relAggregates : buildingStorey.getIsDecomposedBy()) {
    				numberOfSpaces += relAggregates.getRelatedObjects().stream()
    					.filter(space -> space instanceof IfcSpace)
    					.count();
    			}
        		txt.append("The number of IfcSpace in storey '" + buildingStorey.getName() + "' is: " + numberOfSpaces + "\n");
        	}
        	
        	//Geometric concept of spaces
        	
        } else {
        	txt.append("The IFC entity IfcSpace is missing from the IFC model.\n");
        }
	}
}
