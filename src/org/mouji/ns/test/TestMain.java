package org.mouji.ns.test;

import java.io.IOException;
import java.net.Inet4Address;
import java.util.ArrayList;

import org.junit.Assert;
import org.mouji.common.errors.RPCException;
import org.mouji.common.errors.RPCProviderFailureException;
import org.mouji.common.errors.ResponseContentTypeCannotBePrasedException;
import org.mouji.common.errors.SerializationFormatNotSupported;
import org.mouji.common.info.NameServerInfo;
import org.mouji.common.info.SerializationFormat;
import org.mouji.common.info.ServiceInfo;
import org.mouji.common.info.ServiceProviderInfo;
import org.mouji.common.info.ServiceSupportInfo;
import org.mouji.common.info.StubEnvInfo;
import org.mouji.stub.java.stubs.NSClient;


public class TestMain {

	public static void main(String[] args) throws ResponseContentTypeCannotBePrasedException,
			SerializationFormatNotSupported, RPCException, RPCProviderFailureException, IOException, Exception {
		 NameServerInfo nsInfo;
		 ServiceProviderInfo sampleProvider = new ServiceProviderInfo("test",
		 5, StubEnvInfo.currentEnvInfo());
		
		
		
		 ServiceProviderInfo nsSPInfo = new
		 ServiceProviderInfo(Inet4Address.getLocalHost().getHostAddress(),
		 6161,
		 StubEnvInfo.currentEnvInfo());
		 nsInfo = new NameServerInfo(nsSPInfo.getURL(), nsSPInfo.getPort());
		
		 ServiceInfo<Integer> service = new ServiceInfo<Integer>("test", 1);
		
		 NSClient client = new NSClient(nsSPInfo, new ArrayList<>());
		
		 ServiceSupportInfo provider = client.getProvider(service);
		
		 System.out.println(provider);
		

	}
}
