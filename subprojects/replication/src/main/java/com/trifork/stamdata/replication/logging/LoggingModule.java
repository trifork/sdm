package com.trifork.stamdata.replication.logging;

import com.google.inject.Module;
import com.google.inject.PrivateModule;


public class LoggingModule extends PrivateModule implements Module {

	@Override
	protected void configure() {

		bind(DatabaseAuditLogger.class);
		bind(SLF4JAuditLogger.class);
		bind(AuditLogger.class).to(CompositeAuditLogger.class);
		expose(AuditLogger.class);
		
		// The Database logger is exposed too since it is needed
		// in the LogController, for historical reasons.
		
		expose(DatabaseAuditLogger.class);
	}
}
