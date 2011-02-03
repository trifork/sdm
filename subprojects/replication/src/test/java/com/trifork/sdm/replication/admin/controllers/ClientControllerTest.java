package com.trifork.sdm.replication.admin.controllers;


import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import com.trifork.sdm.replication.GuiceTest;
import com.trifork.sdm.replication.admin.models.*;

import freemarker.template.Configuration;


public class ClientControllerTest extends GuiceTest
{
	private ClientController controller;

	private ClientRepository clientRepository;
	private PermissionRepository permissionRepository;

	protected HttpServletRequest request;
	protected HttpServletResponse response;

	protected OutputStream output;
	protected PrintWriter outputWriter;


	@Before
	public void setUp() throws Exception
	{
		//
		// Mock the request.
		//

		request = mock(HttpServletRequest.class);
		when(request.getContextPath()).thenReturn("/replication");

		//
		// Mock the response.
		//

		response = mock(HttpServletResponse.class);

		// Make sure we can access the output.

		output = new ByteArrayOutputStream();
		outputWriter = new PrintWriter(output, true);
		when(response.getWriter()).thenReturn(outputWriter);

		clientRepository = mock(ClientRepository.class);

		when(clientRepository.create(anyString(), anyString())).thenReturn(new Client("1", "1", "1"));
		when(clientRepository.find(anyString())).thenReturn(new Client("1", "1", "1"));

		// Setup permissions.
		
		permissionRepository = mock(PermissionRepository.class);
		
		List<String> permissions = new ArrayList<String>();
		permissions.add("Foo");
		permissions.add("Bar");
		when(permissionRepository.findByClientId(anyString())).thenReturn(permissions);

		// Templates
		
		Configuration templates = getInjector().getInstance(Configuration.class);
		
		// Auditlog
		
		IAuditLog auditlog = mock(AuditLog.class);
		
		// The controller under test.
		
		controller = spy(new ClientController(clientRepository, permissionRepository, templates, auditlog));
	}


	@Test
	public void test_do_new_on_get() throws Throwable
	{
		// Arrange
		when(request.getRequestURI()).thenReturn("SomeUrlEndingWith/new");

		// Act
		controller.doGet(request, response);

		// Assert
		verify(controller).getNew(request, response);
	}


	@Test
	public void test_do_list_on_get() throws Throwable
	{
		// Arrange
		when(request.getRequestURI()).thenReturn("SomeUrlNotEndingWithSlashNew");

		// Not needed, but here to document the
		// null value returned
		when(request.getParameter("id")).thenReturn(null);

		// Act
		controller.doGet(request, response);

		// Assert
		verify(controller).getList(request, response);
	}


	@Test
	public void test_do_edit_on_get() throws Throwable
	{
		// Arrange
		when(request.getRequestURI()).thenReturn("SomeUrlNotEndingWithSlashNew");
		when(request.getParameter("id")).thenReturn("42");

		// Act
		controller.doGet(request, response);

		// Assert
		verify(controller).getEdit(request, response);
	}


	@Test
	public void test_do_create_on_post() throws Throwable
	{
		// Arrange

		// Act
		controller.doPost(request, response);

		// Assert
		verify(controller).getCreate(request, response);
	}


	@Test
	public void test_do_delete_on_post() throws Throwable
	{
		// Arrange
		when(request.getParameter("id")).thenReturn("42");
		when(request.getParameter("method")).thenReturn("DELETE");

		// Act
		controller.doPost(request, response);

		// Assert
		verify(controller).getDelete(request, response);
	}


	@Test
	public void test_do_update_on_post() throws Throwable
	{
		// Arrange
		when(request.getParameter("id")).thenReturn("42");
		when(request.getParameter("method")).thenReturn("UPDATE");

		@SuppressWarnings("unchecked")
		Enumeration<String> paramNames = mock(Enumeration.class);
		when(paramNames.nextElement()).thenReturn("entity_Apotek", "entity_Sygehus");
		when(paramNames.hasMoreElements()).thenReturn(true, true, false);
		when(request.getParameterNames()).thenReturn(paramNames);

		// Act
		controller.doPost(request, response);

		// Assert
		verify(controller).getUpdate(request, response);
	}
}
