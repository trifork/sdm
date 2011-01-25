package com.trifork.sdm.replication.admin.models;

import java.io.IOException;

import javax.inject.Singleton;
import javax.servlet.*;

@Singleton
public class AdminSecurityFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		// TODO: Authorize users based on their SingleSignon token.		
		
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {

	}
}
