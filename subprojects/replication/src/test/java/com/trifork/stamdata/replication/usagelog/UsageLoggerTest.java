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

package com.trifork.stamdata.replication.usagelog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.trifork.stamdata.replication.DatabaseHelper;
import com.trifork.stamdata.replication.logging.UsageLogger;
import com.trifork.stamdata.views.usagelog.UsageLogEntry;


public class UsageLoggerTest {
	private static Session session;
	private UsageLogger logger;

	@BeforeClass
	public static void init() throws Exception {
		DatabaseHelper db = new DatabaseHelper("replication", UsageLogEntry.class);
		session = db.openSession();
		Query query = session.createQuery("delete from UsageLogEntry");
		query.executeUpdate();
	}

	@Before
	public void setUp() {
		logger = new UsageLogger(session);
		session.beginTransaction();
	}

	@After
	public void tearDown() {
		session.getTransaction().rollback();
	}
	
	@Test
	public void canSaveUsageLogInformation() {
		List<?> logs = session.createCriteria(UsageLogEntry.class).list();
		assertEquals(0, logs.size());

		logger.log("CVR:12345678", "/my/objects/v1", 30);
		
		logs = session.createCriteria(UsageLogEntry.class).list();
		assertEquals(1, logs.size());
		
		UsageLogEntry entry = (UsageLogEntry) logs.get(0);
		assertEquals("CVR:12345678", entry.clientId);
		assertNotNull(entry.modifiedDate);
		assertEquals("/my/objects/v1", entry.type);
		assertEquals(30, entry.amount);
	}
}
