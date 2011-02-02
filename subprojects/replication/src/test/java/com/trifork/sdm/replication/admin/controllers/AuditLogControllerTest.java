package com.trifork.sdm.replication.admin.controllers;


import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.*;

import com.google.inject.Provides;
import com.trifork.sdm.replication.GuiceTest;
import com.trifork.sdm.replication.admin.models.IAuditLog;
import com.trifork.sdm.replication.admin.models.LogEntry;

import static org.hamcrest.Matchers.containsString;


public class AuditLogControllerTest extends GuiceTest
{
	private AuditLogController controller;

	private static List<LogEntry> entries;

	private HttpServletRequest request;
	private HttpServletResponse response;

	private OutputStream output;
	private PrintWriter outputWriter;


	@Override
	protected void configure()
	{
		entries = new ArrayList<LogEntry>();
	}


	@Before
	public void setUp() throws Exception
	{
		controller = getInjector().getInstance(AuditLogController.class);

		// Mock the request and response.

		request = mock(HttpServletRequest.class);
		when(request.getContextPath()).thenReturn("/replication");

		response = mock(HttpServletResponse.class);

		// Make sure we can access the output.

		output = new ByteArrayOutputStream();
		outputWriter = new PrintWriter(output, true);
		when(response.getWriter()).thenReturn(outputWriter);
	}


	@Test
	public void should_show_all_audit_log_entries() throws Throwable
	{
		entries.add(new LogEntry("1", "Message 1", new Date()));
		entries.add(new LogEntry("2", "Message 2", new Date()));

		controller.doGet(request, response);

		assertPageContains("Message 1");
		assertPageContains("Message 2");
	}


	public void assertPageContains(String content)
	{
		String page = output.toString();
		assertThat(page, containsString(content));
	}


	@Provides
	public IAuditLog provideAuditLog() throws Exception
	{
		IAuditLog log = mock(IAuditLog.class);
		when(log.all()).thenReturn(entries);
		return log;
	}
}
