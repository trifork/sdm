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
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.trifork.stamdata.lookup.security.annotations.AuthorizedClients;
import com.trifork.stamdata.ssl.AuthenticatedSsnProvider.AuthenticationFailedException;
import com.trifork.stamdata.ssl.AuthenticatedSsnProvider.AuthenticationFailedException.Reason;
import com.trifork.stamdata.ssl.UncheckedProvider;
import com.trifork.stamdata.ssl.annotations.AuthenticatedSSN;

@Singleton
public class SecurityFilter implements Filter {
	
	private final UncheckedProvider<String> authenticatedSsnProvider;
	private final Collection<String> authorizedClients;
	private final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

	@Inject
	public SecurityFilter(@AuthenticatedSSN UncheckedProvider<String> authenticatedSsnProvider, @AuthorizedClients Collection<String> authorizedClients) {
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
        HttpServletResponse res = (HttpServletResponse) response;
        String authenticatedSsn;
		try {
			authenticatedSsn = authenticatedSsnProvider.get();
		}
		catch(AuthenticationFailedException e) {
			logger.info("Client authentication failed", e);
			if(e.getReason() == Reason.NO_CERTIFICATE) {
	            res.sendError(HttpServletResponse.SC_FORBIDDEN, "No credentials supplied");
			}
			else {
	            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Client certificate invalid. ssn=" + e.getSsn());
			}
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
