package org.opensourcebim.levelout.checkingservice;

import java.nio.charset.StandardCharsets;
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
        long products_No = model.getAllWithSubTypes(model.getPackageMetaData().getEClass("IfcProduct")).size();
        SVector3f mnBnds = model.getModelMetaData().getMinBounds();
        SVector3f mxBnds = model.getModelMetaData().getMaxBounds();
        StringBuilder txt = new StringBuilder();
        txt.append("General statistics\n");
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

		new GeodataValidation().validateGeodata(txt, model);

        addExtendedData(txt.toString().getBytes(StandardCharsets.UTF_8), "stats.txt", "Statistics", "text/plain", bimServerClientInterface, roid);
    }
}
