package com.trifork.stamdata.logging;

import java.util.List;

import com.google.inject.Module;

public class LogConfigurer {

	private static final String ZEUS_LOAD_BALANCER = "zeusLoadBalancer";

	public static void configureLogging(boolean twoWaySslInUse, String sslTerminationMethod, List<Module> modules) {
		modules.add(new LoggingModule());

		boolean zeusLoadBalancerInUse = twoWaySslInUse && ZEUS_LOAD_BALANCER.equals(sslTerminationMethod);

		// The Zeus load balancer sends the actual client ip in a header. We are not really
		// interested in logging the ip-address of the load-balancer in every request!
		modules.add(zeusLoadBalancerInUse ? new ZeusClientIpModule() : new DefaultClientIpModule());
	}

}
