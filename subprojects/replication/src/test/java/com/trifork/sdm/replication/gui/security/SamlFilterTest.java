package com.trifork.sdm.replication.gui.security;


import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Provider;
import com.trifork.sdm.replication.gui.models.User;


public class SamlFilterTest {

	private LoginFilter filter;
	private FilterChain filterChain;

	private HttpServletRequest request;
	private HttpServletResponse response;

	@SuppressWarnings("rawtypes")
	private Provider userProvider;

	private String CPR = "11111111111";
	private String CVR = "22222222";


	@Before
	@SuppressWarnings({ "unchecked" })
	public void setUp() throws IOException {

		request = mock(HttpServletRequest.class);
		when(request.getRemoteAddr()).thenReturn("127.0.0.1");

		response = mock(HttpServletResponse.class);

		filterChain = mock(FilterChain.class);

		userProvider = mock(Provider.class);

		filter = new LoginFilter(userProvider);
	}


	@Test
	public void should_continue_if_the_user_is_authorized() throws Exception {
		allowAccess();

		doFilter();

		assertAccessAllowed();
	}


	@Test
	public void should_not_continue_if_the_user_is_not_authorized() throws Exception {
		denyAccess();

		doFilter();

		assertAccessDenied();
	}


	//
	// Assertions
	//

	protected void assertAccessDenied() throws Exception {

		verify(filterChain, never()).doFilter(any(ServletRequest.class), any(ServletResponse.class));
	}


	protected void assertAccessAllowed() throws Exception {

		verify(filterChain).doFilter(request, response);
	}


	//
	// Helper Methods
	//

	protected void doFilter() throws Exception {
		filter.doFilter(request, response, filterChain);
	}


	protected void denyAccess() throws Exception {

		when(userProvider.get()).thenReturn(null);
	}


	protected void allowAccess() throws Exception {

		User user = mock(User.class);
		when(user.getCpr()).thenReturn(CPR);
		when(user.getCvr()).thenReturn(CVR);

		when(userProvider.get()).thenReturn(user);
	}
}
