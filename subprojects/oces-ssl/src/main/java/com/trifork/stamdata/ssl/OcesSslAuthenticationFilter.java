package com.trifork.stamdata.ssl;

import java.io.IOException;

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

import com.trifork.stamdata.ssl.AuthenticatedSsnProvider.AuthenticationFailedException;
import com.trifork.stamdata.ssl.AuthenticatedSsnProvider.AuthenticationFailedException.Reason;
import com.trifork.stamdata.ssl.annotations.AuthenticatedSSN;

public class OcesSslAuthenticationFilter implements Filter{

	private static final Logger logger = LoggerFactory.getLogger(OcesSslAuthenticationFilter.class);

	private final UncheckedProvider<String> ssnProvider;

	@Inject
	public OcesSslAuthenticationFilter(@AuthenticatedSSN UncheckedProvider<String> ssnProvider) {
		this.ssnProvider = ssnProvider;

	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse) response;
		try {
			ssnProvider.get();
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

	}

	@Override
	public void destroy() {
	}

}
