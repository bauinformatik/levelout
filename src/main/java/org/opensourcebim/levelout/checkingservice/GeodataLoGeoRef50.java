package org.opensourcebim.levelout.checkingservice;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.bimserver.emf.IfcModelInterface;
import org.bimserver.models.ifc4.IfcCoordinateOperation;
import org.bimserver.models.ifc4.IfcCoordinateReferenceSystem;
import org.bimserver.models.ifc4.IfcCoordinateReferenceSystemSelect;
import org.bimserver.models.ifc4.IfcGeometricRepresentationContext;
import org.bimserver.models.ifc4.IfcMapConversion;
import org.bimserver.models.ifc4.IfcProject;
import org.bimserver.models.ifc4.IfcRepresentationContext;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public class GeodataLoGeoRef50 extends GeodataLoGeoRef {

	public void validateGeodataLoGeoRef50(StringBuilder txt, IfcModelInterface model) {
		txt.append("Validate Level of Georeferencing 50 (IFC4)." + "\n");
		IfcProject projects = model.getAll(IfcProject.class).stream().findFirst().orElse(null);
         if (projects != null) {
			 EList<IfcRepresentationContext> representationContexts = projects.getRepresentationContexts();
			 IfcGeometricRepresentationContext geometricRepresentationContexts = representationContexts.stream()
					 .filter(feature -> feature instanceof IfcGeometricRepresentationContext && "Model".equals(feature.getContextType()))
					 .map(feature -> (IfcGeometricRepresentationContext) feature)
					 .findFirst()
					 .orElse(null);
			 if (geometricRepresentationContexts != null) {
				 validateIfcGeometricRepresentationContext(txt, geometricRepresentationContexts);
			 } else {
                txt.append("The IFC entity IfcGeometricRepresentationContext is missing from the IFC model.\n");
            }
			 EList<IfcCoordinateOperation> coordinateoperations = geometricRepresentationContexts.getHasCoordinateOperation();
			 IfcMapConversion mapConversions = coordinateoperations.stream()
					 .filter(feature -> feature instanceof IfcMapConversion)
					 .map(feature -> (IfcMapConversion) feature)
					 .findFirst()
					 .orElse(null);
			 if (mapConversions != null) {
				 validateIfcMapConversion(txt, mapConversions);
				 IfcCoordinateReferenceSystem projectedCRSs = mapConversions.getTargetCRS();
				 if (projectedCRSs != null) {
					 validateIfcCoordinateReferenceSystem(txt, projectedCRSs);
				 } else {
					 txt.append("The IFC entity IfcProjectedCRS is missing from the IFC model.\n");
				 }
				 validateIfcContext(txt, model, projects, mapConversions, geometricRepresentationContexts);
			 } else {
				 txt.append("The IfcMapConversion entity is missing from the IFC model.\n");
			 }
         } else {
             txt.append("The IFC entity IfcProject is missing from the IFC model.\n");
         }
    }

	//Checks the relationship between IfcContext and IfcGeometricRepresentationContext
	private void validateIfcContext(StringBuilder txt, IfcModelInterface model, IfcProject projects, IfcMapConversion mapConversions, IfcCoordinateReferenceSystemSelect coordinateReferenceSystemSelects) {
		List<IfcRepresentationContext> representationContexts = projects.getRepresentationContexts();
		boolean isValid = representationContexts.stream().anyMatch(coordinateReferenceSystemSelects::equals);
		if (isValid) {
			txt.append("\n").append("IfcContext has a relationship to IfcGeometricRepresentationContext.\n");
		} else {
			txt.append("\n").append("The relationship between IfcContext and IfcGeometricRepresentationContext is missing.\n");
		}
	}

	//Checks a respective set of attributes of IfcMapConversion for their presence
	private void validateIfcMapConversion(StringBuilder txt, IfcMapConversion mapConversions) {
        List<String> requiredAttributes = Arrays.asList("SourceCRS", "TargetCRS", "Eastings","Northings", "OrthogonalHeight", "XAxisAbscissa", "XAxisOrdinate", "Scale");
        validateAttributes(txt, requiredAttributes, mapConversions);
    }

	//Checks a respective set of attributes of IfcIfcGeometricRepresentationContext for their presence
	private void validateIfcGeometricRepresentationContext(StringBuilder txt, IfcGeometricRepresentationContext geometricRepresentationContexts) {
		List<String> requiredAttributes = Arrays.asList("WorldCoordinateSystem", "TrueNorth", "HasCoordinateOperation");
		validateAttributes(txt, requiredAttributes, geometricRepresentationContexts);
	}

	//Checks a respective set of attributes of IfcCoordinateReferenceSystem for their presence
	private void validateIfcCoordinateReferenceSystem(StringBuilder txt, IfcCoordinateReferenceSystem coordinateReferenceSystems) {
		// printAttributes(txt, coordinateReferenceSystems);
        List<String> requiredAttributes = Arrays.asList("GeodeticDatum", "VerticalDatum", "HasCoordinateOperation", "MapProjection", "MapZone", "MapUnit");
        validateAttributes(txt, requiredAttributes, coordinateReferenceSystems);
      //Prüfen, ob im Attributwert von "Name" der IFC-Entität IfcProjectedCRS der EPSG-Code vorhanden ist
        boolean hasEPSGWord = coordinateReferenceSystems.getName().contains("EPSG:");
        boolean hasEPSGNumber = Pattern.compile("\\b\\d{4,5}\\b").matcher(coordinateReferenceSystems.getName()).find();
		if (hasEPSGWord || hasEPSGNumber) {
			//Valid statement
			txt.append("\n").append("The IFC model contains an EPSG code.\n");
		} else {
			//Invalid statement
			txt.append("\n").append("The EPSG code is missing in the IFC model.\n");
		}
	}

	private void printAttributes(StringBuilder txt, EObject object) {
		EClass eClass = object.eClass();
		txt.append("\n").append("The attributes of " + eClass.getName().toString() + " are: \n");
		for (EStructuralFeature eFeature : eClass.getEAllStructuralFeatures()) {
			String featureName = eFeature.getName();
			Object featureValue = object.eGet(eFeature);
			txt.append("\t").append(featureName).append(": ").append(featureValue).append("\n");
		}
		txt.append("\n");
	}
}