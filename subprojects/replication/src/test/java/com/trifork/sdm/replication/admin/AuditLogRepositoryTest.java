package com.trifork.sdm.replication.admin;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.trifork.sdm.replication.admin.models.LogEntry;


public class AuditLogRepositoryTest extends RepositoryTest
{
	@Test
	public void can_create_audit_log() throws Exception
	{
		// Arrange

		// Act
		boolean created = auditLogRepository.create("This is a %s statement", "log");

		// Assert
		assertTrue(created);
	}


	@Test
	public void unsuccessfull_when_trying_to_log_empty_message() throws Exception
	{
		// Arrange

		// Act
		boolean created = auditLogRepository.create("%s", "");

		// Assert
		assertFalse(created);
	}


	@Test
	public void can_find_all_audit_logs() throws Exception
	{
		// Arrange
		auditLogRepository.create("message1");
		auditLogRepository.create("message2");

		// Act
		List<LogEntry> auditLogs = auditLogRepository.findAll();

		// Assert
		assertTrue(auditLogs.size() > 1);
	}
}
