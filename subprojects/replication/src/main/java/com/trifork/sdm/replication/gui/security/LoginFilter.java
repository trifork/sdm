package com.trifork.sdm.replication.gui.security;

import static org.slf4j.LoggerFactory.*;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;

import com.google.inject.*;
import com.trifork.sdm.replication.gui.models.User;

@Singleton
public class LoginFilter implements Filter {

	private static final Logger logger = getLogger(LoginFilter.class);

	private final Provider<User> userProvider;


	@Inject
	LoginFilter(Provider<User> userProvider) {
		this.userProvider = userProvider;
	}


	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		// If a user is not authorized then the user will be null.

		User user = userProvider.get();

		if (user != null) {
			HttpServletRequest servletRequest = (HttpServletRequest)request;
			
			logger.info("User (CVR={}, CPR={}) accessed URL={}.", new Object[] {user.getCvr(), user.getCpr(), servletRequest.getRequestURL()});
			chain.doFilter(request, response);
		}
		else {
			logger.warn("Unauthorized access attempt. Only administrators allowed. IP={}", request.getRemoteAddr());
			// TODO: response.getWriter().println("Unauthorized access attempt. Only administrators allowed.");
		}
	}


	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// Do nothing
	}


	@Override
	public void destroy() {
		// Do nothing
	}
}
