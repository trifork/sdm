package dk.trifork.sdm.dao;

import dk.trifork.sdm.config.MySQLConnectionManager;
import junit.framework.TestCase;

import java.sql.Connection;

public class ConnectionTest extends TestCase{
	
	public void testConnection() throws Exception{
		Connection con = MySQLConnectionManager.getConnection();
		con.close();
	}

}
