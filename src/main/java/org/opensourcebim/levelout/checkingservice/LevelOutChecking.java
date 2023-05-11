package org.opensourcebim.levelout.checkingservice;


import org.bimserver.emf.IfcModelInterface;
import org.bimserver.interfaces.objects.SObjectType;
import org.bimserver.interfaces.objects.SProject;
import org.bimserver.interfaces.objects.SVector3f;
import org.bimserver.plugins.SchemaName;
import org.bimserver.plugins.services.AbstractAddExtendedDataService;
import org.bimserver.plugins.services.BimServerClientInterface;

//Class LevelOutChecking inherit from the class AbstractAddExtendedDataService
public class LevelOutChecking extends AbstractAddExtendedDataService {
	
	// Declaration and initialization of an instance variable named geoVal of type GeodataValidation
	private GeodataValidation geoVal = new GeodataValidation();
	
	//Constructor LevelOutChecking initialize the parent class AbstractAddExtendedDataService with the schema name
	public LevelOutChecking() {
        super(SchemaName.UNSTRUCTURED_UTF8_TEXT_1_0.name());
        
	}

    @Override
    //Central method of the class LevelOutChecking
    public void newRevision(RunningService runningService, BimServerClientInterface bimServerClientInterface, long poid, long roid, String userToken, long soid, SObjectType settings) throws Exception {
        //Current project and the associated IFC model (IfcModelInterface) are first retrieved via SProject
    	SProject project = bimServerClientInterface.getServiceInterface().getProjectByPoid(poid);
        IfcModelInterface model = bimServerClientInterface.getModel(project, roid, true, false, false);
        
        //Information about the number of general IFC entities and more specifically about all IFC entities that can be grouped under IfcProduct are extracted
        long entities_No = model.size();
        long products_No = model.getAllWithSubTypes(model.getPackageMetaData().getEClass("IfcProduct")).size();
        
        //Minimum and maximum bounds of the IFC model (SVector3f) are retrieved
        SVector3f mnBnds = model.getModelMetaData().getMinBounds();
        SVector3f mxBnds = model.getModelMetaData().getMaxBounds();
        
        //Creates the stringBuilder txt
        StringBuilder txt = new StringBuilder();
        
        //Added the previously extracted information to the StringBuilder txt
        //Title
        txt.append("Checking Report\n");
        txt.append("-------------------\n");
        //Project information
        txt.append("Current project: ").append(project.getName()).append("\n");
        txt.append("No of IFC entities: ").append(entities_No).append("\n");
        txt.append("No of IfcProducts: ").append(products_No).append("\n");
        txt.append("\n");
        
        //Output of bounds values
        txt.append("Bounds values: \n");
        if(mnBnds!= null && mxBnds != null) txt.append("Bounds: " +
                mnBnds.getX() + ", " + mnBnds.getY() + ", " + mnBnds.getZ() + " --- " +
                mxBnds.getX() + ", " + mxBnds.getY() + ", " + mxBnds.getZ() + "\n");
        else txt.append("Bounds: no bounds set\n");
        txt.append("-------------------\n");
 
        //Title: Output and validation of the geodata
        txt.append("Output and validation of geodata\n\n");
        
        //Text: Check for the presence of IfcMapConversion, IfcProjectedCRS and IfcGeometricRepresentationContext and their attribute values
        txt.append("Check for the presence of the following IFC enitities and their attributes:\n" + "\tIfcProject,\n" + "\tIfcMapConversion,\n" + "\tIfcProjectedCRS,\n" + "\tIfcGeometricRepresentationContext\n\n"); 
        
        //The validateGeodata method is called on the geoVal object with the given txt and model parameters.
        geoVal.validateGeodata(txt, model);
        
        //The method addExtendedData() is called to add the created test report as extended data to the BIMserver project. The check report is stored in stats.txt.
        addExtendedData(txt.toString().getBytes(), "stats.txt", "Statistics", "text/plain", bimServerClientInterface, roid);
    }
}