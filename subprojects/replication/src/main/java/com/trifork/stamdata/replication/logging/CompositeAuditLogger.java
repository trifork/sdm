package com.trifork.stamdata.replication.logging;

import com.google.inject.Inject;

/**
 * Facade that logs to both file and database.
 * 
 * @see SLF4JAuditLogger
 * @see DatabaseAuditLogger
 * 
 * @author Thomas Børlum (thb@trifork.com)
 */
public class CompositeAuditLogger implements AuditLogger {

	private final DatabaseAuditLogger dbLogger;
	private final SLF4JAuditLogger fileLogger;

	@Inject
	CompositeAuditLogger(DatabaseAuditLogger dbLogger, SLF4JAuditLogger fileLogger) {
		
		this.dbLogger = dbLogger;
		this.fileLogger = fileLogger;
	}
	
	@Override
	public void log(String format, Object... args) {

		dbLogger.log(format, args);
		fileLogger.log(format, args);
	}
}
