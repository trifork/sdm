package com.trifork.stamdata.logging;

import java.io.IOException;
import java.util.Random;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.MDC;

import com.google.inject.Singleton;

/**
 * This filter adds a randomly generated requestId to the slf4j MDC
 * @author ahj
 *
 */
@Singleton
public class RequestIdLoggingFilter implements Filter {

	private Random random = new Random();

	private long randomLong()  {
		return Math.abs(random.nextLong());
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		MDC.put("requestId", Long.toString(randomLong()));
		try {
			chain.doFilter(request, response);
		}
		finally {
			MDC.remove("requestId");
		}
	}

	@Override
	public void destroy() {
	}

}
