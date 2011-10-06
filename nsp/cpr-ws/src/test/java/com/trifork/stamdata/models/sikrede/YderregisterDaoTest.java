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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.Date;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;


public class YderregisterDaoTest extends AbstractDaoTest
{
	@Before
	public void init()
	{
		purgeTable("Yderregister");
	}

	private void insertInYderregistertable()
	{
		Yderregister yderregister = new Yderregister();
		yderregister.setNummer(1234);
		yderregister.setBynavn("test");
		yderregister.setModifiedDate(new Date());
		yderregister.setCreatedDate(new Date());
		yderregister.setValidFrom(DateTime.now().minusDays(1).toDate());
		yderregister.setValidTo(DateTime.now().plusDays(1).toDate());

		insertInTable(yderregister);
	}

	@Test
	public void verifyMapping() throws SQLException
	{
		assertTrue(true);
		insertInYderregistertable();

		Yderregister yderregister1 = fetcher.fetch(Yderregister.class, 1234);
		assertEquals("test", yderregister1.getBynavn());
	}
}
