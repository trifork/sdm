package com.trifork.stamdata.logging;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.MDC;

import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.trifork.stamdata.logging.annotations.ClientIp;

/**
 * This filter adds a clientIp entry to the slf4j MDC
 * @author ahj
 *
 */
@Singleton
public class ClientIpLoggingFilter implements Filter {

	private final Provider<String> ipProvider;

	@Inject
	public ClientIpLoggingFilter(@ClientIp Provider<String> ipProvider) {
		this.ipProvider = ipProvider;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		MDC.put("clientIp", ipProvider.get());
		try {
			chain.doFilter(request, response);
		}
		finally {
			MDC.remove("clientIp");
		}
	}

	@Override
	public void destroy() {
	}

}
