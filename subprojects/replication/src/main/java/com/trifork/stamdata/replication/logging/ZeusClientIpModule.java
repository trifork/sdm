package com.trifork.stamdata.replication.logging;

import javax.servlet.http.HttpServletRequest;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.servlet.RequestScoped;
import com.trifork.stamdata.replication.logging.annotations.ClientIp;

public class ZeusClientIpModule extends AbstractModule {

	@Provides
	@ClientIp
	@RequestScoped
	protected String provideIp(HttpServletRequest request) {
		String clientIp = request.getHeader("X-Cluster-Client-IP");
		if(clientIp == null) {
			throw new RuntimeException("Zeus dit not provide a client IP! Misconfiguration issue?");
		}
		return clientIp;
	}

	@Override
	protected void configure() {
		requireBinding(HttpServletRequest.class);
	}
}
