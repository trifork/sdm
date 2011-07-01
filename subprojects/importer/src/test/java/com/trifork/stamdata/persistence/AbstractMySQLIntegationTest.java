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

package com.trifork.stamdata.persistence;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Date;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.trifork.stamdata.importer.config.MySQLConnectionManager;


public class AbstractMySQLIntegationTest
{
	protected Date t0;
	protected Date t1;
	protected Date t2;
	protected Date t3;
	protected Date t4;
	protected Date t1000;

	@Before
	public void setup() throws Exception
	{
		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		Statement stmt = con.createStatement();
		stmt.executeUpdate("truncate table TakstVersion");
		stmt.close();
		con.close();
	}

	@Before
	public void initDates()
	{
		t0 = new DateTime(2000, 1, 1, 1, 2, 3, 0).toDate();
		t1 = new DateTime(2001, 1, 1, 1, 2, 3, 0).toDate();
		t2 = new DateTime(2002, 1, 1, 1, 2, 3, 0).toDate();
		t3 = new DateTime(2003, 1, 1, 1, 2, 3, 0).toDate();
		t4 = new DateTime(2003, 1, 1, 1, 2, 3, 0).toDate();
		t1000 = new DateTime(3003, 1, 1, 1, 2, 3, 0).toDate();
	}

	@Test
	public void dummyTest()
	{
	}
}
