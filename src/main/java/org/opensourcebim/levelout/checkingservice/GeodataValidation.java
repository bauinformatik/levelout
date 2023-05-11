package org.opensourcebim.levelout.checkingservice;

import org.bimserver.emf.IfcModelInterface;
import org.bimserver.models.ifc4.IfcAxis2Placement;
import org.bimserver.models.ifc4.IfcAxis2Placement3D;
import org.bimserver.models.ifc4.IfcBuilding;
import org.bimserver.models.ifc4.IfcCartesianPoint;
import org.bimserver.models.ifc4.IfcDirection;
import org.bimserver.models.ifc4.IfcGeometricRepresentationContext;
import org.bimserver.models.ifc4.IfcLocalPlacement;
import org.bimserver.models.ifc4.IfcMapConversion;
import org.bimserver.models.ifc4.IfcObjectPlacement;
import org.bimserver.models.ifc4.IfcPlacement;
import org.bimserver.models.ifc4.IfcProject;
import org.bimserver.models.ifc4.IfcProjectedCRS;
import org.bimserver.models.ifc4.IfcRepresentationContext;
import org.bimserver.models.ifc4.IfcSite;
import org.eclipse.emf.common.util.EList;

public class GeodataValidation {

	// Declaration and initialization of an instance variable named geoLoGeoRef50 of type GeodataLoGeoRef50
	private GeodataLoGeoRef50 geoLoGeoRef50 = new GeodataLoGeoRef50();

	public void validateGeodata(StringBuilder txt, IfcModelInterface model) {
		//Validates the presence of the respective IFC entities and select types for LoGeoRef50
		txt.append("Geodata validation\n----------------\n");
		IfcProject projects = model.getAll(IfcProject.class).stream().findAny().orElse(null);
		IfcMapConversion mapConversions = model.getAll(IfcMapConversion.class).stream().findAny().orElse(null);
		IfcProjectedCRS projectedCRSs = model.getAll(IfcProjectedCRS.class).stream().findAny().orElse(null);
		if (projects != null && (mapConversions != null || projectedCRSs != null)) {
        	txt.append("A geodata analysis for LoGeoRef50 is conducted.");
        	//The validateGeodataLoGeoRef50 method is called on the geoLoGeoRef50 object with the given txt and model parameters.
        	geoLoGeoRef50.validateGeodataLoGeoRef50(txt, model);
    	} else {
    		//Validates the presence of the respective IFC entities and select types for LoGeoRef40
    		//Problem, die zugehörigen IFC-Entitäten für LoGeoRef40 werden wahrscheinlich vorhanden sein, es muss eher geprüft werden, ob diese brauchbar sind. Deshalb kann es sinnvoll sein ab LoGeoRef40 die anderen Prüfungen der LoGeoRefs mit durchlaufen zu lassen
    		EList<IfcRepresentationContext> representationContexts = projects.getRepresentationContexts();
    		IfcGeometricRepresentationContext geometricRepresentationContexts = representationContexts.stream()
    				.filter(feature -> feature instanceof IfcGeometricRepresentationContext && "Model".equals(feature.getContextType()))
    				.map(feature -> (IfcGeometricRepresentationContext) feature)
    				.findFirst()
    				.orElse(null);
    		//IfcAxis2Placement axis2Placements = geometricRepresentationContexts.getWorldCoordinateSystem();
    		//IfcCartesianPoint cartesianPoints = ((IfcPlacement) axis2Placements).getLocation();
    		//IfcDirection directions = geometricRepresentationContexts.getTrueNorth();
    		//txt.append("List all:" + directions).append("\n");
            if (projects != null && geometricRepresentationContexts != null) {
            	txt.append("A geodata analysis for LoGeoRef40 is conducted.");
            	//The validateGeodataLoGeoRef40 method is called on the geoLoGeoRef40 object with the given txt and model parameters.
                //geoLoGeoRef40.validateGeodataLoGeoRef40(txt, model);
            } else {
            	//Validates the presence of the respective IFC entities and select types for LoGeoRef30
            	IfcSite sites = model.getAll(IfcSite.class).stream().findAny().orElse(null);
                IfcObjectPlacement objectPlacements = sites.getObjectPlacement();
                EList<IfcLocalPlacement> localPlacements = objectPlacements.getReferencedByPlacements();
                txt.append("List all:" + localPlacements).append("\n");
                //IfcAxis2Placement ifcAxis2Placements = localPlacements.getRelativePlacement();
                if (sites != null && localPlacements != null) {
                	txt.append("A geodata analysis for LoGeoRef30 is conducted.");
                	//The validateGeodataLoGeoRef40 method is called on the geoLoGeoRef30 object with the given txt and model parameters.
                    //geoLoGeoRef30.validateGeodataLoGeoRef30(txt, model);
                } else {
                	//Validates the presence of the respective IFC entities and select types for LoGeoRef20
                	if (sites != null) {
                		txt.append("A geodata analysis for LoGeoRef20 is conducted.");
                    	//The validateGeodataLoGeoRef20 method is called on the geoLoGeoRef20 object with the given txt and model parameters.
                        //geoLoGeoRef20.validateGeodataLoGeoRef20(txt, model);
                	} else {
                		//Validates the presence of the respective IFC entities and select types for LoGeoRef10
                		IfcBuilding buildings = model.getAll(IfcBuilding.class).stream().findAny().orElse(null);
                		if (buildings != null) {
                			txt.append("A geodata analysis for LoGeoRef10 is conducted.");
                        	//The validateGeodataLoGeoRef10 method is called on the geoLoGeoRef10 object with the given txt and model parameters.
                            //geoLoGeoRef10.validateGeodataLoGeoRef10(txt, model);
                		}
                	}
                }
            }
        }
	}
	
	//Prüfen, ob die SI-Einheiten gesetzt sind

}

