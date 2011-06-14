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

package com.trifork.stamdata.logging;

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
