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

package com.trifork.stamdata.importer.sks;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.trifork.stamdata.config.MySQLConnectionManager;


public class SksIntegrationsTest {

	public File SHAKCompleate;

	@After
	@Before
	public void cleanDb() throws Exception {

		SHAKCompleate = FileUtils.toFile(getClass().getClassLoader().getResource("data/sks/SHAKCOMPLETE.TXT"));

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		Statement stmt = con.createStatement();
		stmt.executeQuery("truncate table Organisation");
		stmt.close();
		con.close();
	}

	@Test
	public void testSHAKImport() throws Throwable {

		ArrayList<File> files = new ArrayList<File>();
		files.add(SHAKCompleate);
		SksImporter importer = new SksImporter();
		importer.run(files);

		Connection con = MySQLConnectionManager.getConnection();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("Select count(*) from Organisation");
		rs.next();
		assertEquals(9717, rs.getInt(1));

		rs = stmt.executeQuery("Select count(*) from Organisation where Organisationstype ='Sygehus' ");
		rs.next();
		assertEquals(689, rs.getInt(1));

		rs = stmt.executeQuery("Select count(*) from Organisation where Organisationstype ='Afdeling' ");
		rs.next();
		assertEquals(9028, rs.getInt(1));
		stmt.close();
		con.close();
	}

}
