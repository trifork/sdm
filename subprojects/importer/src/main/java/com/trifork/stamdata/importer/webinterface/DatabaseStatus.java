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

package com.trifork.stamdata.importer.webinterface;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trifork.stamdata.importer.config.MySQLConnectionManager;


/**
 * @author Jan Buchholdt (jbu@trifork.com)
 */
public class DatabaseStatus {

	private final Logger logger = LoggerFactory.getLogger(DatabaseStatus.class);

	public boolean isAlive() {

		boolean isAlive = false;
		Connection con = null;

		try {
			con = MySQLConnectionManager.getConnection();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT 1");
			rs.next();
			if (1 == rs.getInt(1)) isAlive = true;
		}
		catch (Exception e) {
			logger.error("db connection down", e);
		}
		finally {
			MySQLConnectionManager.close(con);
		}

		return isAlive;
	}
}
