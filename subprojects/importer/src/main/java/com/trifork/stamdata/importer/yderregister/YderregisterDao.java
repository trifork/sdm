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

package com.trifork.stamdata.importer.yderregister;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.trifork.stamdata.importer.exceptions.FilePersistException;
import com.trifork.stamdata.persistence.AuditingPersister;

public class YderregisterDao extends AuditingPersister {

	public YderregisterDao(Connection con) {
		super(con);
	}

	public int getLastLoebenummer() throws FilePersistException
	{
		int latestInDB = 0;
		try {
			Statement stm = connection.createStatement();
			ResultSet rs = stm.executeQuery("SELECT MAX(Loebenummer) FROM YderLoebenummer");
			if (rs.next()) {
				latestInDB = rs.getInt(1);
			}
		} catch (SQLException sqle) {
			try { connection.close();} catch (Exception e)  {/*ignore*/}
			throw new FilePersistException("An error occured while querying latest loebenummer " + sqle.getMessage(), sqle);
		}
		return latestInDB;
	}

	public void setLastLoebenummer(int loebeNummer) throws FilePersistException {
		try {
			Statement stm = connection.createStatement();
			stm.execute("INSERT INTO YderLoebenummer (Loebenummer) values (" + loebeNummer + "); ");
		} catch (SQLException sqle) {
			throw new FilePersistException("Det opstoed en fejl ved skrivning af løbenummer til databasen under indlæsning af et yderregister: " + sqle.getMessage(), sqle );
		}

	}


}
