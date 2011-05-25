
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

package com.trifork.stamdata.replication.db;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import com.google.common.collect.Sets;
import com.google.inject.servlet.ServletModule;
import com.trifork.stamdata.Nullable;
import com.trifork.stamdata.db.SessionFactoryModule;
import com.trifork.stamdata.replication.gui.models.Client;
import com.trifork.stamdata.replication.gui.models.User;
import com.trifork.stamdata.replication.logging.LogEntry;
import com.trifork.stamdata.replication.security.dgws.Authorization;
import com.trifork.stamdata.views.Views;


public class DatabaseModule extends ServletModule {

	private final String driverClass;
	private final String hibernateDialect;
	private final String jdbcURL;
	private final String username;
	private final String password;

	public DatabaseModule(String driverClass, String hibernateDialect, String jdbcURL, String username, @Nullable String password) {
		
		this.driverClass = checkNotNull(driverClass);
		this.hibernateDialect = checkNotNull(hibernateDialect);
		this.jdbcURL = checkNotNull(jdbcURL);
		this.username = checkNotNull(username);
		this.password = password;
	}

	@Override
	protected final void configureServlets() {
		// DISCOVER ALL ENTITY CLASSES

		Set<Class<?>> classes = Sets.newHashSet();
		classes.addAll(Views.findAllViews());
		classes.add(User.class);
		classes.add(LogEntry.class);
		classes.add(Client.class);
		classes.add(Authorization.class);

		install(new SessionFactoryModule(driverClass, hibernateDialect, jdbcURL, username, password, classes));
	}
}
