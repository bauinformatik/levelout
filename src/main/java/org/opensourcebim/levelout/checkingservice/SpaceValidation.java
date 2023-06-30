package org.opensourcebim.levelout.checkingservice;

import java.util.*;
import java.util.stream.Collectors;

import org.bimserver.emf.IfcModelInterface;
import org.bimserver.models.ifc4.IfcBuildingStorey;
import org.bimserver.models.ifc4.IfcProductDefinitionShape;
import org.bimserver.models.ifc4.IfcProductRepresentation;
import org.bimserver.models.ifc4.IfcRelAggregates;
import org.bimserver.models.ifc4.IfcRepresentation;
import org.bimserver.models.ifc4.IfcRepresentationContext;
import org.bimserver.models.ifc4.IfcShapeRepresentation;
import org.bimserver.models.ifc4.IfcSpace;
import org.eclipse.emf.common.util.EList;

public class SpaceValidation extends Validation {
	
	public SpaceValidation(StringBuilder txt) {
		this.txt = txt;
	}

	public void validateSpace(IfcModelInterface model) {
		txt.append("Space Validation\n-------------------\n");
		//Validates the presence of the respective IFC entities and IFC select types for the investigation
		//Find a solution with relation to the IfcProject
		List<IfcSpace> spaces = model.getAll(IfcSpace.class);
        if (spaces == null || spaces.isEmpty()) {
        	txt.append("\tThe IFC entity IfcSpace is missing from the IFC model.\n");
        	return;
        }
    	//Total number of spaces 
    	long totalNumberOfSpaces = spaces.size();
    	txt.append("\tThe total number of IfcSpace is: " + totalNumberOfSpaces + "\n");
    	
    	//Number of spaces per storey
    	//Find a solution with relation to the IfcProject
    	for (IfcBuildingStorey buildingStorey : model.getAll(IfcBuildingStorey.class)) {
    		long numberOfSpaces = 0;
    		for (IfcRelAggregates relAggregates : buildingStorey.getIsDecomposedBy()) {
				numberOfSpaces += relAggregates.getRelatedObjects().stream()
					.filter(space -> space instanceof IfcSpace)
					.count();
			}
    		txt.append("\t\tThe number of IfcSpace in storey '" + buildingStorey.getName() + "' is: " + numberOfSpaces + "\n");
    	}
    	//3D representation of spaces
    	List<IfcProductRepresentation> productRepresentations = new ArrayList<>();
        for (IfcSpace space : spaces) {
            IfcProductRepresentation productRepresentation = space.getRepresentation();
            if (productRepresentation == null) {
                txt.append("\t\t\t" + "The representation is unavailable." + "\n");
                continue;
                //TODO Instanzen von IfcSpace herausfiltern, die keine Representation besitzen
            }
            productRepresentations.add(productRepresentation);
        }
        List<IfcProductDefinitionShape> productDefinitionShapes = productRepresentations.stream()
                .filter(feature -> feature instanceof IfcProductDefinitionShape)
                .map(feature -> (IfcProductDefinitionShape) feature)
                .collect(Collectors.toList());
        //TODO Ausgabe, wenn nur IfcMaterialDefinitionRepresentation verfügbar ist, aber nicht das erforderliche IfcProductDefinitionShape
        List<IfcRepresentation> representations = new ArrayList<IfcRepresentation>();
        //Es kann eine unterschiedliche Anzahl an representations vorliegen
        for (IfcProductDefinitionShape productDefinitionShape : productDefinitionShapes) {
        	EList<IfcRepresentation> representation = productDefinitionShape.getRepresentations();
        	if (representation == null || representation.isEmpty()) {
        		 txt.append("\t\t\t" + "The representation is unavailable." + "\n");
                 continue;
                 //TODO Instanzen von IfcProductDefinitionShape herausfiltern, die keine Representation besitzen
        	}
        	representations.addAll(representation);
        }
        List<IfcShapeRepresentation> shapeRepresentations = representations.stream()
                .filter(feature -> feature instanceof IfcShapeRepresentation)
                .map(feature -> (IfcShapeRepresentation) feature)
                .collect(Collectors.toList());
        //TODO Ausgabe, wenn kein IfcShapeRepresentation verfügbar ist (IfcRepresentation <-- IfcShapeModel <-- IfcShapeRepresentation)
        List<IfcRepresentationContext> representationContexts = new ArrayList<>();
        for (IfcShapeRepresentation shapeRepresentation : shapeRepresentations) {
        	IfcRepresentationContext representationContext = shapeRepresentation.getContextOfItems();
        	if (representationContext == null) {
        		 txt.append("\t\t\t" + "The RepresentationIdentifier is unavailable" + "\n");
                 continue;
               //TODO Instanzen von (IfcShapeRepresentation herausfiltern, die kein ContextOfItems besitzen
        	}
        	representationContexts.add(representationContext);
        }
        List<IfcRepresentationContext> representationContextsForBody = new ArrayList<>();
        List<IfcRepresentationContext> representationContextsWithoutBody = new ArrayList<>();
        for (IfcRepresentationContext representationContext : representationContexts) {
            if ("Body".equals(representationContext.getContextIdentifier())) {
            	representationContextsForBody.add(representationContext);
            } else {
            	representationContextsWithoutBody.add(representationContext);
            }
        }
        if (spaces.size() == representationContextsForBody.size()) {
    		txt.append("\n" + "\t" + "Every instance of IfcSpace contains a 3D representation/Body." + "\n");
        } else {
        	txt.append("\n" + "\t\t" + "Only " + representationContextsForBody.size() + " instances of IfcSpace out of " + spaces.size() + " contains a 3D representation/Body" + "\n");
        	List<IfcSpace> spacesWithMissingBody = new ArrayList<>();
        	for (IfcSpace space : spaces) {
                boolean hasBodyRepresentation = false;
                for (IfcRepresentation representation : space.getRepresentation().getRepresentations()){
                    if(representationContextsForBody.contains(representation.getContextOfItems())){
                        hasBodyRepresentation = true; break;
                    }
                }
                if (!hasBodyRepresentation) {
                    spacesWithMissingBody.add(space);
                }
            }
         	txt.append("\t" + "The following IfcSpace instances are out of 3D representation/Body: " + spacesWithMissingBody + "\n");
        	txt.append("\t\t" + "Please check your IFC export in your IFC supporting software or manipuate your IFC model." + "\n\n");
        } 
        //Geometric concept of spaces
        List<IfcShapeRepresentation> shapeRepresentationsForSolidModel = new ArrayList<>();
        List<IfcShapeRepresentation> shapeRepresentationsWithoutSolidModel = new ArrayList<>();
        for (IfcShapeRepresentation shapeRepresentation : shapeRepresentations) {
        	String representationType = shapeRepresentation.getRepresentationType();
            if (Arrays.asList("SweptSolid", "AdvancedSweptSolid", "Brep", "AdvancedBrep", "CSG", "Clipping").contains(representationType))  {
            	shapeRepresentationsForSolidModel.add(shapeRepresentation);
            } else {
            	shapeRepresentationsWithoutSolidModel.add(shapeRepresentation);
            }
        }
        if (spaces.size() == shapeRepresentationsForSolidModel.size()) {
    		txt.append("\t" + "Every instance of IfcSpace contains a 3D representation/SolidModel." + "\n");
        } else {
        	txt.append("\t" + "Only " + shapeRepresentationsForSolidModel.size() + " instances of IfcSpace out of " + spaces.size() + " contains a 3D representation/SolidModel" + "\n");
        	List<IfcSpace> spacesWithMissingSolidModel = new ArrayList<>();
        	for (IfcSpace space : spaces) {
                if (!shapeRepresentationsForSolidModel.contains(space)) {
                    spacesWithMissingSolidModel.add(space);
                }
            }
         	txt.append("\t\t\t" + "The following IfcSpace instances are out of 3D representation/SolidModel: " + spacesWithMissingSolidModel + "\n");
        	txt.append("\t\t\t" + "Please check your IFC export in your IFC supporting software or manipuate your IFC model." + "\n");
        }
        //Counts of geometric concept
        Map<String, Integer> representationTypeCounts = new HashMap<>();
        for (IfcShapeRepresentation shapeRepresentation : shapeRepresentations) {
            String representationType = shapeRepresentation.getRepresentationType();
            if (!representationTypeCounts.containsKey(representationType)) {
                representationTypeCounts.put(representationType, 1);
            }
            representationTypeCounts.put( representationType, representationTypeCounts.get(representationType) + 1);
        }
        txt.append("\n" + "\t" + "The geometric concept of IfcSpace instances has the following counts of values: " + "\n");
        for (Map.Entry<String, Integer> representationTypeCount : representationTypeCounts.entrySet()) {
            txt.append("\t\t" + "Representation Type: " + representationTypeCount.getKey() + ", Count: " + representationTypeCount.getValue() + "\n");
        }
        //txt.append("Test value: "  + "\n");
	}
}
