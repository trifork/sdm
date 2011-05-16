package com.trifork.stamdata.replication.logging;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import com.google.inject.Provider;
import com.google.inject.servlet.RequestScoped;

@RequestScoped
public class DefaultClientIpProvider implements Provider<String> {

	private final HttpServletRequest request;

	@Inject
	public DefaultClientIpProvider(HttpServletRequest request) {
		this.request = request;
	}
	
	@Override
	public String get() {
		return request.getRemoteAddr();
	}
}
