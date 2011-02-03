package com.trifork.sdm.replication.admin.controllers;


import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import com.trifork.sdm.replication.GuiceTest;
import com.trifork.sdm.replication.admin.models.IAuditLog;
import com.trifork.sdm.replication.admin.models.LogEntry;

import freemarker.template.Configuration;


public class AuditLogControllerTest extends GuiceTest
{
	private AuditLogController controller;

	private HttpServletRequest request;

	private HttpServletResponse response;

	private ByteArrayOutputStream output;

	private PrintWriter outputWriter;

	private static List<LogEntry> entries;

	private IAuditLog auditLog;


	@Override
	protected void configure()
	{
		entries = new ArrayList<LogEntry>();
	}


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
		when(auditLog.all()).thenReturn(entries);

		// The controller under test.

		Configuration config = getInjector().getInstance(Configuration.class);

		controller = spy(new AuditLogController(config, auditLog));
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
		assertThat(output.toString(), containsString(content));
	}
}
