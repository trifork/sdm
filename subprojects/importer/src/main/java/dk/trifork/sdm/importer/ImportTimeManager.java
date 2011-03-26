package dk.trifork.sdm.importer;

import dk.trifork.sdm.config.MySQLConnectionManager;
import dk.trifork.sdm.util.DateUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class ImportTimeManager {
    private static Logger logger = LoggerFactory.getLogger(ImportTimeManager.class);

    public static Calendar getLastImportTime(String spoolername) {
        Connection con = null;
        Statement stmt = null;
        try {
            con = MySQLConnectionManager.getAutoCommitConnection();
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select max(importtime) from Import where spoolername = '" + spoolername + "'");
            if (rs.next()) {
                Calendar cal = DateUtils.toCalendar(rs.getTimestamp(1));
                return cal;
            } else
                return null;
        } catch (Exception e) {
            logger.error("getLastImportTime(" + spoolername + ")", e);
            return null;
        }
        finally {
            MySQLConnectionManager.close(stmt, con);
        }

    }

    public static void setImportTime(String spoolerName, Calendar importTime) {
        Connection con = null;
        Statement stmt = null;
        try {
            con = MySQLConnectionManager.getAutoCommitConnection();
            stmt = con.createStatement();
            stmt.executeUpdate("insert into Import values('" + DateUtils.toMySQLdate(importTime) + "', '" + spoolerName + "')");
        } catch (Exception e) {
            logger.error("getLastImportTime(" + spoolerName + ")", e);
        }
        finally {
            MySQLConnectionManager.close(stmt, con);
        }

    }

}
