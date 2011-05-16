package com.trifork.stamdata.replication.logging;

import javax.servlet.http.HttpServletRequest;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.servlet.RequestScoped;
import com.trifork.stamdata.replication.logging.annotations.ClientIp;

public class DefaultClientIpModule extends AbstractModule {
	@Provides
	@ClientIp
	@RequestScoped
	protected String provideIp(HttpServletRequest request) {
		return request.getRemoteAddr();

	}

	@Override
	protected void configure() {
		requireBinding(HttpServletRequest.class);
	}
}
