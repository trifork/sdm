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

import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.servlet.ServletModule;
import com.trifork.stamdata.replication.logging.annotations.ClientIp;


public class LoggingModule extends ServletModule implements Module {

	@Override
	protected void configureServlets() {
		requireBinding(Key.get(String.class, ClientIp.class));
		filter("*").through(RequestIdLoggingFilter.class);
		filter("*").through(ClientIpLoggingFilter.class);
		bind(DatabaseAuditLogger.class);
		bind(SLF4JAuditLogger.class);
		bind(AuditLogger.class).to(CompositeAuditLogger.class);
	}

}
