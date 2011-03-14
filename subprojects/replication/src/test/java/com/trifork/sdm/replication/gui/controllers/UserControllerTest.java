package com.trifork.sdm.replication.gui.controllers;


import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import com.trifork.sdm.replication.gui.models.*;


public class UserControllerTest {

	private UserController userController;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private IAuditLog auditLog;

	private Set<String> whitelist;


	@Before
	public void setUp() throws Exception {

		// REQUEST

		request = mock(HttpServletRequest.class);
		when(request.getContextPath()).thenReturn("/replication");

		// RESPONSE

		response = mock(HttpServletResponse.class);

		auditLog = mock(IAuditLog.class);

		// The controller under test.

		whitelist = new HashSet<String>();

		UserDao users = mock(UserDao.class);

		// HTML Renderer

		PageRenderer renderer = mock(PageRenderer.class);

		userController = spy(new UserController(whitelist, users, auditLog, renderer));

		doNothing().when(userController).getNew(request, response);
		doNothing().when(userController).getList(request, response);
		doNothing().when(userController).getEdit(request, response);
		doNothing().when(userController).getCreate(request, response);
		doNothing().when(userController).getDelete(request, response);
	}


	@Test
	public void test_do_new_on_get() throws Throwable {

		when(request.getRequestURI()).thenReturn("SomeUrlEndingWith/new");

		userController.doGet(request, response);

		verify(userController).getNew(request, response);
	}


	@Test
	public void test_do_list_on_get() throws Throwable {

		when(request.getRequestURI()).thenReturn("SomeUrlNotEndingWithSlashNew");
		when(request.getParameter("id")).thenReturn(null);

		userController.doGet(request, response);

		verify(userController).getList(request, response);
	}


	@Test
	public void test_do_edit_on_get() throws Throwable {

		when(request.getRequestURI()).thenReturn("SomeUrlNotEndingWithSlashNew");
		when(request.getParameter("id")).thenReturn("42");

		userController.doGet(request, response);

		verify(userController).getEdit(request, response);
	}


	@Test
	public void test_do_create_on_post() throws Throwable {

		userController.doPost(request, response);

		verify(userController).getCreate(request, response);
		verify(userController, never()).getDelete(request, response);
	}


	@Test
	public void test_do_delete_on_post() throws Throwable {

		when(request.getParameter("id")).thenReturn("42");
		when(request.getParameter("method")).thenReturn("DELETE");

		userController.doPost(request, response);

		verify(userController, never()).getCreate(request, response);
		verify(userController).getDelete(request, response);
	}
}
