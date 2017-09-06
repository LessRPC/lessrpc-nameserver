package org.mouji.ns.core;

import java.util.Random;

import org.mouji.common.info.ServiceInfo;
import org.mouji.common.info.ServiceSupportInfo;
import org.mouji.common.loadbalance.ProviderLoadBalancer;

public class RandomLoadBalancer implements ProviderLoadBalancer {

	private final Random rnd;

	public RandomLoadBalancer() {
		rnd = new Random();
		rnd.setSeed(System.currentTimeMillis());
	}

	@Override
	public ServiceSupportInfo select(ServiceInfo<?> service, ServiceSupportInfo[] supports) {
		return supports[rnd.nextInt(supports.length)];
	}

}
