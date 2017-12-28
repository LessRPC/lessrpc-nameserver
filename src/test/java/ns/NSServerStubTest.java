package test.java.ns;

import java.io.FileNotFoundException;
import java.net.Inet4Address;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lessrpc.common.errors.ApplicationSpecificErrorException;
import org.lessrpc.common.errors.DatabaseNotSupported;
import org.lessrpc.common.errors.ExecuteInternalError;
import org.lessrpc.common.errors.InvalidArgsException;
import org.lessrpc.common.errors.ServiceNotSupportedException;
import org.lessrpc.common.info.EnvironmentInfo;
import org.lessrpc.common.info.SerializationFormat;
import org.lessrpc.common.info.SerializedObject;
import org.lessrpc.common.info.ServiceInfo;
import org.lessrpc.common.info.ServiceProviderInfo;
import org.lessrpc.common.info.ServiceRequest;
import org.lessrpc.common.info.ServiceSupportInfo;
import org.lessrpc.common.info.responses.ServiceResponse;
import org.lessrpc.common.serializer.Serializer;
import org.lessrpc.common.services.ServiceProvider;
import org.lessrpc.ns.core.server.NameServer;
import org.lessrpc.stub.java.serializer.JsonSerializer;
import org.lessrpc.stub.java.stubs.NSClient;
import org.lessrpc.stub.java.stubs.NSServerStub;

import me.salimm.allconfig.core.errors.PrefixNotANestedConfigException;
import me.salimm.allconfig.core.types.XMLConfig;

public class NSServerStubTest {
	private NameServer ns;

	private ServiceProviderInfo nsSPInfo;
	private ServiceProviderInfo spInfo;

	
	

	@Before
	public void runNS() throws ClassNotFoundException, FileNotFoundException, SQLException, DatabaseNotSupported,
			PrefixNotANestedConfigException, Exception {
		ns = new NameServer(new XMLConfig("conf.xml"));
		ns.start();
		ns.reset();

		nsSPInfo = new ServiceProviderInfo(Inet4Address.getLocalHost().getHostAddress(), 6161,
				EnvironmentInfo.currentEnvInfo());
		
		spInfo = new ServiceProviderInfo(Inet4Address.getLocalHost().getHostAddress(), 4343,
				EnvironmentInfo.currentEnvInfo());
		System.out.println("-----------1");
		
	}
	
	@Test
	public void testNSServerStubStop() throws Exception {

		ServiceInfo<Integer> service = new ServiceInfo<Integer>("add", 1);
		
		List<Serializer> list = new ArrayList<Serializer>();
		list.add(new JsonSerializer());
		
		
		NSServerStub serverStub = new NSServerStub(spInfo.getPort(),nsSPInfo, list);

		ServiceProvider serviceProvider = new TestServiceProvider(service,spInfo);
		
				ServiceProvider provider = serviceProvider;
		serverStub.init(provider);

		serverStub.start();
		
		

		NSClient client = new NSClient(nsSPInfo, new ArrayList<>());

		ServiceSupportInfo[] providers = client.getAllProviders();

		Assert.assertEquals(1, providers.length);
		
		serverStub.stop();
		
		
		providers = client.getAllProviders();

		Assert.assertEquals(0, providers.length);
		

	}
	
	@After
	public void postTest() throws Exception{
		ns.stop();
	}
}

class TestServiceProvider implements ServiceProvider{

	private ServiceProviderInfo spInfo;
	private ServiceInfo<?> service;

	public TestServiceProvider(ServiceInfo<?> service,ServiceProviderInfo spInfo) {
		this.service = service;
		this.spInfo = spInfo;
	}

	@Override
	public ServiceSupportInfo service(ServiceInfo<?> info) throws ServiceNotSupportedException {
		return new ServiceSupportInfo(info, spInfo,
				new SerializationFormat[] { SerializationFormat.defaultFotmat() });
	}

	@Override
	public boolean ping() {
		return true;
	}

	@Override
	public ServiceProviderInfo info() {
		return spInfo;
	}

	@Override
	public ServiceResponse<?> execute(ServiceRequest request) throws ApplicationSpecificErrorException,
			ExecuteInternalError, InvalidArgsException, ServiceNotSupportedException {
		if (request.getService().getId() == 1) {
			if (!(request.getArgs()[0].getContent() instanceof Integer)
					|| !(request.getArgs()[1].getContent() instanceof Integer)) {
				throw new InvalidArgsException("Both must be integers!!");
			}
			Integer num1 = (Integer) request.getArgs()[0].getContent();
			Integer num2 = (Integer) request.getArgs()[1].getContent();
			return new ServiceResponse<>(request.getService(), new SerializedObject<>(num1 + num2),
					request.getRequestId());
		} else {
			throw new ServiceNotSupportedException(request.getService());
		}
	}

	@Override
	public List<ServiceSupportInfo> listSupport() {
		List<ServiceSupportInfo> list = new ArrayList<ServiceSupportInfo>();
		list.add(new ServiceSupportInfo(service, spInfo,
				new SerializationFormat[] { SerializationFormat.defaultFotmat() }));
		return list;
	}
	
}
