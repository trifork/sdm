package com.trifork.stamdata.lookup.security;

import java.io.IOException;
import java.util.Collection;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.trifork.stamdata.lookup.security.annotations.AuthorizedClients;
import com.trifork.stamdata.ssl.annotations.AuthenticatedSSN;

@Singleton
public class SecurityFilter implements Filter {
	
	private final Provider<String> authenticatedSsnProvider;
	private final Collection<String> authorizedClients;
	private final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

	@Inject
	public SecurityFilter(@AuthenticatedSSN Provider<String> authenticatedSsnProvider, @AuthorizedClients Collection<String> authorizedClients) {
		this.authenticatedSsnProvider = authenticatedSsnProvider;
		this.authorizedClients = authorizedClients;
		logger.info("Initializing security filter, authorized clients: {}", authorizedClients);
	}
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		String authenticatedSsn = authenticatedSsnProvider.get();
		if(authenticatedSsn == null) {
			throw new RuntimeException("No authenticated client");
		}
		if(!authorizedClients.contains(authenticatedSsn)) {
			throw new RuntimeException("Client not authorized, client=" + authenticatedSsn);
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}

}
