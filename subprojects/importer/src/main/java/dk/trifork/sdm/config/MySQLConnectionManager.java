package dk.trifork.sdm.config;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class MySQLConnectionManager {
    private static Logger logger = Logger.getLogger(MySQLConnectionManager.class);

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection con = DriverManager.getConnection(Configuration.getString("db.url")+getDBName(), Configuration.getString("db.sdm.user"), Configuration.getString("db.sdm.pwd"));
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
