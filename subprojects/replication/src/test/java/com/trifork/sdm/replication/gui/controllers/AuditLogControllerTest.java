package com.trifork.sdm.replication.gui.controllers;


import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import com.trifork.sdm.replication.gui.models.IAuditLog;
import com.trifork.sdm.replication.gui.models.LogEntry;


public class AuditLogControllerTest {

	private AuditLogController controller;

	private HttpServletRequest request;
	private HttpServletResponse response;

	private static List<LogEntry> entries;

	private IAuditLog auditLog;

	private PageRenderer renderer;


	@Before
	public void setUp() throws Exception {

		entries = new ArrayList<LogEntry>();

		// REQUEST

		request = mock(HttpServletRequest.class);

		// RESPONSE

		response = mock(HttpServletResponse.class);

		// AUDIT

		auditLog = mock(IAuditLog.class);
		when(auditLog.all()).thenReturn(entries);

		// HTML RENDERER

		renderer = mock(PageRenderer.class);

		// CONTROLLER

		controller = new AuditLogController(auditLog, renderer);
	}


	@Test
	public void should_show_all_audit_log_entries() throws Exception {

		entries.add(new LogEntry("1", "Message 1", new Date()));
		entries.add(new LogEntry("2", "Message 2", new Date()));

		controller.doGet(request, response);

		assertPageContains("log/list.ftl", "Message 1");
	}


	@SuppressWarnings("unchecked")
	public void assertPageContains(String template, String... content) throws IOException {

		verify(renderer).render(eq(template), anyMap(), eq(request), eq(response));
	}
}
