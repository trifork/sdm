package com.trifork.sdm.replication.admin.controllers;


import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import com.trifork.sdm.replication.GuiceTest;
import com.trifork.sdm.replication.admin.models.*;

import freemarker.template.Configuration;


public class UserControllerTest extends GuiceTest
{
	private UserController userController;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private ByteArrayOutputStream output;
	private PrintWriter outputWriter;
	private IAuditLog auditLog;

	private Set<String> whitelist;


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

		auditLog = mock(IAuditLog.class);

		// The controller under test.

		Configuration config = getInjector().getInstance(Configuration.class);

		whitelist = new HashSet<String>();

		IUserRepository users = mock(UserRepository.class);

		userController = spy(new UserController(whitelist, users, config, auditLog));

		doNothing().when(userController).getNew(request, response);
		doNothing().when(userController).getList(request, response);
		doNothing().when(userController).getEdit(request, response);
		doNothing().when(userController).getCreate(request, response);
		doNothing().when(userController).getDelete(request, response);
	}


	@Test
	public void test_do_new_on_get() throws Throwable
	{
		// Arrange
		when(request.getRequestURI()).thenReturn("SomeUrlEndingWith/new");

		// Act
		userController.doGet(request, response);

		// Assert
		verify(userController).getNew(request, response);
		verify(userController, never()).getList(request, response);
		verify(userController, never()).getEdit(request, response);
	}


	@Test
	public void test_do_list_on_get() throws Throwable
	{
		// Arrange
		when(request.getRequestURI()).thenReturn("SomeUrlNotEndingWithSlashNew");
		when(request.getParameter("id")).thenReturn(null); // Not needed, but here to document the
															// null value returned

		// Act
		userController.doGet(request, response);

		// Assert
		verify(userController).getList(request, response);
		verify(userController, never()).getNew(request, response);
		verify(userController, never()).getEdit(request, response);
	}


	@Test
	public void test_do_edit_on_get() throws Throwable
	{
		// Arrange
		when(request.getRequestURI()).thenReturn("SomeUrlNotEndingWithSlashNew");
		when(request.getParameter("id")).thenReturn("42");

		// Act
		userController.doGet(request, response);

		// Assert
		verify(userController).getEdit(request, response);
		verify(userController, never()).getNew(request, response);
		verify(userController, never()).getList(request, response);
	}


	@Test
	public void test_do_create_on_post() throws Throwable
	{
		// Arrange

		// Act
		userController.doPost(request, response);

		// Assert
		verify(userController).getCreate(request, response);
		verify(userController, never()).getDelete(request, response);
	}


	@Test
	public void test_do_delete_on_post() throws Throwable
	{
		// Arrange
		when(request.getParameter("id")).thenReturn("42");
		when(request.getParameter("method")).thenReturn("DELETE");

		// Act
		userController.doPost(request, response);

		// Assert
		verify(userController, never()).getCreate(request, response);
		verify(userController).getDelete(request, response);
	}
}
