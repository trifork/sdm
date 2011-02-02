package com.trifork.sdm.replication.admin.controllers;


import static org.mockito.Mockito.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import com.trifork.sdm.replication.GuiceTest;


public class ClientControllerTest extends GuiceTest
{
	private ClientController controller;

	private HttpServletRequest request;
	private HttpServletResponse response;


	@Before
	public void setUp() throws Throwable
	{
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);

		doNothing().when(controller).getNew(request, response);
		doNothing().when(controller).getList(request, response);
		doNothing().when(controller).getEdit(request, response);
		doNothing().when(controller).getCreate(request, response);
		doNothing().when(controller).getDelete(request, response);
		doNothing().when(controller).getUpdate(request, response);
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
		verify(controller, never()).getList(request, response);
		verify(controller, never()).getEdit(request, response);
	}


	@Test
	public void test_do_list_on_get() throws Throwable
	{
		// Arrange
		when(request.getRequestURI()).thenReturn("SomeUrlNotEndingWithSlashNew");
		when(request.getParameter("id")).thenReturn(null); // Not needed, but here to document the
															// null value returned

		// Act
		controller.doGet(request, response);

		// Assert
		verify(controller).getList(request, response);
		verify(controller, never()).getNew(request, response);
		verify(controller, never()).getEdit(request, response);
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
		verify(controller, never()).getNew(request, response);
		verify(controller, never()).getList(request, response);
	}


	@Test
	public void test_do_create_on_post() throws Throwable
	{
		// Arrange

		// Act
		controller.doPost(request, response);

		// Assert
		verify(controller).getCreate(request, response);
		verify(controller, never()).getDelete(request, response);
		verify(controller, never()).getUpdate(request, response);
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
		verify(controller, never()).getCreate(request, response);
		verify(controller).getDelete(request, response);
		verify(controller, never()).getUpdate(request, response);
	}


	@Test
	public void test_do_update_on_post() throws Throwable
	{
		// Arrange
		when(request.getParameter("id")).thenReturn("42");
		when(request.getParameter("method")).thenReturn("OTHER");

		// Act
		controller.doPost(request, response);

		// Assert
		verify(controller, never()).getCreate(request, response);
		verify(controller, never()).getDelete(request, response);
		verify(controller).getUpdate(request, response);
	}
}
