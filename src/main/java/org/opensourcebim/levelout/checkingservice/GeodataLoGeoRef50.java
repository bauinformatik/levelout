package org.opensourcebim.levelout.checkingservice;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.bimserver.models.ifc4.IfcCoordinateOperation;
import org.bimserver.models.ifc4.IfcCoordinateReferenceSystem;
import org.bimserver.models.ifc4.IfcGeometricRepresentationContext;
import org.bimserver.models.ifc4.IfcMapConversion;

public class GeodataLoGeoRef50 extends Validation {

	public GeodataLoGeoRef50(StringBuilder txt) {
		this.txt = txt;
	}

	public void validateGeodataLoGeoRef50(IfcGeometricRepresentationContext context) {
		txt.append("Validate Level of Georeferencing 50 (IFC4)." + "\n");
		validateIfcGeometricRepresentationContext(context);
		List<IfcCoordinateOperation> coordinateoperations = context.getHasCoordinateOperation();
		IfcMapConversion mapConversions = coordinateoperations.stream()
				.filter(feature -> feature instanceof IfcMapConversion)
				.map(feature -> (IfcMapConversion) feature)
				.findFirst()
				.orElse(null);
		if (mapConversions == null){
			txt.append("The IfcMapConversion entity is missing from the IFC model.\n");
			return;
		}
		validateIfcMapConversion(mapConversions);
		IfcCoordinateReferenceSystem projectedCRS = mapConversions.getTargetCRS();
		if(projectedCRS == null){
			txt.append("The IFC entity IfcProjectedCRS is missing from the IFC model.\n");
			return;
		}
		validateIfcCoordinateReferenceSystem(projectedCRS);
    }

	private void validateIfcMapConversion(IfcMapConversion mapConversions) {
        List<String> requiredAttributes = Arrays.asList("SourceCRS", "TargetCRS", "Eastings","Northings", "OrthogonalHeight", "XAxisAbscissa", "XAxisOrdinate", "Scale");
        validateAttributes(requiredAttributes, mapConversions);
    }

	private void validateIfcGeometricRepresentationContext(IfcGeometricRepresentationContext geometricRepresentationContexts) {
		List<String> requiredAttributes = Arrays.asList("WorldCoordinateSystem", "TrueNorth", "HasCoordinateOperation");
		validateAttributes(requiredAttributes, geometricRepresentationContexts);
	}

	private void validateIfcCoordinateReferenceSystem(IfcCoordinateReferenceSystem coordinateReferenceSystem) {
		// printAttributes(coordinateReferenceSystem);
        List<String> requiredAttributes = Arrays.asList("GeodeticDatum", "VerticalDatum", "HasCoordinateOperation", "MapProjection", "MapZone", "MapUnit");
        validateAttributes(requiredAttributes, coordinateReferenceSystem);
        // Prüfen, ob im Attributwert von "Name" der IFC-Entität IfcProjectedCRS der EPSG-Code vorhanden ist
        boolean hasEPSGWord = coordinateReferenceSystem.getName().startsWith("EPSG:");
        boolean hasEPSGNumber = Pattern.compile("\\b\\d{4,5}\\b").matcher(coordinateReferenceSystem.getName()).find();
		if (hasEPSGWord || hasEPSGNumber) {
			txt.append("\n").append("The IFC model contains an EPSG code.\n");
		} else {
			txt.append("\n").append("The EPSG code is missing in the IFC model.\n");
		}
	}

}