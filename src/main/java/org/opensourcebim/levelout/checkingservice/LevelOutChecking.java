package org.opensourcebim.levelout.checkingservice;


import org.bimserver.emf.IfcModelInterface;
import org.bimserver.interfaces.objects.SObjectType;
import org.bimserver.interfaces.objects.SProject;
import org.bimserver.interfaces.objects.SVector3f;
import org.bimserver.plugins.SchemaName;
import org.bimserver.plugins.services.AbstractAddExtendedDataService;
import org.bimserver.plugins.services.BimServerClientInterface;

public class LevelOutChecking extends AbstractAddExtendedDataService {

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
        txt.append("--------------------------------------\n");
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
        txt.append("--------------------------------------\n" + "--------------------------------------\n");
        
        new GeodataValidation(txt).validateGeodata(model);
        txt.append("--------------------------------------\n" + "--------------------------------------\n");
        
        new StoreyValidation(txt).validateStorey(model);
        txt.append("--------------------------------------\n" + "--------------------------------------\n");
        
        new SpaceBoundaryValidation(txt).validateSpaceBoundary(model);
        txt.append("--------------------------------------\n" + "--------------------------------------\n");
        
        new SpaceValidation(txt).validateSpace(model);
        txt.append("--------------------------------------\n" + "--------------------------------------\n");

        //The method addExtendedData() is called to add the created test report as extended data to the BIMserver project. The check report is stored in stats.txt.
        addExtendedData(txt.toString().getBytes(), "stats.txt", "Statistics", "text/plain", bimServerClientInterface, roid);
    }
}