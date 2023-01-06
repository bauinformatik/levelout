package org.opensourcebim.levelout;

import org.bimserver.*;
import org.bimserver.interfaces.objects.SExtendedDataSchema;
import org.bimserver.interfaces.objects.SProject;
import org.bimserver.models.log.AccessMethod;
import org.bimserver.plugins.services.BimServerClientInterface;
import org.bimserver.resources.ClasspathResourceFetcher;
import org.bimserver.shared.ChannelConnectionException;
import org.bimserver.shared.exceptions.BimServerClientException;
import org.bimserver.shared.exceptions.ServiceException;
import org.bimserver.webservices.ServiceMap;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CheckingServiceTest {
	@Test
	public void test() throws IOException, ServiceException, ChannelConnectionException, BimServerClientException {
		ClasspathResourceFetcher resourceFetcher = new ClasspathResourceFetcher();
		Path plugin = Paths.get(".");
		LocalDevBimServerStarterJar bimserver = new LocalDevBimServerStarterJar();
		bimserver.start(-1, "localhost", "bimserver", 8082, 8085,
				new Path[]{plugin}, Paths.get("home"),
				resourceFetcher, null,true
		);
		ServiceMap serviceMap = bimserver.getBimServer().getServiceFactory().get(AccessMethod.INTERNAL);
		serviceMap.getServiceInterface();
		serviceMap.getAuthInterface().login("admin@bimserver.org", "admin");
		BimServerClientInterface client = bimserver.getBimServer().getBimServerClientFactory().create();
		// TODO if no project with revisions, upload
		client.getAuthInterface().login("admin@bimserver.org", "admin");
		for(SProject project :  client.getServiceInterface().getAllProjects(true, true)){
			long serviceId = client.getServiceInterface().getAllServicesOfProject(project.getOid()).get(0).getOid();
			client.getServiceInterface().triggerNewRevision(project.getLastRevisionId(), serviceId);
			SExtendedDataSchema schema = client.getServiceInterface().getExtendedDataSchemaByName("..."); // TODO data schema name
			long extendedDataId = client.getServiceInterface().getLastExtendedDataOfRevisionAndSchema(project.getLastRevisionId(), schema.getOid()).getOid();
			client.downloadExtendedData(extendedDataId, System.out);
			// client.download(project.getLastRevisionId(), serializerId, System.out);
		}

	}


}
