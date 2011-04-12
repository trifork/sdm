// Stamdata - Copyright (C) 2011 National Board of e-Health (NSI)
// 
// All source code and information supplied as part of Stamdata is
// copyright to National Board of e-Health.
// 
// The source code has been released under a dual license - meaning you can
// use either licensed version of the library with your code.
// 
// It is released under the Common Public License 1.0, a copy of which can
// be found at the link below.
// http://www.opensource.org/licenses/cpl1.0.php
// 
// It is released under the LGPL (GNU Lesser General Public License), either
// version 2.1 of the License, or (at your option) any later version. A copy
// of which can be found at the link below.
// http://www.gnu.org/copyleft/lesser.html

package dk.trifork.sdm.config;

import org.slf4j.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class MySQLConnectionManager {
    private static Logger logger = LoggerFactory.getLogger(MySQLConnectionManager.class);

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection con = DriverManager.getConnection(Configuration.getString("db.url")+getDBName(), Configuration.getString("db.user"), Configuration.getString("db.pwd"));
            con.setAutoCommit(false);
            return con;
        } catch (Exception e) {
            logger.error("Error creating MySQL database connection", e);
        }
        return null;
    }

    public static Connection getAutoCommitConnection() {
        try {
            Connection con = getConnection();
            con.setAutoCommit(true);
            return con;
        } catch (Exception e) {
            logger.error("Error creating MySQL database connection", e);
        }
        return null;
    }

    public static String getDBName() {
    	return Configuration.getString("db.database");
    }
    
    public static String getHousekeepingDBName() {

    	String value = Configuration.getString("db.housekeepingdatabase");
    	return value != null
    		? value
    		: Configuration.getString("db.database");
    }

    public static void close(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            } else logger.warn("Cannot commit and close connection, because connection == null");
        }
        catch (Exception e) {
            logger.error("Could not close connection", e);
        }
    }

    public static void close(Statement stmt, Connection connection) {
        try {
            if (stmt != null) {
                stmt.close();
            } else logger.warn("Cannot close stmt, because stmt == null");
        }
        catch (Exception e) {
            logger.error("Could not close stmt", e);
        }
        finally {
            close(connection);
        }
    }

}
