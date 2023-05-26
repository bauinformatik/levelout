package org.opensourcebim.levelout.checkingservice;

import java.util.Optional;

import org.bimserver.emf.IfcModelInterface;
import org.bimserver.models.ifc4.IfcContext;
import org.bimserver.models.ifc4.IfcCoordinateOperation;
import org.bimserver.models.ifc4.IfcCoordinateReferenceSystem;
import org.bimserver.models.ifc4.IfcDimensionCount;
import org.bimserver.models.ifc4.IfcElementCompositionEnum;
import org.bimserver.models.ifc4.IfcGeometricRepresentationContext;
import org.bimserver.models.ifc4.IfcMapConversion;
import org.bimserver.models.ifc4.IfcNamedUnit;
import org.bimserver.models.ifc4.IfcProject;
import org.bimserver.models.ifc4.IfcProjectedCRS;
import org.bimserver.models.ifc4.IfcRepresentationContext;
import org.bimserver.models.ifc4.IfcSite;
import org.bimserver.models.ifc4.IfcUnit;
import org.bimserver.models.ifc4.IfcUnitEnum;
import org.eclipse.emf.common.util.EList;

public class GeodataValidation {

	// Declaration and initialization of instance variables
	private GeodataLoGeoRef geodataLoGeoRef = new GeodataLoGeoRef();
	private GeodataLoGeoRef50 geodataLoGeoRef50 = new GeodataLoGeoRef50();
	private GeodataLoGeoRef20_40 geodataLoGeoRef20_40 = new GeodataLoGeoRef20_40();
	
	public void validateGeodata(StringBuilder txt, IfcModelInterface model) {
		txt.append("Geodata Validation\n-------------------\n");
		geodataLoGeoRef.validateGeodataLoGeoRef(txt, model);
		//geodataLoGeoRef.validateProjects(projects, txt, model);
		//geodataLoGeoRef.validateSites(sites, txt, model);
		//Validates the presence of the respective IFC entities and select types for Variant 1 (LoGeoRef50)
		IfcProject project = model.getAll(IfcProject.class).stream().findAny().orElse(null);
		if (project != null) {
			IfcMapConversion mapConversion = model.getAll(IfcMapConversion.class).stream().findAny().orElse(null);
			IfcProjectedCRS projectedCRS = model.getAll(IfcProjectedCRS.class).stream().findAny().orElse(null);
			if (mapConversion != null || projectedCRS != null) {
	        	txt.append("A geodata analysis for LoGeoRef50 is conducted." + "\n");
	        	txt.append("Check for the presence of the following IFC entities and their attributes:\n"
	        			+ "\tIfcProject,\n" + "\tIfcMapConversion,\n" + "\tIfcProjectedCRS,\n" + "\tIfcGeometricRepresentationContext\n\n");
	        	geodataLoGeoRef50.validateGeodataLoGeoRef50(txt, model);
	    	} else {
	    		//Validates the presence of the respective IFC entities and select types for Variant 2 (LoGeoRef20+40)
	    		
	    		//Auxiliary code
	    		IfcSite sites = model.getAll(IfcSite.class).stream().findAny().orElse(null);
	    			
	    		
	        }
		} else {
			txt.append("A geodata analysis is impossible. The IfcProject entity is missing from the IFC model.");
		}
		
		
		/*
		 * //Alternative procedure checks presence via relations - in construction
        IfcProject project = model.getAll(IfcProject.class).stream().findAny().orElse(null);
		if (project != null) {
			EList<IfcRepresentationContext> eListRepresentationContext = project.getRepresentationContexts();
			IfcGeometricRepresentationContext geometricRepresentationContext = eListRepresentationContext.stream()
					.filter(feature -> feature instanceof IfcGeometricRepresentationContext && "Model".equals(feature.getContextType()))
					.map(feature -> (IfcGeometricRepresentationContext) feature)
					.findFirst()
					.orElse(null);
			if(geometricRepresentationContext != null) {
				EList<IfcCoordinateOperation> eListCoordinateOperation = geometricRepresentationContext.getHasCoordinateOperation();
				IfcMapConversion mapConversion = eListCoordinateOperation.stream()
						.filter(feature -> feature instanceof IfcMapConversion)
						.map(feature -> (IfcMapConversion) feature)
						.findFirst()
						.orElse(null);
				if (mapConversion != null) {
				IfcCoordinateReferenceSystem coordinateReferenceSystem = mapConversion.getTargetCRS();
					if (coordinateReferenceSystem != null) {
						txt.append("A geodata analysis for LoGeoRef50 is conducted." + "\n");
			        	txt.append("Check for the presence of the following IFC entities and their attributes:\n"
			        		+ "\tIfcProject\n" + "\tIfcGeometricRepresentationContext\n" + "\tIfcMapConversion\n" + "\tIfcProjectedCRS\n\n");
			        	geoLoGeoRef50.validateGeodataLoGeoRef50(txt, model);
					} else {
						txt.append("The IfcCoordinateReferenceSystem entity is missing in the SPF file.");
						//Validates the presence of the respective IFC entities and select types for Variant 2 (LoGeoRef20+40)
	    				IfcSite sites = model.getAll(IfcSite.class).stream().findAny().orElse(null);
	    				IfcGeometricRepresentationContext geometricRepresentationContext = model.getAll(IfcGeometricRepresentationContext.class).stream()
	    						.filter(feature -> feature instanceof IfcGeometricRepresentationContext && "Model".equals(feature.getContextType()))
								.map(feature -> (IfcGeometricRepresentationContext) feature)
								.findFirst()
								.orElse(null);
	    			if (sites != null || geometricRepresentationContext != null) {
	        			txt.append("A geodata analysis for Variant 2 (LoGeoRef20+40) is conducted." + "\n");
	        			txt.append("Check for the presence of the following IFC entities and their attributes:\n"
	        				 + "\tIfcSite\n" + "IfcGeometricRepresentationContext\n\n");
	                geoLoGeoRef20_40.validateGeodataLoGeoRef20_40(txt, model);
				
				} else {
					txt.append("The IfcMapConversion entity is missing in the SPF file.");
				}
				
	    	} else {
	    		txt.append("A geodata analysis for LoGeoRef50 or LoGeoRef20 is impossible.");
	    	}
			} else {
				txt.append("The IfcGeometricRepresentationContext entity for the model Context Type is missing in the SPF file. No statement can be made about the analysis method to be used for geodata.");
			}
		} else {
			txt.append("A geodata analysis is impossible. The IfcProject entity is missing from the IFC model.");
		}
    	*/
	}
}