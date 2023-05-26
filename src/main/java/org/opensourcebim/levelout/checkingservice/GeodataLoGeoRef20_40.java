package org.opensourcebim.levelout.checkingservice;

import java.util.Arrays;
import java.util.List;

import org.bimserver.models.ifc4.IfcDirection;
import org.bimserver.models.ifc4.IfcGeometricRepresentationContext;
import org.bimserver.models.ifc4.IfcSite;
import org.eclipse.emf.common.util.EList;

public class GeodataLoGeoRef20_40 extends Validation {

	public GeodataLoGeoRef20_40(StringBuilder txt) {
		this.txt = txt;
	}

	public void validateGeodataLoGeoRef20_40(IfcSite site, IfcGeometricRepresentationContext context) {
		txt.append("Validate Level of Georeferencing 20/40 (IFC2x3)." + "\n");
		validatePosition(site);
		validateRotation(context);
	}

	private void validatePosition(IfcSite site) {
        List<String> validValues = Arrays.asList("RefLatitude", "RefLongitude", "RefElevation");
        validateAttributes(validValues, site);
		EList<Long> refLatitude = site.getRefLatitude();
		if (refLatitude.size() < 3) {
			txt.append("Site reference latitude lacking required values for degrees, minutes, seconds.\n");
		} else if (refLatitude.size() < 4) {
			txt.append("Site reference latitude value only precise to the second, probably some default value from CAD software.\n");
		}
		EList<Long> refLongitude = site.getRefLongitude();
		if (refLongitude.size() < 3) {
			txt.append("Site reference longitude lacking required values for degrees, minutes, seconds.\n");
		} else if (refLongitude.size() < 4) {
			txt.append("Site reference longitude value only precise to the second, probably some default value from CAD software.\n");
		}
	}

	private void validateRotation(IfcGeometricRepresentationContext context) {
		IfcDirection trueNorth = context.getTrueNorth();
		if(trueNorth==null) {
			txt.append("True north not set. Assuming WCS y-axis is north.\n");
			return;
		}
		if (!(trueNorth.getDim() == 2 && trueNorth.getDirectionRatios().size() == 2)) {
			txt.append("Wrong true north dimension.\n");
		}
		List<String> requiredAttributes = List.of("DirectionRatios");
        validateAttributes(requiredAttributes, trueNorth);
	}
}
