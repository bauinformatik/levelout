package org.opensourcebim.levelout.checkingservice;

import java.util.Arrays;
import java.util.List;

import org.bimserver.emf.IfcModelInterface;
import org.bimserver.models.ifc4.IfcAxis2Placement;
import org.bimserver.models.ifc4.IfcAxis2Placement2D;
import org.bimserver.models.ifc4.IfcAxis2Placement3D;
import org.bimserver.models.ifc4.IfcContext;
import org.bimserver.models.ifc4.IfcDirection;
import org.bimserver.models.ifc4.IfcGeometricRepresentationContext;
import org.bimserver.models.ifc4.IfcLocalPlacement;
import org.bimserver.models.ifc4.IfcMapConversion;
import org.bimserver.models.ifc4.IfcObjectDefinition;
import org.bimserver.models.ifc4.IfcObjectPlacement;
import org.bimserver.models.ifc4.IfcProject;
import org.bimserver.models.ifc4.IfcReal;
import org.bimserver.models.ifc4.IfcRelAggregates;
import org.bimserver.models.ifc4.IfcRepresentationContext;
import org.bimserver.models.ifc4.IfcSite;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public class GeodataLoGeoRef20_40 extends GeodataLoGeoRef {
	
	public void validateGeodataLoGeoRef20_40(StringBuilder txt, IfcModelInterface model) {
		
		//Validates the presence of the respective IFC entities and IFC select types for the investigation
		IfcSite sites = model.getAll(IfcSite.class).stream().findFirst().orElse(null);
		if (sites != null) {
			validateIfcSite(txt, sites);
			 
			//Extention with LoGeoRef40
			EList<IfcRelAggregates> relAggregates = sites.getDecomposes();
			txt.append("Test value: " + relAggregates + "\n");
			if (relAggregates != null) {
				
			} else {
				txt.append("The IFC entity IfcRelAggregates for the connection to the TrueNorth attribute is missing from the IFC model.\n");
			}
			
			IfcRelAggregates relAggregate = relAggregates.stream()
	     				.filter(feature -> feature instanceof IfcRelAggregates)
	     				.map(feature -> (IfcRelAggregates) feature)
	     				.findFirst()
	     				.orElse(null);
			txt.append("Test value: " + relAggregate + "\n");
			 
			IfcObjectDefinition objectDefinitions = relAggregate.getRelatingObject();
			txt.append("Test value: " + objectDefinitions + "\n");
			
			EList<IfcRepresentationContext> representationContexts = ((IfcContext) objectDefinitions).getRepresentationContexts();
			txt.append("Test value: " + representationContexts + "\n");
			IfcGeometricRepresentationContext geometricRepresentationContexts = representationContexts.stream()
     				.filter(feature -> feature instanceof IfcGeometricRepresentationContext && "Model".equals(feature.getContextType()))
     				.map(feature -> (IfcGeometricRepresentationContext) feature)
     				.findFirst()
     				.orElse(null);
			
			IfcDirection directions = geometricRepresentationContexts.getTrueNorth();
			txt.append("Test value: " + directions + "\n");
			
			EList<Double> reals = directions.getDirectionRatios();
			txt.append("Test value: " + reals + "\n");
			validateTrueNorth(txt, directions);
			
			/*
			EClass eClass = ((EObject) directions).eClass();
			EList<EStructuralFeature> eFeatures = eClass.getEAllStructuralFeatures();
			txt.append("\n").append("The attributes of " + eClass.getName().toString() + " are: \n");
	        for (EStructuralFeature eFeature : eFeatures) {
	        	String featureName = eFeature.getName();
	            Object featureValue = sites.eGet(eFeature);
	            txt.append("\t").append(featureName).append(": ").append(featureValue).append("\n");
	        }
	        txt.append("\n");
	        */
			
			/* 
			EList<IfcRepresentationContext> projects = null;
				if (objectDefinitions instanceof IfcContext) {
					projects = ((IfcContext) objectDefinitions).getRepresentationContexts();
				}
			*/ 
			 
		 } else {
             txt.append("The IFC entity IfcSite is missing from the IFC model.\n");
         }
	}
	
	//Checks a respective set of attributes of IfcSite for their presence
	private void validateIfcSite(StringBuilder txt, IfcSite sites) {
		EClass eClass = sites.eClass();
		EList<EStructuralFeature> eFeatures = eClass.getEAllStructuralFeatures();
        List<String> validValues = Arrays.asList("RefLatitude", "RefLongitude", "RefElevation");
        validateAttributes(txt, eClass, eFeatures, validValues, sites);
	}
	
	private void validateTrueNorth(StringBuilder txt, IfcDirection directions) {
		EClass eClass = directions.eClass();
		EList<EStructuralFeature> eFeatures = eClass.getEAllStructuralFeatures();
		List<String> validValues = Arrays.asList("DirectionRatios");
        validateAttributes(txt, eClass, eFeatures, validValues, directions);
	}
}

//tools:
//Text output of the respective IFC entity name
		/*
		txt.append("\n").append("The attributes of " + eClass.getName().toString() + " are: \n");
      for (EStructuralFeature eFeature : eFeatures) {
      	String featureName = eFeature.getName();
          Object featureValue = sites.eGet(eFeature);
          txt.append("\t").append(featureName).append(": ").append(featureValue).append("\n");
      }
      txt.append("\n");
      */