// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
// 
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
// 
// Contributor(s): Contributors are attributed in the source code
// where applicable.
// 
// The Original Code is "Stamdata".
// 
// The Initial Developer of the Original Code is Trifork Public A/S.
// 
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
// 
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

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
