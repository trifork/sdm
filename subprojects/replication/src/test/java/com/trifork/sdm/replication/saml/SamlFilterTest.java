package com.trifork.sdm.replication.saml;

import static org.mockito.Mockito.*;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.*;
import org.mockito.Mockito;

import com.google.inject.*;
import com.google.inject.util.Modules;
import com.trifork.rid2cpr.RID2CPRFacade;
import com.trifork.sdm.replication.ProductionModule;
import com.trifork.sdm.replication.admin.models.IUserRepository;
import com.trifork.sdm.replication.admin.security.SamlFilter;

import dk.itst.oiosaml.sp.UserAssertion;

public class SamlFilterTest
{
	private static Injector injector;
	
	private SingleSignonHelper singleSignonHelper;
	private RID2CPRFacade rIDHelper;
	private IUserRepository userRepository;

	private String userRID;
	private String userCPR;

	private SamlFilter filter;
	private FilterChain filterChain;
	private HttpServletRequest request;
	private HttpServletResponse response;


	@BeforeClass
	public static void init()
	{
		Module config = Modules.override(new ProductionModule()).with(new AbstractModule()
		{
			@Override
			protected void configure()
			{
				// Unfortunately the user assertion holder uses static
				// methods, and thus makes it hard to test.
				// But we inject a mocked object and access the static on that.

				bind(SingleSignonHelper.class).toInstance(mock(SingleSignonHelper.class));

				// Let us control who is admin and who is not.

				bind(IUserRepository.class).toInstance(mock(IUserRepository.class));

				// Mock the RID2CPR.
				
				bind(RID2CPRFacade.class).toInstance(mock(RID2CPRFacade.class));
			}
		});

		injector = Guice.createInjector(config);
	}


	@Before
	public void setUp()
	{
		filter = injector.getInstance(SamlFilter.class);
		
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		filterChain = mock(FilterChain.class);
		
		// TODO: There is not much use in injecting an instance if
		// we do not reset its behavior after each run. It does not
		// break the test, but it could in the future.
		
		rIDHelper = injector.getInstance(RID2CPRFacade.class);
		userRepository = injector.getInstance(IUserRepository.class);
		singleSignonHelper = injector.getInstance(SingleSignonHelper.class);
	}


	@Test
	public void should_continue_if_the_user_is_authorized() throws Exception
	{
		allowAccess();

		doFilter();

		assertAccessAllowed();
	}


	@Test
	public void should_not_continue_if_the_user_is_not_authorized() throws Exception
	{
		denyAccess();

		doFilter();

		assertAccessDenied();
	}


	//
	// Assertions
	//

	protected void assertAccessDenied() throws Exception
	{
		verify(filterChain, times(0)).doFilter(request, response);
	}


	protected void assertAccessAllowed() throws Exception
	{
		verify(filterChain, times(1)).doFilter(request, response);
	}


	//
	// Helper Methods
	//

	protected void doFilter() throws Exception
	{
		// Create a user with the settings from the test.

		UserAssertion userAssertion = mock(UserAssertion.class);
		
		when(userAssertion.getRIDNumber()).thenReturn(userRID);
		when(singleSignonHelper.getUser()).thenReturn(userAssertion);
		
		when(rIDHelper.getCPR(userRID)).thenReturn(userCPR);

		// Call the filter.

		filter.doFilter(request, response, filterChain);
	}


	protected void denyAccess() throws Exception
	{
		when(userRepository.isAdmin(Mockito.anyString(), Mockito.anyString())).thenReturn(false);
	}


	protected void allowAccess() throws Exception
	{
		when(userRepository.isAdmin(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
	}
}
