package org.opensourcebim.levelout.checkingservice;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bimserver.emf.IfcModelInterface;
import org.bimserver.models.ifc4.IfcBuildingStorey;
import org.bimserver.models.ifc4.IfcDoor;
import org.bimserver.models.ifc4.IfcGeometricRepresentationContext;
import org.bimserver.models.ifc4.IfcGeometricRepresentationSubContext;
import org.bimserver.models.ifc4.IfcOpeningElement;
import org.bimserver.models.ifc4.IfcProductDefinitionShape;
import org.bimserver.models.ifc4.IfcProductRepresentation;
import org.bimserver.models.ifc4.IfcRelAggregates;
import org.bimserver.models.ifc4.IfcRelContainedInSpatialStructure;
import org.bimserver.models.ifc4.IfcRelFillsElement;
import org.bimserver.models.ifc4.IfcRelSpaceBoundary;
import org.bimserver.models.ifc4.IfcRepresentation;
import org.bimserver.models.ifc4.IfcRepresentationContext;
import org.bimserver.models.ifc4.IfcShapeRepresentation;
import org.bimserver.models.ifc4.IfcSpace;
import org.bimserver.models.ifc4.IfcVirtualElement;
import org.eclipse.emf.common.util.EList;

public class SpaceBoundaryValidation extends Validation {
	
	public SpaceBoundaryValidation(StringBuilder txt) {
		this.txt = txt;
	}

