package com.trifork.sdm.replication.replication;

import static com.trifork.sdm.replication.replication.URLParameters.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.*;

import com.google.inject.Injector;
import com.trifork.sdm.replication.GuiceTest;
import com.trifork.sdm.replication.util.SignatureFactory;


public class ResourceFilterTest extends GuiceTest
{
	private static final int MILLIS_TO_SECS = 1000;
	private static final int TIME_TO_LIVE = 100;

	private static Injector injector;

	private ReplicationFilter filter;

	private String requestEntity;
	private String requestExpires;
	private String requestSignature;

	private static final String generatedSignature = RandomStringUtils.randomAlphabetic(20);

	private FilterChain filterChain;


	@Override
	protected void configure()
	{
		// Since we have to check the signature in the filter, we have
		// to stub the
		// create method on the signature factory.

		SignatureFactory factory = mock(SignatureFactory.class);
		when(factory.create(anyString(), anyLong(), anyString(), anyInt())).thenReturn(generatedSignature);

		bind(SignatureFactory.class).toInstance(factory);
	}


	@Before
	public void setUp()
	{
		filter = injector.getInstance(ReplicationFilter.class);

		// The filter chain is used to tell us if the filter continued or
		// returned.

		filterChain = mock(FilterChain.class);

		// Set the default request values.

		requestEntity = RandomStringUtils.randomAlphabetic(10);

		requestExpires = Long.toString(getCurrentTimeInSecs() + TIME_TO_LIVE);

		requestSignature = generatedSignature;
	}


	@Test
	public void should_allowed_valid_requests() throws Exception
	{
		sendRequest();

		assertDidAcceptRequest();
	}


	@Test
	public void should_rejects_expired_requests() throws Exception
	{
		requestExpires = Long.toString(getCurrentTimeInSecs());

		sendRequest();

		assertDidRejectRequest();
	}


	@Test
	public void should_reject_requests_with_invalid_signatures() throws Exception
	{
		requestSignature = RandomStringUtils.randomAlphabetic(20);

		sendRequest();

		assertDidRejectRequest();
	}


	//
	// Assertions
	//

	protected void assertDidRejectRequest() throws Exception
	{
		verify(filterChain, times(0)).doFilter(any(ServletRequest.class), any(ServletResponse.class));
	}


	protected void assertDidAcceptRequest() throws Exception
	{
		verify(filterChain, times(1)).doFilter(any(ServletRequest.class), any(ServletResponse.class));
	}


	//
	// Helper methods
	//

	protected void sendRequest() throws Exception
	{
		// Build a request we the given parameters.

		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getParameter(ENTITY_TYPE)).thenReturn(requestEntity);
		when(request.getParameter(EXPIRES)).thenReturn(requestExpires);
		when(request.getParameter(SIGNATURE)).thenReturn(requestSignature);
		when(request.getParameter(PAGE_SIZE)).thenReturn("10");

		// Build a mock response so we can check the returned status.

		ServletOutputStream outputStream = mock(ServletOutputStream.class);

		HttpServletResponse response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(outputStream);

		// Run the filter.

		filter.doFilter(request, response, filterChain);
	}


	private long getCurrentTimeInSecs()
	{
		return System.currentTimeMillis() / MILLIS_TO_SECS;
	}
}
