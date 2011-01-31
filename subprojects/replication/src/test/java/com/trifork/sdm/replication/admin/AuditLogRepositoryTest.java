package com.trifork.sdm.replication.admin;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.*;

import com.trifork.sdm.replication.GuiceTest;
import com.trifork.sdm.replication.admin.models.*;


public class AuditLogRepositoryTest extends GuiceTest
{
	private AuditLog auditLogRepository;


	@Before
	public void setUp()
	{
		auditLogRepository = getInjector().getInstance(AuditLog.class);
	}


	@Test
	public void can_create_audit_log() throws Exception
	{
		// Act
		boolean isCreated = auditLogRepository.create("This is a %s statement", "log");

		// Assert
		assertThat(isCreated, is(true));
	}


	@Test
	public void unsuccessful_when_trying_to_log_empty_message() throws Exception
	{
		// Act
		boolean isCreated = auditLogRepository.create("%s", "");

		// Assert
		assertThat(isCreated, is(false));
	}


	@Test
	public void can_find_all_audit_logs() throws Exception
	{
		// Arrange
		auditLogRepository.create("message1");
		auditLogRepository.create("message2");

		// Act
		List<LogEntry> auditLogs = auditLogRepository.all();

		// Assert
		assertTrue(auditLogs.size() > 1);
	}
}
