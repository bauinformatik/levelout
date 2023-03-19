package org.opensourcebim.levelout.checkingservice;

import org.bimserver.interfaces.objects.SObjectType;
import org.bimserver.models.store.ServiceDescriptor;
import org.bimserver.plugins.services.AbstractService;
import org.bimserver.plugins.services.BimServerClientInterface;

public class TestProgressService extends AbstractService {
	@Override
	public void newRevision(RunningService runningService, BimServerClientInterface bimServerClientInterface, long poid, long roid, String userToken, long soid, SObjectType settings) throws Exception {
		runningService.updateProgress(0);
		for(int i=1; i<=100; i++){
			Thread.sleep(100);
			runningService.updateProgress(i);
		}
	}

	@Override
	public void addRequiredRights(ServiceDescriptor serviceDescriptor) {
		// none required
	}
}
