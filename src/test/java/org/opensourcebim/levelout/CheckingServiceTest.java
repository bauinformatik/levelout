package org.opensourcebim.levelout;

import org.apache.commons.io.FileUtils;
import org.bimserver.*;
import org.bimserver.database.DatabaseRestartRequiredException;
import org.bimserver.database.berkeley.DatabaseInitException;
import org.bimserver.interfaces.objects.*;
import org.bimserver.models.log.AccessMethod;
import org.bimserver.resources.ClasspathResourceFetcher;
import org.bimserver.shared.exceptions.*;
import org.bimserver.webservices.ServiceMap;
import org.bimserver.webservices.authorization.SystemAuthorization;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.activation.DataHandler;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class CheckingServiceTest {
	boolean online = false;
	boolean clean = false;
	String home = "tmptestdata/home";

	@Before
	public void setupBimServerHome() throws IOException, ServiceException, DatabaseInitException, BimserverDatabaseException, PluginException, DatabaseRestartRequiredException, InterruptedException {
		Path homePath = Paths.get(this.home);
		if(!clean && homePath.toFile().exists()) return;
		if(homePath.toFile().isDirectory())
			FileUtils.deleteDirectory(homePath.toFile());
		else
			Files.deleteIfExists(homePath);
		BimServer bimServer = createBimServer(homePath);
		bimServer.start();
		ServiceMap serviceMap = bimServer.getServiceFactory().get(new SystemAuthorization(1, TimeUnit.HOURS), AccessMethod.INTERNAL);
		serviceMap.getAdminInterface().setup("http://localhost:7010", "Test Name", "Test Description", "noicon", "Administrator", "admin@localhost", "admin");
		serviceMap.getSettingsInterface().setCacheOutputFiles(false);
		serviceMap.getSettingsInterface().setPluginStrictVersionChecking(false);
		serviceMap.getSettingsInterface().setSiteAddress("http://localhost:7010");
		ServiceMap services = bimServer.getServiceFactory().get(serviceMap.getAuthInterface().login("admin@localhost", "admin"), AccessMethod.INTERNAL);
		services.getPluginInterface().installPluginBundle("https://repo1.maven.org/maven2/", "org.opensourcebim", "ifcplugins", null, null);
		services.getPluginInterface().installPluginBundle("https://repo1.maven.org/maven2/", "org.opensourcebim", "binaryserializers", null, null);
		services.getPluginInterface().installPluginBundle("https://repo1.maven.org/maven2/", "org.opensourcebim", "bimviews", null, null);
		if (online)
			services.getPluginInterface().installPluginBundle("https://repo1.maven.org/maven2/", "org.opensourcebim", "ifcopenshellplugin", null, null);
		else {
			Optional<SRenderEnginePluginConfiguration> nopRenderEngine = services.getPluginInterface().getAllRenderEngines(true).stream().filter((re) -> re.getName().equals("NOP Render Engine")).findFirst();
			services.getPluginInterface().setDefaultRenderEngine(nopRenderEngine.get().getOid());
		}
		SExtendedDataSchema s = new SExtendedDataSchema();
		s.setName("UNSTRUCTURED_UTF8_TEXT_1_0");
		s.setContentType("text/plain");
		s.setUrl("");
		s.setDescription("Generic text");
		services.getServiceInterface().addExtendedDataSchema(s);
		SProject sProject = services.getServiceInterface().addProject("test", "IFC4");
		LocalDevPluginLoader.loadPlugins(bimServer.getPluginBundleManager(), new Path[]{Path.of(".")});
		bimServer.activateServices();
		Optional<SServiceDescriptor> levelOut = services.getServiceInterface().getAllLocalServiceDescriptors().stream().filter((serviceDescriptor -> serviceDescriptor.getName().startsWith("LevelOut"))).findFirst();
		if (levelOut.isEmpty()) Assert.fail("service not found");
		SServiceDescriptor serviceDescriptor = levelOut.get();
		SProfileDescriptor profile = services.getServiceInterface().getAllLocalProfiles(serviceDescriptor.getIdentifier()).get(0);
		SExtendedDataSchema writeExtendedDataSchema = services.getServiceInterface().getExtendedDataSchemaByName(serviceDescriptor.getWriteExtendedData());
		SService service = createService(serviceDescriptor, profile, writeExtendedDataSchema);
		services.getServiceInterface().addLocalServiceToProject(sProject.getOid(), service, Long.valueOf(service.getProfileIdentifier()));
		bimServer.stop();
	}

	@Test
	public void testTwoStoreySPF() throws DatabaseInitException, BimserverDatabaseException, ServerException, PluginException, DatabaseRestartRequiredException, InterruptedException, UserException, IOException {
		Path home = Paths.get(this.home);
		Assert.assertTrue("setup home at " + this.home, home.toFile().exists());
		BimServer bimServer= createBimServer(home);
		bimServer.start();
		LocalDevPluginLoader.loadPlugins(bimServer.getPluginBundleManager(), new Path[]{Path.of(".")});
		bimServer.activateServices();
		// we might need to wait until plugins are loaded
		String token = bimServer.getServiceFactory().get(AccessMethod.INTERNAL).getAuthInterface().login("admin@localhost", "admin");
		ServiceMap services = bimServer.getServiceFactory().get(token, AccessMethod.INTERNAL);
		SProject sProject = services.getServiceInterface().getProjectsByName("test").get(0);
		SDeserializerPluginConfiguration ifcDeserializer = services.getServiceInterface().getSuggestedDeserializerForExtension("ifc", sProject.getOid());
		URL resource = getClass().getClassLoader().getResource("20221020_two-storey-residential-building_face-based-surface.ifc");
		Assert.assertNotNull(resource);
		File fileResource = new File(resource.getFile());
		SLongCheckinActionState checkin = services.getServiceInterface().checkinSync(sProject.getOid(), "checkin", ifcDeserializer.getOid(), fileResource.length(), fileResource.getName(), new DataHandler(resource), false);
		Assert.assertNotEquals(-1, checkin.getRoid());
		sProject = services.getServiceInterface().getProjectsByName("test").get(0);
		Thread.sleep(2000); // service execution might take shorter or longer TODO register and be notified
		Assert.assertEquals(3, services.getServiceInterface().getAllExtendedDataOfRevision(sProject.getLastRevisionId()).size());
		long extendedDataId = services.getServiceInterface().getExtendedDataSchemaByName("UNSTRUCTURED_UTF8_TEXT_1_0").getOid();
		SExtendedData extendedData = services.getServiceInterface().getLastExtendedDataOfRevisionAndSchema(sProject.getLastRevisionId(), extendedDataId);
		Assert.assertNotNull(extendedData);
		System.out.write(services.getServiceInterface().getFile(extendedData.getFileId()).getData());
		bimServer.stop();
	}

	private BimServer createBimServer(Path home) {
		BimServerConfig config = new BimServerConfig();
		config.setHomeDir(home);
		config.setStartEmbeddedWebServer(true);
		config.setPort(7010);
		config.setResourceFetcher(new ClasspathResourceFetcher());
		config.setClassPath(System.getProperty("java.class.path"));
		BimServer bimServer = new BimServer(config);
		bimServer.setEmbeddedWebServer(new EmbeddedWebServer(bimServer, null, false));
		return bimServer;
	}

	private static SService createService(SServiceDescriptor serviceDescriptor, SProfileDescriptor profile, SExtendedDataSchema writeExtendedDataSchema) {
		SService service = new SService();
		service.setName(serviceDescriptor.getName()+ new Random().nextInt(1000)); // This must be unique in project
		service.setProviderName(serviceDescriptor.getProviderName());
		service.setServiceName(serviceDescriptor.getName());
		service.setServiceIdentifier(serviceDescriptor.getIdentifier());
		service.setUrl(serviceDescriptor.getUrl());
		service.setToken(serviceDescriptor.getToken());
		service.setNotificationProtocol(serviceDescriptor.getNotificationProtocol());
		service.setDescription(serviceDescriptor.getDescription());
		service.setTrigger(serviceDescriptor.getTrigger());
		service.setProfileIdentifier(profile.getIdentifier());
		service.setProfileName(profile.getName());
		service.setProfileDescription(profile.getDescription());
		service.setProfilePublic(profile.isPublicProfile());
		service.setReadRevision(serviceDescriptor.isReadRevision());
		service.setWriteRevisionId(-1);
		service.setWriteExtendedDataId(writeExtendedDataSchema.getOid());
		return service;
	}


}
