package org.opensourcebim.levelout.checkingservice;

import java.util.Arrays;
import java.util.List;

import org.bimserver.models.ifc4.IfcDirection;
import org.bimserver.models.ifc4.IfcGeometricRepresentationContext;
import org.bimserver.models.ifc4.IfcSite;

public class GeodataLoGeoRef20_40 extends Validation {
	
	public void validateGeodataLoGeoRef20_40(StringBuilder txt, IfcSite site, IfcGeometricRepresentationContext context) {
		txt.append("Validate Level of Georeferencing 20/40 (IFC2x3)." + "\n");
		validatePosition(txt, site);
		validateRotation(txt, context);
	}
	
	private void validatePosition(StringBuilder txt, IfcSite site) {
		// TODO take from serializer
        List<String> validValues = Arrays.asList("RefLatitude", "RefLongitude", "RefElevation");
        validateAttributes(txt, validValues, site);
	}
	
	private void validateRotation(StringBuilder txt, IfcGeometricRepresentationContext context) {
		// TODO take from serializer
		IfcDirection directions = context.getTrueNorth();
		if(directions==null) {
			txt.append("True north not set.\n");
			return;
		}
		List<String> validValues = List.of("DirectionRatios");
        validateAttributes(txt, validValues, directions);
	}
}
