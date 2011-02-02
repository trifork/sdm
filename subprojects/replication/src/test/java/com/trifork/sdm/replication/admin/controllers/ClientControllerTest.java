package com.trifork.sdm.replication.admin.controllers;

import static org.mockito.Mockito.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

public class ClientControllerTest {
	private ClientController clientController;
	private HttpServletRequest request;
	private HttpServletResponse response;

	@Before
	public void setUp() throws Throwable {
		clientController = spy(new ClientController());
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		doNothing().when(clientController).getNew(request, response);
		doNothing().when(clientController).getList(request, response);
		doNothing().when(clientController).getEdit(request, response);
		doNothing().when(clientController).getCreate(request, response);
		doNothing().when(clientController).getDelete(request, response);
		doNothing().when(clientController).getUpdate(request, response);
		
	}
	
	@Test
	public void test_do_new_on_get() throws Throwable {
		// Arrange
		when(request.getRequestURI()).thenReturn("SomeUrlEndingWith/new");
		
		// Act
		clientController.doGet(request, response);
		
		// Assert
		verify(clientController).getNew(request, response);
		verify(clientController, never()).getList(request, response);
		verify(clientController, never()).getEdit(request, response);
	}

	@Test
	public void test_do_list_on_get() throws Throwable {
		// Arrange
		when(request.getRequestURI()).thenReturn("SomeUrlNotEndingWithSlashNew");
		when(request.getParameter("id")).thenReturn(null); // Not needed, but here to document the null value returned
		
		// Act
		clientController.doGet(request, response);
		
		// Assert
		verify(clientController).getList(request, response);
		verify(clientController, never()).getNew(request, response);
		verify(clientController, never()).getEdit(request, response);
	}

	@Test
	public void test_do_edit_on_get() throws Throwable {
		// Arrange
		when(request.getRequestURI()).thenReturn("SomeUrlNotEndingWithSlashNew");
		when(request.getParameter("id")).thenReturn("42");
		
		// Act
		clientController.doGet(request, response);
		
		// Assert
		verify(clientController).getEdit(request, response);
		verify(clientController, never()).getNew(request, response);
		verify(clientController, never()).getList(request, response);
	}

	@Test
	public void test_do_create_on_post() throws Throwable {
		// Arrange
		
		// Act
		clientController.doPost(request, response);
		
		// Assert
		verify(clientController).getCreate(request, response);
		verify(clientController, never()).getDelete(request, response);
		verify(clientController, never()).getUpdate(request, response);
	}

	@Test
	public void test_do_delete_on_post() throws Throwable {
		// Arrange
		when(request.getParameter("id")).thenReturn("42");
		when(request.getParameter("method")).thenReturn("DELETE");
		
		// Act
		clientController.doPost(request, response);
		
		// Assert
		verify(clientController, never()).getCreate(request, response);
		verify(clientController).getDelete(request, response);
		verify(clientController, never()).getUpdate(request, response);
	}

	@Test
	public void test_do_update_on_post() throws Throwable {
		// Arrange
		when(request.getParameter("id")).thenReturn("42");
		when(request.getParameter("method")).thenReturn("OTHER");
		
		// Act
		clientController.doPost(request, response);
		
		// Assert
		verify(clientController, never()).getCreate(request, response);
		verify(clientController, never()).getDelete(request, response);
		verify(clientController).getUpdate(request, response);
	}
}
