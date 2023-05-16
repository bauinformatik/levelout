package org.opensourcebim.levelout.checkingservice;

import org.bimserver.interfaces.objects.SObjectType;
import org.bimserver.plugins.SchemaName;
import org.bimserver.plugins.services.AbstractAddExtendedDataService;
import org.bimserver.plugins.services.BimServerClientInterface;

import java.nio.charset.StandardCharsets;

public class TestProgressService extends AbstractAddExtendedDataService {
	public TestProgressService() {
		super(SchemaName.UNSTRUCTURED_UTF8_TEXT_1_0.name());
	}
	@Override
	public void newRevision(RunningService runningService, BimServerClientInterface bimServerClientInterface, long poid, long roid, String userToken, long soid, SObjectType settings) throws Exception {
		runningService.updateProgress(0);
		for(int i=1; i<=100; i++){
			Thread.sleep(100);
			runningService.updateProgress(i);
		}
		addExtendedData("This is just a test service. It does not actually check anything.\nBut if you see this, you know that some things work at least.".getBytes(StandardCharsets.UTF_8), "result.txt", "Service execution result", "text/plain", bimServerClientInterface, roid);
	}

	@Override
	public ProgressType getProgressType() {
		return ProgressType.KNOWN;
	}

}
