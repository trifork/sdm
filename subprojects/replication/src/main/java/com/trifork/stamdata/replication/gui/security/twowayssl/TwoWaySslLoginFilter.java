package com.trifork.stamdata.replication.gui.security.twowayssl;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.google.inject.Provider;
import com.trifork.stamdata.replication.gui.models.User;

@Singleton
public class TwoWaySslLoginFilter implements Filter {

	private final Provider<User> user;

	@Inject
	public TwoWaySslLoginFilter(Provider<User> user) {
		this.user = user;
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if(user.get() == null) {
			return;
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
