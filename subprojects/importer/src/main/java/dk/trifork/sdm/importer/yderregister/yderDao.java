package dk.trifork.sdm.importer.yderregister;

import dk.trifork.sdm.dao.AuditingPersister;
import dk.trifork.sdm.importer.exceptions.FilePersistException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class yderDao extends AuditingPersister {

	public yderDao(Connection con) {
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
