package org.mouji.ns.test;

import java.io.FileNotFoundException;
import java.net.Inet4Address;
import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mouji.common.errors.DatabaseNotSupported;
import org.mouji.common.info.NameServerInfo;
import org.mouji.common.info.SerializationFormat;
import org.mouji.common.info.ServiceInfo;
import org.mouji.common.info.ServiceProviderInfo;
import org.mouji.common.info.ServiceSupportInfo;
import org.mouji.common.info.StubEnvInfo;
import org.mouji.common.services.NameServerServices;
import org.mouji.ns.core.server.NameServer;
import org.mouji.stub.java.stubs.ClientStub;
import org.mouji.stub.java.stubs.NSClient;

import me.salimm.allconfig.core.errors.PrefixNotANestedConfigException;
import me.salimm.allconfig.core.types.XMLConfig;

public class NameServerTest implements NameServerServices {

	private NameServer ns;

	private ServiceProviderInfo nsSPInfo;

	private NameServerInfo nsInfo;

	private final ServiceProviderInfo sampleProvider = new ServiceProviderInfo("test", 5, StubEnvInfo.currentEnvInfo());

	@Before
	public void runNS() throws ClassNotFoundException, FileNotFoundException, SQLException, DatabaseNotSupported,
			PrefixNotANestedConfigException, Exception {
		ns = new NameServer(new XMLConfig("conf.xml"));
		ns.start();
		ns.reset();

		nsSPInfo = new ServiceProviderInfo(Inet4Address.getLocalHost().getHostAddress(), 6161,
				StubEnvInfo.currentEnvInfo());
		nsInfo = new NameServerInfo(nsSPInfo.getURL(), nsSPInfo.getPort());
	}

	@Test
	public void testPing() throws Exception {
		ClientStub client = new ClientStub(new ArrayList<>());
		client.ping(nsSPInfo);
	}

	@Test
	public void testInfo() throws Exception {
		ClientStub client = new ClientStub(new ArrayList<>());
		ServiceProviderInfo info = client.getInfo("localhost", 6161);
		Assert.assertEquals(nsSPInfo, info);
	}

	@Test
	public void testSupportedService() throws Exception {
		ClientStub client = new ClientStub(new ArrayList<>());
		ServiceSupportInfo info = client.getServiceSupport(nsSPInfo, NameServerServices.REGISTER);
		Assert.assertNotNull(info);

		Assert.assertEquals(NameServerServices.REGISTER, info.getService());
		Assert.assertEquals(nsSPInfo, info.getProvider());
		Assert.assertEquals(1, info.getSerializers().length);
	}

	@Test
	public void testRegister() throws Exception {
		NSClient client = new NSClient(nsInfo, new ArrayList<>());
		boolean flag = client.register(new ServiceSupportInfo(REGISTER, sampleProvider,
				new SerializationFormat[] { SerializationFormat.defaultFotmat() }));

		Assert.assertEquals(true, flag);
	}

	@Test
	public void testGetProvider() throws Exception {
		ServiceInfo<Integer> service = new ServiceInfo<Integer>("test", 1);

		NSClient client = new NSClient(nsInfo, new ArrayList<>());
		boolean flag = client.register(new ServiceSupportInfo(service, sampleProvider,
				new SerializationFormat[] { SerializationFormat.defaultFotmat() }));
		Assert.assertEquals(true, flag);

		ServiceSupportInfo provider = client.getProvider(service);

		Assert.assertEquals(service, provider.getService());

		Assert.assertEquals(sampleProvider, provider.getProvider());

		Assert.assertEquals(1, provider.getSerializers().length);
		Assert.assertEquals(SerializationFormat.defaultFotmat().getName(), provider.getSerializers()[0].getName());
		Assert.assertEquals(SerializationFormat.defaultFotmat().getVersion(),
				provider.getSerializers()[0].getVersion());

	}

	@Test
	public void testGetProviders() throws Exception {
		int[] ports = new int[] { 1, 2, 3 };

		ServiceInfo<Integer> service = new ServiceInfo<Integer>("test", 1);
		ServiceProviderInfo provider1 = new ServiceProviderInfo("tes1", ports[0], StubEnvInfo.currentEnvInfo());
		ServiceProviderInfo provider2 = new ServiceProviderInfo("test2", ports[1], StubEnvInfo.currentEnvInfo());
		ServiceProviderInfo provider3 = new ServiceProviderInfo("test3", ports[2], StubEnvInfo.currentEnvInfo());

		NSClient client = new NSClient(nsInfo, new ArrayList<>());
		boolean flag = client.register(new ServiceSupportInfo(service, provider1,
				new SerializationFormat[] { SerializationFormat.defaultFotmat() }));
		boolean flag2 = client.register(new ServiceSupportInfo(service, provider2,
				new SerializationFormat[] { SerializationFormat.defaultFotmat() }));
		boolean flag3 = client.register(new ServiceSupportInfo(service, provider3,
				new SerializationFormat[] { SerializationFormat.defaultFotmat() }));

		Assert.assertEquals(true, flag & flag2 & flag3);

		ServiceSupportInfo[] providers = client.getProviders(service);

		Assert.assertEquals(3, providers.length);

//		Assert.assertEquals(sampleProvider, provider.getProvider());

//		Assert.assertEquals(1, provider.getSerializers().length);
//		Assert.assertEquals(SerializationFormat.defaultFotmat().getName(), provider.getSerializers()[0].getName());
//		Assert.assertEquals(SerializationFormat.defaultFotmat().getVersion(),
//				provider.getSerializers()[0].getVersion());

	}

	@After
	public void stopNS() throws Exception {
		ns.stop();
	}

}
