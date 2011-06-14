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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.jersey.api.NotFoundException;
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
        HttpServletResponse res = (HttpServletResponse) response;

        if(authenticatedSsn == null) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "No credentials supplied");
            return;
        }
        if(!authorizedClients.contains(authenticatedSsn)) {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Client not authorized, client=" + authenticatedSsn);
            return;
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}

}
