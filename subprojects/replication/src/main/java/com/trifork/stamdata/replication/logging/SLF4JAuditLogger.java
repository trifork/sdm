package com.trifork.stamdata.replication.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logs audit messages using to a file using SLF4J.
 * 
 * @author Thomas Børlum (thb@trifork.com)
 */
public class SLF4JAuditLogger implements AuditLogger {

	private static final Logger logger = LoggerFactory.getLogger(AuditLogger.class);
	
	@Override
	public void log(String format, Object... args) {
		
		logger.info(String.format(format, args));
	}
}
