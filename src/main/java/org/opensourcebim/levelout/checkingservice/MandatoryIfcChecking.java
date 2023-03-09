package org.opensourcebim.levelout.checkingservice;

import org.bimserver.emf.IdEObject;
import org.bimserver.emf.IfcModelInterface;
import org.bimserver.interfaces.objects.SObjectType;
import org.bimserver.interfaces.objects.SProject;
import org.bimserver.plugins.SchemaName;
import org.bimserver.plugins.services.AbstractAddExtendedDataService;
import org.bimserver.plugins.services.BimServerClientInterface;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.nio.charset.StandardCharsets;

public class MandatoryIfcChecking extends AbstractAddExtendedDataService {
	public MandatoryIfcChecking() {
		super(SchemaName.UNSTRUCTURED_UTF8_TEXT_1_0.name());
	}

	@Override
	public void newRevision(RunningService runningService, BimServerClientInterface bimServerClientInterface, long poid, long roid, String userToken, long soid, SObjectType settings) throws Exception {
		super.newRevision(runningService, bimServerClientInterface, poid, roid, userToken, soid, settings);
		SProject projectByPoid = bimServerClientInterface.getServiceInterface().getProjectByPoid(poid);
		IfcModelInterface model = bimServerClientInterface.getModel(projectByPoid, roid, true, false);
		StringBuilder txt = new StringBuilder("Mandatory attribute checking\n");
		model.generateMinimalExpressIds();
		for(IdEObject value: model.getUnidentifiedValues()){
			// TODO: defined types
			if(!value.eClass().getEPackage().equals(model.getPackageMetaData().getEPackage())) continue;
		}
		for(IdEObject entity : model.getValues()){
			if(!entity.eClass().getEPackage().equals(model.getPackageMetaData().getEPackage())) continue;
			for(EStructuralFeature feature: entity.eClass().getEAllStructuralFeatures()){
				entity.getExpressId();
				if(!feature.isUnsettable() && !entity.eIsSet(feature)){
					txt.append("mandatory feature not set: " + entity.eClass().getName()+ "." + feature.getName() + " = " + entity.eGet(feature) + " (#" + entity.getExpressId() + "/" + entity.getOid() + ")\n");
				}
				if(feature.isMany()) {
					int size = ((EList<?>) entity.eGet(feature)).size();
					if(feature.getLowerBound() > size || (feature.getUpperBound() != -1 && feature.getUpperBound() < size)) {
						txt.append("many feature not within bounds: " + entity.eClass().getName() + "." + feature.getName() + " = " + size + " vs " + feature.getLowerBound() + "..." + feature.getUpperBound() + " (#" + entity.getExpressId() + "/" + entity.getOid() + ")\n");
					}
				}
			}
		}
		addExtendedData(txt.toString().getBytes(StandardCharsets.UTF_8), "stats.txt", "Statistics", "text/plain", bimServerClientInterface, roid);
	}
}
