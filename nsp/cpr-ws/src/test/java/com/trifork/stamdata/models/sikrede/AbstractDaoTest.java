/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */
package com.trifork.stamdata.models.sikrede;

import static org.junit.Assert.assertNotNull;

import java.sql.SQLException;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.trifork.stamdata.Fetcher;
import com.trifork.stamdata.models.BaseTemporalEntity;

import dk.nsi.stamdata.cpr.ComponentController;

public abstract class AbstractDaoTest
{
	protected static Session session;
	protected Fetcher fetcher;

	@Before
	public void setupSession()
	{
		Injector injector = Guice.createInjector(Stage.DEVELOPMENT, new ComponentController.ComponentModule());

		session = injector.getInstance(Session.class);
		fetcher = injector.getInstance(Fetcher.class);
	}

	protected void purgeTable(String table)
	{
		assertNotNull(table);
		session.createSQLQuery("TRUNCATE " + table).executeUpdate();
	}

	protected void insertInTable(BaseTemporalEntity entity)
	{
		session.getTransaction().begin();
		session.save(entity);
		session.flush();
		session.getTransaction().commit();
	}

	@Test
	public abstract void verifyMapping() throws SQLException;

}
