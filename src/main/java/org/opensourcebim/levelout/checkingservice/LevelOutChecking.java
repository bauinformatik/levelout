package org.opensourcebim.levelout.checkingservice;

import org.bimserver.emf.IfcModelInterface;
import org.bimserver.interfaces.objects.SObjectType;
import org.bimserver.interfaces.objects.SProject;
import org.bimserver.interfaces.objects.SVector3f;
import org.bimserver.plugins.SchemaName;
import org.bimserver.plugins.services.AbstractAddExtendedDataService;
import org.bimserver.plugins.services.BimServerClientInterface;

public class LevelOutChecking extends AbstractAddExtendedDataService {
    public LevelOutChecking() {
        super(SchemaName.UNSTRUCTURED_UTF8_TEXT_1_0.name());
    }

    @Override
    public void newRevision(RunningService runningService, BimServerClientInterface bimServerClientInterface, long poid, long roid, String userToken, long soid, SObjectType settings) throws Exception {
        SProject project = bimServerClientInterface.getServiceInterface().getProjectByPoid(poid);
        IfcModelInterface model = bimServerClientInterface.getModel(project, roid, true, false, true);
        long entities = model.size();
        long products = model.getAllWithSubTypes(model.getPackageMetaData().getEClass("IfcProduct")).size();
        SVector3f mnBnds = model.getModelMetaData().getMinBounds();
        SVector3f mxBnds = model.getModelMetaData().getMaxBounds();
        StringBuilder txt = new StringBuilder();
        txt.append("Hallo world!\n");
        txt.append("-------------------\n");
        txt.append("Current project: ").append(project.getName()).append("\n");
        txt.append("No of entities: ").append(entities).append("\n");
        txt.append("No of products: ").append(products).append("\n");
        if(mnBnds!= null && mxBnds != null) txt.append("Bounds: " +
                mnBnds.getX() + ", " + mnBnds.getY() + ", " + mnBnds.getZ() + " --- " +
                mxBnds.getX() + ", " + mxBnds.getY() + ", " + mxBnds.getZ() + "\n");
        else txt.append("Bounds: no bounds set\n");
        addExtendedData(txt.toString().getBytes(), "stats.txt", "Statistics", "text/plain", bimServerClientInterface, roid);
    }
}
