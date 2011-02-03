package com.trifork.sdm.replication.gateway;

import static org.mockito.Mockito.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

public class GatewayServletMethodsTest {
	private GatewayServlet servlet;
	private HttpServletRequest request;
	private HttpServletResponse response;

	@Before
	public void setUp() throws Throwable {
		servlet = spy(new GatewayServlet(null));
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		doNothing().when(servlet).processRequest(request, response);
	}

	@Test
	public void test_do_post_works() throws Throwable {
		// Arrange
		
		// Act
		servlet.doPost(request, response);
		
		// Assert
		verify(servlet).processRequest(request, response);
	}
	
	@Test
	public void test_do_get_works() throws Throwable {
		// Arrange
		
		// Act
		servlet.doGet(request, response);
		
		// Assert
		verify(servlet).processRequest(request, response);
	}
	
	@Test
	public void test_do_head_works() throws Throwable {
		// Arrange
		
		// Act
		servlet.doHead(request, response);
		
		// Assert
		verify(servlet).processRequest(request, response);
	}
	
	@Test
	public void test_do_put_works() throws Throwable {
		// Arrange
		
		// Act
		servlet.doPut(request, response);
		
		// Assert
		verify(servlet).processRequest(request, response);
	}
	
	@Test
	public void test_do_delete_works() throws Throwable {
		// Arrange
		
		// Act
		servlet.doDelete(request, response);
		
		// Assert
		verify(servlet).processRequest(request, response);
	}
}
