package org.opensourcebim.levelout.checkingservice;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.bimserver.emf.IdEObject;
import org.bimserver.emf.IfcModelInterface;
import org.bimserver.interfaces.objects.SObjectType;
import org.bimserver.interfaces.objects.SProject;
import org.bimserver.interfaces.objects.SVector3f;
import org.bimserver.models.ifc4.IfcMapConversion;
import org.bimserver.models.ifc4.IfcProjectedCRS;
import org.bimserver.plugins.SchemaName;
import org.bimserver.plugins.services.AbstractAddExtendedDataService;
import org.bimserver.plugins.services.BimServerClientInterface;
import org.eclipse.emf.ecore.EStructuralFeature;


public class LevelOutChecking extends AbstractAddExtendedDataService {
    public LevelOutChecking() {
        super(SchemaName.UNSTRUCTURED_UTF8_TEXT_1_0.name());
    }

    @Override
    public void newRevision(RunningService runningService, BimServerClientInterface bimServerClientInterface, long poid, long roid, String userToken, long soid, SObjectType settings) throws Exception {
        SProject project = bimServerClientInterface.getServiceInterface().getProjectByPoid(poid);
        IfcModelInterface model = bimServerClientInterface.getModel(project, roid, true, false, false);
        
        long entities = model.size();
        //String ifcEntities = model.getAllWithSubTypes(model.getPackageMetaData().getEClass("IfcRoot")).toString();
        
        long products = model.getAllWithSubTypes(model.getPackageMetaData().getEClass("IfcProduct")).size();
        //String ifcProducts = model.getAllWithSubTypes(model.getPackageMetaData().getEClass("IfcProduct")).toString();
        
        
        SVector3f mnBnds = model.getModelMetaData().getMinBounds();
        SVector3f mxBnds = model.getModelMetaData().getMaxBounds();
        
        StringBuilder txt = new StringBuilder();
        
        txt.append("Hallo Welt! Ich bin das Prüfroutinen-Plugin :)\n");
        txt.append("-------------------\n");
        txt.append("Current project: ").append(project.getName()).append("\n");
        txt.append("No of entities: ").append(entities).append("\n");
        //txt.append("Names of entities: ").append(ifcEntities).append("\n");
        txt.append("No of products: ").append(products).append("\n");
        //txt.append("Names of products: ").append(ifcProducts).append("\n");
        
        txt.append("\n");
        
        if(mnBnds!= null && mxBnds != null) txt.append("Bounds: " +
                mnBnds.getX() + ", " + mnBnds.getY() + ", " + mnBnds.getZ() + " --- " +
                mxBnds.getX() + ", " + mxBnds.getY() + ", " + mxBnds.getZ() + "\n");
        else txt.append("Bounds: no bounds set\n");
        
        txt.append("\n");
        
        //Überprüfung auf das Vorhandensein von IfcMapConversion
        //String entityInstances = model.getAllWithSubTypes(model.getPackageMetaData().getEClass("IfcProduct")).toString();
        
        //List<Object> entityInstances = (List<Object>) EcoreUtil.getObjectsByType(model.getAllWithSubTypes(model.getPackageMetaData().getEClass("IfcProduct")), model.getPackageMetaData().getEClass("IfcProduct"));
        //txt.append("Folgende Entitäten sind enthalten: ").append(entityInstances).append("\n");
        
        //Collection<IdEObject> entityInstances = model.getObjects();
        //Collection<IdEObject> entityInstances = model.getObjects();
        
        List<IdEObject> entityInstances = model.getAllWithSubTypes(model.getPackageMetaData().getEClass("IfcCoordinateOperation"));
        //txt.append("List of entity instances: ").append(entityInstances).append("\n");
       
        boolean hasIfcMapConversion = false;

        for (IdEObject entityInstance : entityInstances) {
        	if (entityInstance instanceof IfcMapConversion) {
            	hasIfcMapConversion = true;
                break;
            }
        }

        txt.append("hasIfcMapConversion is ").append(hasIfcMapConversion).append("\n");;
        
        if (hasIfcMapConversion) {
            txt.append("IFC model contains IfcMapConversion.").append("\n");
        } else {
            txt.append("IFC model does not contain IfcMapConversion.").append("\n");
        }
        
        txt.append("\n");
        
        //Auslesen von IfcMapConversion und IfcProjectedCRS für die Validierung und Ausgabe der Geodaten
        txt.append("Überprüfung von IfcMapConversion").append("\n");
        
        List<IfcMapConversion> mapConversions = model.getAll(IfcMapConversion.class);
        
        List<String> featureNames = List.of("SourceCRS", "TargetCRS", "Eastings", "Northings", "OrthogonalHeight", "XAxisAbscissa", "XAxisOrdinate", "Scale");
            
        for (IfcMapConversion mapConversion : mapConversions) {
       
        	if (featureNames.stream().anyMatch(featureName -> 
        		mapConversion.eGet(mapConversion.eClass().getEStructuralFeature(featureName)) == null)) {
				txt.append("Die Geodaten im IFC-Modell sind ungültig. Folgende Geodaten fehlen und müssen ergänzt werden: ").append("\n");
				featureNames.stream()
                .filter(featureName -> mapConversion.eGet(mapConversion.eClass().getEStructuralFeature(featureName)) == null)
                .forEach(featureName -> txt.append(featureName + " fehlt\n"));

            } else
				txt.append("Die Geodaten im IFC-Modell sind gültig").append("\n");
            txt.append("\n");
        	
            	
            
        	
        	/*txt.append("SourceCRS: " + mapConversion.getSourceCRS()).append("\n");
            txt.append("TargetCRS: " + mapConversion.getTargetCRS()).append("\n");
            txt.append("Easting: " + mapConversion.getEastings()).append("\n"); //Spielt scheinbar keine Rolle, ob man es als String oder double ausgeben lässt.
            txt.append("Northing: " + mapConversion.getNorthings()).append("\n"); //Spielt scheinbar keine Rolle, ob man es als String oder double ausgeben lässt.
            txt.append("OrthogonalHeight: " + mapConversion.getOrthogonalHeight()).append("\n");
            txt.append("XAxisAbscissa: " + mapConversion.getXAxisAbscissa()).append("\n");
            txt.append("XAxisOrdinate: " + mapConversion.getXAxisOrdinate()).append("\n");
            txt.append("Scale: " + mapConversion.getScale()).append("\n");
            txt.append("\n");
            
            Map<Supplier<Object>, String> fields = new LinkedHashMap<>();
            fields.put(mapConversion::getSourceCRS, "SourceCRS");
            fields.put(mapConversion::getTargetCRS, "TargetCRS");
            fields.put(mapConversion::getEastings, "Eastings");
            fields.put(mapConversion::getNorthings, "Northings");
            fields.put(mapConversion::getOrthogonalHeight, "OrthogonalHeight");
            fields.put(mapConversion::getXAxisAbscissa, "XAxisAbscissa");
            fields.put(mapConversion::getXAxisOrdinate, "XAxisOrdinate");
        	
            if (fields.entrySet().stream().anyMatch(entry -> entry.getKey().get() == null)) {
				txt.append("Die Geodaten im IFC-Modell sind ungültig. Folgende Geodaten fehlen und müssen ergänzt werden: ").append("\n");
				fields.entrySet().stream()
                .filter(entry -> entry.getKey().get() == null)
                .forEach(entry -> txt.append(entry.getValue() + " fehlt\n"));

            } else
				txt.append("Die Geodaten im IFC-Modell sind gültig").append("\n");*/
            txt.append("\n");
        }
        
        txt.append("Überprüfung von IfcProjectedCRS").append("\n");
        
        List<IfcProjectedCRS> projectedCRSs = model.getAll(IfcProjectedCRS.class);
        
        for (IfcProjectedCRS projectedCRS : projectedCRSs) {
            txt.append("Name: " + projectedCRS.getName()).append("\n");
        	txt.append("Description: " + projectedCRS.getDescription()).append("\n");
        	txt.append("GeodeticDatum: " + projectedCRS.getGeodeticDatum()).append("\n");
        	txt.append("VerticalDatum: " + projectedCRS.getVerticalDatum()).append("\n");
        	txt.append("MapProjection: " + projectedCRS.getMapProjection()).append("\n");
        	txt.append("MapZone: " + projectedCRS.getMapZone()).append("\n");
        	txt.append("MapUnit: " + projectedCRS.getMapUnit()).append("\n");
        	txt.append("\n");
            
        	txt.append("List all attribute names of IfcProjectedCRS: " + model.getAll(IfcProjectedCRS.class)).append("\n");
        	txt.append("\n");
        	
            /*if (projectedCRS.getName() == null) {
          	    txt.append("Name fehlt").append("\n");
            }
            if (projectedCRS.getDescription() == null) {
          	    txt.append("Description fehlt").append("\n");
            }
            if (projectedCRS.getGeodeticDatum() == null) {
          	    txt.append("GeodeticDatum fehlt").append("\n");
            }
            if (projectedCRS.getVerticalDatum() == null) {
            	txt.append("VerticalDatum fehlt").append("\n");
            }
            if (projectedCRS.getMapProjection() == null) {
            	txt.append("MapProjection fehlt").append("\n");
            }
            if (projectedCRS.getMapZone() == null) {
                txt.append("MapZone fehlt").append("\n");
            }
            if (projectedCRS.getMapUnit() == null) {
              	txt.append("MapUnit fehlt").append("\n");
            }
            txt.append("\n");*/
            
            Map<Supplier<Object>, String> fields = new LinkedHashMap<>();
            fields.put(projectedCRS::getName, "Name");
            fields.put(projectedCRS::getDescription, "Description");
            fields.put(projectedCRS::getGeodeticDatum, "GeodeticDatum");
            fields.put(projectedCRS::getVerticalDatum, "VerticalDatum");
            fields.put(projectedCRS::getMapProjection, "MapProjection");
            fields.put(projectedCRS::getMapZone, "MapZone");
            fields.put(projectedCRS::getMapUnit, "MapUnit");

            if (fields.entrySet().stream().anyMatch(entry -> entry.getKey().get() == null)) {
				txt.append("Die Geodaten im IFC-Modell sind ungültig. Folgende Geodaten fehlen und müssen ergänzt werden: ").append("\n");
				fields.entrySet().stream()
                .filter(entry -> entry.getKey().get() == null)
                .forEach(entry -> txt.append(entry.getValue() + " fehlt\n"));

            } else
				txt.append("Die Geodaten im IFC-Modell sind gültig").append("\n");
            
            txt.append("\n");
        
        }
        
        addExtendedData(txt.toString().getBytes(), "stats.txt", "Statistics", "text/plain", bimServerClientInterface, roid);
        
    }
}
