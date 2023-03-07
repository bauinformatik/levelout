package org.opensourcebim.levelout.checkingservice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bimserver.emf.IdEObject;
import org.bimserver.emf.IfcModelInterface;
import org.bimserver.interfaces.objects.SObjectType;
import org.bimserver.interfaces.objects.SProject;
import org.bimserver.interfaces.objects.SVector3f;
import org.bimserver.models.ifc4.IfcGeometricRepresentationContext;
import org.bimserver.models.ifc4.IfcMapConversion;
import org.bimserver.models.ifc4.IfcProjectedCRS;
import org.bimserver.models.ifc4.IfcSpace;
import org.bimserver.plugins.SchemaName;
import org.bimserver.plugins.services.AbstractAddExtendedDataService;
import org.bimserver.plugins.services.BimServerClientInterface;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.google.common.collect.BiMap;


public class LevelOutChecking extends AbstractAddExtendedDataService {
    public LevelOutChecking() {
        super(SchemaName.UNSTRUCTURED_UTF8_TEXT_1_0.name());
    }

    @Override
    public void newRevision(RunningService runningService, BimServerClientInterface bimServerClientInterface, long poid, long roid, String userToken, long soid, SObjectType settings) throws Exception {
        SProject project = bimServerClientInterface.getServiceInterface().getProjectByPoid(poid);
        IfcModelInterface model = bimServerClientInterface.getModel(project, roid, true, false, false);
        
        long entities_No = model.size();
        //String ifcEntities = model.getAllWithSubTypes(model.getPackageMetaData().getEClass("IfcRoot")).toString();
        
        long products_No = model.getAllWithSubTypes(model.getPackageMetaData().getEClass("IfcProduct")).size();
        //String ifcProducts = model.getAllWithSubTypes(model.getPackageMetaData().getEClass("IfcProduct")).toString();
        
        
        SVector3f mnBnds = model.getModelMetaData().getMinBounds();
        SVector3f mxBnds = model.getModelMetaData().getMaxBounds();
        
        StringBuilder txt = new StringBuilder();
        
        txt.append("Hallo Welt! Ich bin das Prüfroutinen-Plugin :)\n");
        txt.append("-------------------\n");
        txt.append("Current project: ").append(project.getName()).append("\n");
        txt.append("No of entities: ").append(entities_No).append("\n");
        txt.append("No of products: ").append(products_No).append("\n");
        txt.append("\n");
        
        txt.append("Bounds values: \n");
        if(mnBnds!= null && mxBnds != null) txt.append("Bounds: " +
                mnBnds.getX() + ", " + mnBnds.getY() + ", " + mnBnds.getZ() + " --- " +
                mxBnds.getX() + ", " + mxBnds.getY() + ", " + mxBnds.getZ() + "\n");
        else txt.append("Bounds: no bounds set\n");
        txt.append("\n");
        
        
        //Ausgabe aller Entitäten, die im IFC-Modell enthalten sind
        BiMap<Long, IdEObject> entities = model.getObjects();
        //txt.append("Folgende Entitäten sind enthalten: ").append(entities).append("\n");
        long entities_size = entities.size();
        txt.append("Die Anzahl der Entitäten über die Methode getObjects() beträgt: ").append(entities_size).append("\n");
        txt.append("\n");
        
        Collection<IdEObject> entities2 = model.getValues();
        //txt.append("Folgende Entitäten sind enthalten: ").append(entitiess).append("\n");
        long entitiess_size = entities2.size();
        txt.append("Die Anzahl der Entitäten über die Methode getValues() beträgt: ").append(entitiess_size).append("\n");
        txt.append("\n\n");
 
        
        //Auslesen von IfcMapConversion und IfcProjectedCRS für die Ausgabe und Validierung der Geodaten
        txt.append("Ausgabe und Validierung der Geodaten\n\n");
        //Überprüfung auf das Vorhandensein von IfcMapConversion und IfcProjectedCRS sowie deren Attributwerte
        //Wie kann die folgende Zeile noch generisch umgestaltet werden, damit die Werte, die aus der DB abgefragtwerden sollen auch in diesem String erscheinen.
        //Problem sind die Werte im IFC-Modell nicht vorhanden, werden ise auch nicht in der Liste gespeichert und demnach auch nicht in einem String, der die Liste abfragt.
        txt.append("Überprüfung auf das Vorhandensein von IfcMapConversion, IfcProjectedCRS und IfcGeometricRepresentationContext sowie deren Attributwerte\n");
        List<IdEObject> entities3 = new ArrayList<IdEObject>(
        	Arrays.asList(
        		model.getAll(IfcMapConversion.class).stream().findFirst().orElse(null),//IfcMapConversion
        		model.getAll(IfcMapConversion.class).stream().findFirst().orElse(null).getSourceCRS(),//IfcGeometricRepresentationContext
        		model.getAll(IfcMapConversion.class).stream().findFirst().orElse(null).getTargetCRS()//IfcProjectedCRS
        		//Methode finden zum Abfragen der weiteren Attribute
        		//model.getAll(IfcMapConversion.class).stream().findFirst().orElse(null).getSourceCRS(),
        	)
        );
        for (IdEObject entity : entities3) {
	        if (entity != null) {
	        	EClass eClass = entity.eClass();
	        	EList<EStructuralFeature> eFeatures = eClass.getEAllStructuralFeatures();
	        	txt.append("\n").append("Die Attribute von " + entity.eClass().getName().toString() + " lauten: \n");
	        	for (EStructuralFeature eFeature : eFeatures) {
	        		 String featureName = eFeature.getName();
	        		 Object featureValue = entity.eGet(eFeature);
	        		 txt.append("\t").append(featureName).append(": ").append(featureValue).append("\n");
	        	}
	        	txt.append("\n");
	        	
	        	//Beginn der Prüfbedingungen
	        	//Abspeichern der zu überprüfenden Attribute in Liste validValues mit für die Geolokalisation notwendigen Attribute
	        	List<String> validValues = new ArrayList<>(
	        			Arrays.asList(
	        					"SourceCRS", "TargetCRS", "Eastings","Northings", "OrthogonalHeight", "XAxisAbscissa", "XAxisOrdinate", //IfcMapConversion
	        					"WorldCoordinateSystem", //IfcGeometricRepresentationContext
	        					"GeodeticDatum", "VerticalDatum", "MapProjection", "MapZone", "MapUnit" //IfcProjectedCRS
    					)
	        	);
	        	//Überprüfung1 auf Attributwerte in Liste validValues != null
	        	boolean hasValue = eFeatures.stream()
	        			//Filtern nach den Werten in der Liste validValues
	        			.filter(feature -> validValues.contains(feature.getName()))
	        			.map(feature -> entity.eGet(feature))
	        			//Überprüfung, ob die Werte nicht null sind
	        			.allMatch(value -> value != null);//oder anyMatch()?
	        			//.filter --> Alle rausschreiben, die Fehlerhaft sind, wenn Liste leer alle gültig, wenn etwas drin ist, Fehler vorhanden
	        	//Überprüfung2 auf Gültigkeit der Geodaten, also IFC-Modell in Bezug auf Geodaten gültig oder ungültig
	        	if (hasValue) {
	        	    txt.append("Die erforderlichen Geodaten im IFC-Modell sind gültig.\n");
	        	} else {
	        		//Überprüfung3 auf Notwendigkeit der Ergänzung
	        		txt.append("Die erforderlichen Geodaten im IFC-Modell sind ungültig.\n");
	        		txt.append("Folgende Geodaten müssen ergänzt werden: \n");
	        	    eFeatures.stream()
	        	    .filter(feature -> entity.eGet(feature) == null)
	        	    .forEach(feature -> txt.append("\t").append(feature.getName()).append(" fehlt\n"));
	        	}
	        	
	        	
	        	/*boolean hasNullValue = eFeatures.stream()
	        			//Filtern für die Ausnahme für Überprüfung3
	        			.filter(feature -> !(feature.getName() == "ScaleAsString") || "ContextIdentifier") // Ausnahme für ScaleAsString
	        			.map(feature -> entity.eGet(feature)).anyMatch(value -> value == null);
	        	if (hasNullValue) {
	        		//Überprüfung2 auf Gültigkeit der Geodaten, also IFC-Modell in Bezug auf Geodaten gültig oder ungültig
	        	    txt.append("Die Geodaten im IFC-Modell sind ungültig.\n");
	        		txt.append("Folgende Geodaten müssen ergänzt werden: \n");
	        	    eFeatures.stream()
	        	    //Überprüfung3 auf Notwendigkeit der Ergänzung
	        	    	.filter(feature -> !feature.getName().equals("ScaleAsString") && entity.eGet(feature) == null)
	        	        .forEach(feature -> txt.append("\t").append(feature.getName()).append(" fehlt\n"));
	        	} else {
	        		txt.append("Die erforderlichen Geodaten im IFC-Modell sind gültig").append("\n");
	        	}*/
	        	
	        } else {
	        	txt.append("Die Entität IfcMapConversion, IfcProjectedCRS oder IfcGeometricRepresentationContext fehlt im IFC-Modell.\n");
	        }
        }
	        txt.append("\n\n");

	        
	    //Auslesen von IfcSpace für die Ausgabe und Validierung des geometrischen Konzepts
	    txt.append("Ausgabe und Validierung des geometrischen Konzepts\n\n");
	    List<IdEObject> entities4 = new ArrayList<>(
	    	    Stream.concat(
	    	        model.getAll(IfcSpace.class).stream().findFirst().stream(),
	    	        model.getAll(IfcSpace.class).stream().findFirst().stream()
	    	            .flatMap(space -> space.getRepresentation().getRepresentations().stream())
	    	            .flatMap(rep -> rep.getItems().stream())
	    	    ).collect(Collectors.toList())
	    	);
	    Optional<IdEObject> entitycheck2 = entities4.stream().filter(entity -> entity instanceof IfcSpace).findFirst();
	    if (entitycheck2.isPresent()) {
	    	for (IdEObject entity : entities4) {
	    		EClass eClass = entity.eClass();
	        	EList<EStructuralFeature> eFeatures = eClass.getEAllStructuralFeatures();
	        	//Überprüfung auf das Vorhandensein von IfcSpace und dem zugrundeliegenden geometrischen Konzept
	        	txt.append("Überprüfung auf das Vorhandensein von " + entity.eClass().getName().toString() + " und dem zugrundeliegenden geometrischen Konzept\n");
	        	txt.append("\n").append("Die Attribute von " + entity.eClass().getName().toString() + " lauten: \n");
	        	for (EStructuralFeature eFeature : eFeatures) {
	        		 String featureName = eFeature.getName();
	        		 Object featureValue = entity.eGet(eFeature);
	        		 txt.append("\t").append(featureName).append(": ").append(featureValue).append("\n");
	        	}
	        	txt.append("\n");
	        //Beginn der Prüfbedingungen
	        	//Überprüfung1 auf null-Attributwerte
	        	boolean hasNullValue = eFeatures.stream().map(feature -> entity.eGet(feature)).anyMatch(value -> value == null);
	        	if (hasNullValue) {
	        		txt.append("Die Daten von " + entity.eClass().getName().toString() + " im IFC-Modell sind ungültig. Folgende Daten von " + entity.eClass().getName().toString() + " fehlen und müssen ergänzt werden: \n");
	        	    eFeatures.stream()
	        	    	.filter(feature -> entity.eGet(feature) == null)
	        	        .forEach(feature -> txt.append("\t").append(feature.getName()).append(" fehlt\n"));
	        	} else {
	        		txt.append("Die Daten von " + entity.eClass().getName().toString() + " im IFC-Modell sind gültig").append("\n");
	        	}
	    	}
	    } else {
	    	txt.append("Die Entität " + entitycheck2.toString() + " fehlt im IFC-Modell.\n");
	    }
	        txt.append("\n");

	        
	        
        addExtendedData(txt.toString().getBytes(), "stats.txt", "Statistics", "text/plain", bimServerClientInterface, roid);
        
    }
}