	public void validateSpaceBoundary(IfcModelInterface model) {
		txt.append("Space Boundary Validation\n-------------------\n");
		//TODO Find a solution with relation to the IfcProject
		//TODO Number of storeys (IfcBuildingStorey), spaces (IfcSpace)/storey, doors(IfcDoor)/storey and  opening element(IfcOpeningElement/storey (evtl. noch IfcVirtualElement)
		List<IfcBuildingStorey> buildingStoreys = model.getAll(IfcBuildingStorey.class);
        if (buildingStoreys == null || buildingStoreys.isEmpty()) {
        	txt.append("\t" + "The IFC entity IfcBuildingStorey is missing from the IFC model.\n");
        	return;
        }
        //Total number of building storeys 
    	long totalNumberOfBuildingStoreys = buildingStoreys.size();
    	txt.append("\t" + "The total number of IfcBuildingStorey is: " + totalNumberOfBuildingStoreys + "\n");
		//Total number of spaces per building storey
    	List<IfcSpace> spaces = model.getAll(IfcSpace.class);
        if (spaces == null || spaces.isEmpty()) {
        	txt.append("\t" + "The IFC entity IfcSpace is missing from the IFC model.\n");
        	return;
        }
    	for (IfcBuildingStorey buildingStorey : buildingStoreys) {
    		long numberOfSpaces = 0;
    		for (IfcRelAggregates relAggregates : buildingStorey.getIsDecomposedBy()) {
				numberOfSpaces += relAggregates.getRelatedObjects().stream()
					.filter(space -> space instanceof IfcSpace)
					.count();
			}
    		txt.append("\t" + "The number of IfcSpace instances in building storey '" + buildingStorey.getName() + "' is: " + numberOfSpaces + "\n");
    	}
    	//Total number of doors per building storey
    	txt.append("\n");
    	List<IfcDoor> doors = model.getAll(IfcDoor.class);
        if (doors == null || doors.isEmpty()) {
        	txt.append("\t" + "The IFC entity IfcDoor is missing from the IFC model.\n");
        	return;
        }
    	for (IfcBuildingStorey buildingStorey : buildingStoreys) {
    		long numberOfDoors = 0;
    		for (IfcRelContainedInSpatialStructure relContainedInSpatialStructure : buildingStorey.getContainsElements()) {
    			numberOfDoors += relContainedInSpatialStructure.getRelatedElements().stream()
    				.filter(door -> door instanceof IfcDoor)
    				.count();
    		}
    		txt.append("\t" + "The number of IfcDoor instances in building storey '" + buildingStorey.getName() + "' is: " + numberOfDoors + "\n");
    	}
    	//Total number of opening elements per building storey
    	txt.append("\n");
    	List<IfcOpeningElement> openingElements = model.getAll(IfcOpeningElement.class);
        if (openingElements == null || openingElements.isEmpty()) {
        	txt.append("\t" + "The IFC entity IfcOpeningElement is missing from the IFC model.\n");
        	return;
        }
    	for (IfcBuildingStorey buildingStorey : buildingStoreys) {
    		long numberOfOpeningElements = 0;
    		for (IfcRelContainedInSpatialStructure relContainedInSpatialStructure : buildingStorey.getContainsElements()) {
    			numberOfOpeningElements += relContainedInSpatialStructure.getRelatedElements().stream()
    				.filter(openingElement -> openingElement instanceof IfcDoor)
    				.count();
    		}
    		txt.append("\t" + "The number of IfcOpeningElement instances in building storey '" + buildingStorey.getName() + "' is: " + numberOfOpeningElements + "\n");
    	}
    	//Total number of virtual elements per building storey
    	//TODO Quelltext funktioniert so nicht, es können IfcVirtualElement-Instanzen vorhanden sein, diese stehen jedoch nicht in Beziehung mit IfcBuildingStorey-Instanzen
    	txt.append("\n");
    	List<IfcVirtualElement> virtualElements = model.getAll(IfcVirtualElement.class);
    	if (virtualElements == null || virtualElements.isEmpty()) {
    		txt.append("\t" + "The IFC entity IfcVirtualElement is missing from the IFC model.\n");
    		return;
    	}
    	for (IfcBuildingStorey buildingStorey : buildingStoreys) {
    		long numberOfVirtualElements = 0;
    		for (IfcRelContainedInSpatialStructure relContainedInSpatialStructure : buildingStorey.getContainsElements()) {
    			numberOfVirtualElements += relContainedInSpatialStructure.getRelatedElements().stream()
    				.filter(virtualElement -> virtualElement instanceof IfcVirtualElement)
    				.count();
    		}
    		txt.append("\t" + "The number of IfcVirtualElement instances in building storey '" + buildingStorey.getName() + "' is: " + numberOfVirtualElements + "\n");
    	}
    	//Liegt folgende Beziehung vor IfcSpace - IfcRelSpaceBoundary (SBL1) - IfcDoor/IfcOpeningElement
    	//Hat die IfcRelSpaceBoundary-Instanz eine Verbindung zu einem Raum und zu einer Tür/Öffnungselement?
    	//Anzahl an jeder Beziehung (z.B. IfcSpace - IfcRelSpaceBoundary und IfcRelSpaceBoundary -IfcDoor/IfcOpeningElement) muss gleich der Anzahl an IfcSpace, IfcDoor/IfcOpeningElement pro IfcBuildingStorey sein
    	txt.append("\n");
    	for (IfcBuildingStorey buildingStorey : buildingStoreys) {
    	    long numberOfRelSpaceBoundarys = 0;
    	    for (IfcSpace space : spaces) {
    	        for (IfcRelSpaceBoundary relSpaceBoundary : space.getBoundedBy()) {
    	        	 numberOfRelSpaceBoundarys++;
    	        }
    	    }
    	    txt.append("\t" + "The number of IfcRelSpaceBoundary instances in building storey '"
    	        + buildingStorey.getName() + "' is: " + numberOfRelSpaceBoundarys + "\n");
    	}
    	//Identification of the number of IfcRelSpaceBoundary instances related to an IfcDoor instances.
    	txt.append("\n");
    	for (IfcBuildingStorey buildingStorey : buildingStoreys) {
    	    long numberOfRelSpaceBoundarys = 0;
    	    for (IfcDoor door : doors) { //über Liste Türen pro Geschoss iterieren
    	        for (IfcRelSpaceBoundary relSpaceBoundary : door.getProvidesBoundaries()) {
    	        	 numberOfRelSpaceBoundarys++;
    	        	 //Beachte Türen mit mehr als zwei Raumbegrenzungen möglich
    	        }
    	    }
    	    txt.append("\t" + "The number of IfcRelSpaceBoundary instances with a connection to an IfcDoor instance in building storey '"
    	        + buildingStorey.getName() + "' is: " + numberOfRelSpaceBoundarys + "\n");
    	}
    	txt.append("\n");
    	for (IfcBuildingStorey buildingStorey : buildingStoreys) {
    	    long numberOfRelSpaceBoundarys = 0;
    	    for (IfcOpeningElement openingElement : openingElements) {
    	        for (IfcRelSpaceBoundary relSpaceBoundary : openingElement.getProvidesBoundaries()) {
    	        	 numberOfRelSpaceBoundarys++;
    	        }
    	    }
    	    txt.append("\t" + "The number of IfcRelSpaceBoundary instances with a connection to an IfcOpeningElement instance in building storey '"
    	        + buildingStorey.getName() + "' is: " + numberOfRelSpaceBoundarys + "\n");
    	}
    	//Identification of free-standing doors
    	txt.append("\n");
    	for (IfcBuildingStorey buildingStorey : buildingStoreys) {
    		long numberOfFreeStandingDoors = 0;
    		long numberOfDoorsWithOpeningElement = 0;
    		for (IfcDoor door : doors) {
    			/*//Variant1 (with issues)
    			boolean doorHasFillsVoids = door.getFillsVoids().contains(openingElements);
    			if (doorHasFillsVoids) {
    				numberOfDoorsWithOpeningElement++;
    			} else {
    				numberOfFreeStandingDoors++;
    			}
    			*/
    			//Variant2
    			EList<IfcRelFillsElement> relFillsElements = door.getFillsVoids();
    			if (relFillsElements == null || relFillsElements.isEmpty()) {
    				numberOfFreeStandingDoors++;
    				continue;
    			} else {
    				List<IfcOpeningElement> relatingOpeningElements = new ArrayList<>();
        			for (IfcRelFillsElement relFillsElement : relFillsElements) {
        				IfcOpeningElement relatingOpeningElement = relFillsElement.getRelatingOpeningElement();
        				if (relatingOpeningElement == null) {
        					numberOfFreeStandingDoors++;
        	                continue;
        	        	} else {
        	        		numberOfDoorsWithOpeningElement++;
        	        	}
        	        	relatingOpeningElements.add(relatingOpeningElement);
        			}
    			}
    		}
    		//txt.append("Test value: " + relatingOpeningElements + "\n");
    		txt.append("\t" + "The number of free-standing doors in building storey '"
    	    	       + buildingStorey.getName() + "' is: " + numberOfFreeStandingDoors + "\n");
    		txt.append("\t" + "The number of doors with relating opening elements in building storey '"
    				+ buildingStorey.getName() + "' is: " + numberOfDoorsWithOpeningElement + "\n");
    	}
    	//Identification of the IfcRelSpaceboundary-Level via the attribute Name
    	txt.append("\n");
    	for (IfcBuildingStorey buildingStorey : buildingStoreys) {
    		long numberOfRelSpaceBoundaryLevel1 = 0;
    		long numberOfRelSpaceBoundaryLevel2 = 0;
    		List<IfcRelSpaceBoundary> relevantRelSpaceBoundarys = new ArrayList<>();
    		for (IfcDoor door : doors) { //and/or IfcOpeningElement?
    			EList<IfcRelSpaceBoundary> relevantRelSpaceBoundary = door.getProvidesBoundaries();
    			if (relevantRelSpaceBoundary == null || relevantRelSpaceBoundary.isEmpty()) {
    				txt.append("\t\t" + "The attribute ProvidesBoundaries is unavailable for the following IfcDoor instances (GUID) " + door.getGlobalId() + "\n");
                    continue;
                    //TODO Filter instances of IfcDoor that have no ProvidesBoundaries attribute
    			}
            	relevantRelSpaceBoundarys.addAll(relevantRelSpaceBoundary);
            }
    		txt.append("\t" + "The number of relevant IfcRelSpaceBoundary instances in building storey '"
 	    	       + buildingStorey.getName() + "' is: " + relevantRelSpaceBoundarys.size() + "\n");
    		for (IfcRelSpaceBoundary relevantRelSpaceBoundary : relevantRelSpaceBoundarys) {
    			if (relevantRelSpaceBoundary.getName() == null) {
    				txt.append("\t" + "No statements can be made about the space boundary levels. "
    						+ "The attribute Name is missing from the IfcRelSpaceBoundary instance (GUID): " + relevantRelSpaceBoundary.getGlobalId() + "\n");
    	        	return;
    			} else if (relevantRelSpaceBoundary.getName().equals("1stLevel")) {
    				numberOfRelSpaceBoundaryLevel1++;
    			} else if (relevantRelSpaceBoundary.getName().equals("2ndLevel")) {
    				numberOfRelSpaceBoundaryLevel2++;
    			} else {
    				txt.append("\t" + "The space boundary level does not correspond to the expected value of '1stLevel' or '2ndLevel'. "
    						+ "The attribute Name of IfcRelSpaceBoundary instance: " + relevantRelSpaceBoundary.getGlobalId() + "is: " + relevantRelSpaceBoundary.getName() + "\n");
    			}
    		}
    		txt.append("\t" + "The number of IfcRelSpaceBoundary space boundary level 1 instances in building storey '"
    	        + buildingStorey.getName() + "' is: " + numberOfRelSpaceBoundaryLevel1 + "\n");
    		txt.append("\t" + "The number of IfcRelSpaceBoundary space boundary level 2 instances in building storey '"
        	        + buildingStorey.getName() + "' is: " + numberOfRelSpaceBoundaryLevel2 + "\n");
    	}
    	//Identify the presence of the geometry of IfcOpeningElement instances.
    	txt.append("\n");
    	for (IfcBuildingStorey buildingStorey : buildingStoreys) {
    		List<IfcProductRepresentation> productRepresentations = new ArrayList<>();
            for (IfcOpeningElement openingElement : openingElements) {
                IfcProductRepresentation productRepresentation = openingElement.getRepresentation();
                if (productRepresentation == null) {
                    txt.append("\t" + "The representation is unavailable." + "\n");
                    continue;
                    //TODO Filter out instances of IfcOpeningElement that do not have a representation.
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
            		 txt.append("\t" + "The representation is unavailable." + "\n");
                     continue;
                     //TODO Instanzen von IfcProductDefinitionShape herausfiltern, die keine Representation besitzen
            	}
            	representations.addAll(representation);
            }
            List<IfcShapeRepresentation> shapeRepresentations = representations.stream()
                    .filter(feature -> feature instanceof IfcShapeRepresentation)
                    .map(feature -> (IfcShapeRepresentation) feature)
                    .collect(Collectors.toList());
    		List<IfcShapeRepresentation> shapeRepresentationsForSolidModel = new ArrayList<>();
            List<IfcShapeRepresentation> shapeRepresentationsWithoutSolidModel = new ArrayList<>();
            for (IfcShapeRepresentation shapeRepresentation : shapeRepresentations) {
            	String representationType = shapeRepresentation.getRepresentationType();
                if (representationType.equals("SweptSolid") || representationType.equals("AdvancedSweptSolid") ||
                        representationType.equals("Brep") || representationType.equals("AdvancedBrep") ||
                        representationType.equals("CSG") || representationType.equals("Clipping")) {
                	shapeRepresentationsForSolidModel.add(shapeRepresentation);
                } else {
                	shapeRepresentationsWithoutSolidModel.add(shapeRepresentation);
                }
            }
            if (openingElements.size() == shapeRepresentationsForSolidModel.size()) {
        		txt.append("\t" + "Every instance of IfcOpeningElement contains a 3D representation/SolidModel." + "\n");
            } else {
            	txt.append("\t" + "Only " + shapeRepresentationsForSolidModel.size() + " instances of IfcOpeningElement out of " + openingElements.size() + " contains a 3D representation/SolidModel" + "\n");
            	List<IfcOpeningElement> openingElementsWithMissingSolidModel = new ArrayList<>();
            	for (IfcOpeningElement openingElement : openingElements) {
                    if (!shapeRepresentationsForSolidModel.contains(openingElement)) {
                    	openingElementsWithMissingSolidModel.add(openingElement);
                    }
                }
             	txt.append("\t" + "The following IfcOpeningElement instances are out of 3D representation/SolidModel: " + openingElementsWithMissingSolidModel + "\n");
            	txt.append("\t" + "Please check your IFC export in your IFC supporting software or manipuate your IFC model." + "\n");
            }
            //Counts of geometric concept
            List<String> representationTypes = new ArrayList<>();
            List<Integer> representationTypeCounts = new ArrayList<>();
            for (IfcShapeRepresentation shapeRepresentation : shapeRepresentations) {
                String representationType = shapeRepresentation.getRepresentationType();
                if (representationTypes.contains(representationType)) {
                    int index = representationTypes.indexOf(representationType);
                    representationTypeCounts.set(index, representationTypeCounts.get(index) + 1);
                } else {
                    representationTypes.add(representationType);
                    representationTypeCounts.add(1);
                }
            }
            txt.append("\t" + "The geometric concept of IfcOpeningElement instances the following counts of values: " + "\n");
            for (int i = 0; i < representationTypes.size(); i++) {
                txt.append("\t\t" + "Representation Type: " + representationTypes.get(i) + ", Count: " + representationTypeCounts.get(i) + "\n");
            }
    	}
    	//txt.append("Test value: "  + "\n");
	}
}

