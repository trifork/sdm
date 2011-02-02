package com.trifork.sdm.replication.admin.controllers;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.mockito.ArgumentMatcher;

import com.trifork.sdm.replication.admin.models.IAuditLog;
import com.trifork.sdm.replication.admin.models.LogEntry;

public class AuditLogControllerTest {
	@SuppressWarnings("unchecked")
	@Test
	public void test_can_show_all_audit_logs() throws Throwable {
		// Arrange
		ArrayList<LogEntry> logEntries = new ArrayList<LogEntry>();
		logEntries.add(new LogEntry("1", "Message 1", new Date()));
		logEntries.add(new LogEntry("2", "Message 2", new Date()));
		
		IAuditLog auditLog = mock(IAuditLog.class);
		when(auditLog.all()).thenReturn(logEntries);

		AuditLogController auditLogController = spy(new AuditLogController());
		auditLogController.auditLog = auditLog;
		doNothing().when(auditLogController).render(anyString(), any(Map.class), any(HttpServletRequest.class), any(HttpServletResponse.class));
		
		// Act
		auditLogController.doGet(null, null);
		
		// Assert
		verify(auditLogController).render(
				eq("log/list.ftl"),
				argThat(new MapContainLogEntriesMatcher()),
				any(HttpServletRequest.class),
				any(HttpServletResponse.class));
	}
	
	@SuppressWarnings("rawtypes")
	private class MapContainLogEntriesMatcher extends ArgumentMatcher<Map> {
		@SuppressWarnings("unchecked")
		@Override
		public boolean matches(Object argument) {
			if (!(argument instanceof Map<?, ?>)) {
				return false;
			}
			
			Map<String, Object> real = (Map<String, Object>) argument;
			
			if (!real.containsKey("entries")) {
				return false;
			}
			
			Object value = real.get("entries");
			
			if (!(value instanceof List)) {
				return false;
			}
			
			List<LogEntry> logEntries = (List<LogEntry>) value;
			boolean foundAllEntries = true;
			foundAllEntries &= logEntries.get(0).getId().equals("1");
			foundAllEntries &= logEntries.get(1).getId().equals("2");
			
			return foundAllEntries;
		}
	}
}
