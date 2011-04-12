package com.trifork.stamdata.replication.logging;

/**
 * An abstract for different kinds of audit loggers.
 * 
 * Uses the Strategy Pattern.
 * 
 * @author Thomas Børlum (thb@trifork.com)
 */
public interface AuditLogger {
	
	/**
	 * Logs a message formed by the format and arguments.
	 * 
	 * @param format The format of the message.
	 * @param args The arguments to be inserted into the format.
	 */
	void log(String format, Object...args);
}
