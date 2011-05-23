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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.trifork.stamdata.lookup.security.annotations.AuthorizedClients;
import com.trifork.stamdata.ssl.MocesCertificateWrapper;
import com.trifork.stamdata.ssl.OcesHelper;

@Singleton
public class SecurityFilter implements Filter {
	
	private final Provider<OcesHelper> ocesHelper;
	private final Collection<String> authorizedClients;
	private final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

	@Inject
	public SecurityFilter(Provider<OcesHelper> ocesHelper, @AuthorizedClients Collection<String> authorizedClients) {
		this.ocesHelper = ocesHelper;
		this.authorizedClients = authorizedClients;
		logger.info("Initializing security filter, authorized clients: {}", authorizedClients);
	}
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		MocesCertificateWrapper certificate = ocesHelper.get().extractCertificateFromRequest((HttpServletRequest) request);
		if(certificate.isValid()) {
			String ssn = certificate.getSubjectSerialNumber();
			if(!authorizedClients.contains(ssn)) {
				throw new RuntimeException("Client not authorized, client=" + ssn);
			}
			else {
				chain.doFilter(request, response);
			}
		}
		else {
			throw new RuntimeException("Client presented invalid certificate, client=" + certificate.getSubjectSerialNumber());
		}
	}

	@Override
	public void destroy() {
	}

}
