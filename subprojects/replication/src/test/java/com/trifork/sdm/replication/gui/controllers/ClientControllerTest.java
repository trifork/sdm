package com.trifork.sdm.replication.gui.controllers;


import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import com.trifork.sdm.replication.gui.models.*;
import com.trifork.sdm.replication.mocks.MockEntity;
import com.trifork.sdm.replication.replication.models.Record;


public class ClientControllerTest {

	private ClientController controller;

	private ClientDao clientDao;
	private PermissionDao permissionDao;

	protected HttpServletRequest request;
	protected HttpServletResponse response;

	protected OutputStream output;
	protected PrintWriter outputWriter;


	@Before
	public void setUp() throws Exception {

		// REQUEST

		request = mock(HttpServletRequest.class);
		when(request.getContextPath()).thenReturn("/replication");

		// RESPONSE

		response = mock(HttpServletResponse.class);

		// CLIENTS

		clientDao = mock(ClientDao.class);

		when(clientDao.create(anyString(), anyString())).thenReturn(new Client("1", "1", "1"));
		when(clientDao.find(anyString())).thenReturn(new Client("1", "1", "1"));

		// Setup permissions.

		permissionDao = mock(PermissionDao.class);

		List<String> permissions = new ArrayList<String>();
		permissions.add("Foo");
		permissions.add("Bar");
		when(permissionDao.findByClientId("1")).thenReturn(permissions);

		// Auditlog

		AuditLog audit = mock(AuditLog.class);

		// Mock some Entity types.

		Map<String, Class<? extends Record>> registry = new HashMap<String, Class<? extends Record>>();
		registry.put("foo/bar/v1", MockEntity.class);

		// HTML Templates

		PageRenderer renderer = mock(PageRenderer.class);

		// The controller under test.

		controller = spy(new ClientController(clientDao, permissionDao, renderer, audit, registry));
	}


	@Test
	public void test_do_new_on_get() throws Throwable {
		// Arrange
		when(request.getRequestURI()).thenReturn("SomeUrlEndingWith/new");

		// Act
		controller.doGet(request, response);

		// Assert
		verify(controller).getNew(request, response);
	}


	@Test
	public void test_do_list_on_get() throws Throwable {
		// Arrange
		when(request.getRequestURI()).thenReturn("SomeUrlNotEndingWithSlashNew");

		// Not needed, but here to document the
		// null value returned.
		when(request.getParameter("id")).thenReturn(null);

		controller.doGet(request, response);

		verify(controller).getList(request, response);
	}


	@Test
	public void test_do_edit_on_get() throws Throwable {

		when(request.getRequestURI()).thenReturn("SomeUrlNotEndingWithSlashNew");
		when(request.getParameter("id")).thenReturn("42");

		controller.doGet(request, response);
		verify(controller).getEdit(request, response);
	}


	@Test
	public void test_do_create_on_post() throws Throwable {

		controller.doPost(request, response);

		verify(controller).getCreate(request, response);
	}


	@Test
	public void test_do_delete_on_post() throws Throwable {

		when(request.getParameter("id")).thenReturn("42");
		when(request.getParameter("method")).thenReturn("DELETE");

		controller.doPost(request, response);

		verify(controller).getDelete(request, response);
	}


	@Test
	public void test_do_update_on_post() throws Throwable {

		when(request.getParameter("id")).thenReturn("42");
		when(request.getParameter("method")).thenReturn("UPDATE");

		@SuppressWarnings("unchecked")
		Enumeration<String> paramNames = mock(Enumeration.class);
		when(paramNames.nextElement()).thenReturn("entity_Apotek", "entity_Sygehus");
		when(paramNames.hasMoreElements()).thenReturn(true, true, false);
		when(request.getParameterNames()).thenReturn(paramNames);

		controller.doPost(request, response);

		verify(controller).getUpdate(request, response);
	}
}
