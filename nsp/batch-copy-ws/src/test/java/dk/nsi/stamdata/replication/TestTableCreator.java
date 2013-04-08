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
package dk.nsi.stamdata.replication;

import com.google.inject.Inject;
import com.google.inject.Provider;
import dk.nsi.stamdata.replication.models.Client;
import dk.nsi.stamdata.replication.models.ClientDao;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.*;

public class TestTableCreator {
    public final static String TEST_TABLE = "testtable_skrs";
    public final static String TEST_REGISTER = "testreg";
    public final static String TEST_DATATYPE = "testtype";
    public static final String WHITELISTED_CVR = "25520041";

    @Inject
    private Provider<Connection> connectionProvider;
    @Inject
    private ClientDao clientDao;
    @Inject
    private Session session;

    public void createTestTable() throws SQLException {
        String sql = "CREATE  TABLE IF NOT EXISTS `testtable_skrs` (\n" +
                "  `PID` BIGINT(15) NOT NULL AUTO_INCREMENT ,\n" +
                "  `tekst` VARCHAR(200) NULL ,\n" +
                "  `tal` INT NULL ,\n" +
                "  `stort_tal` BIGINT NULL ,\n" +
                "  `decimal_tal` DECIMAL(8,2) NULL ,\n" +
                "  `dato` DATE NULL ,\n" +
                "  `dato_tid` DATETIME NULL ,\n" +
                "  `floatingpoint` FLOAT NULL ,\n" +
                "  `flag` TINYINT(1) NULL ,\n" +
                "  `ModifiedDate` DATETIME NULL ,\n" +
                "  `ValidFrom` DATETIME NULL ,\n" +
                "  `ValidTo` DATETIME NULL ,\n" +
                "  PRIMARY KEY (`PID`) )\n" +
                "ENGINE = InnoDB";
        Connection conn = null;
        QueryRunner run = new QueryRunner();
        try {
            conn = connectionProvider.get();
            run.update(conn, sql);
            run.update(conn, "DELETE FROM testtable_skrs");
        } finally {
            DbUtils.close(conn);
        }
    }


    public long createAndWhiteListForTestView() throws SQLException {
        long testViewId = insertNewView(TEST_REGISTER, TEST_DATATYPE, 1L, TEST_TABLE);
        addColumnToView(testViewId, true,  "PID",          0, -5, null);
        addColumnToView(testViewId, false, "tekst",        1, 12, 200);
        addColumnToView(testViewId, false, "tal",          2,  4, null);
        addColumnToView(testViewId, false, "stort_tal",    3, -5, null);
        addColumnToView(testViewId, false, "decimal_tal",  4,  3, null);
        addColumnToView(testViewId, false, "dato",         5, 91, null);
        addColumnToView(testViewId, false, "dato_tid",     6, 93, null);
        addColumnToView(testViewId, false, "floatingpoint",7,  6, null);
        addColumnToView(testViewId, false, "flag",         8, 16, null);
        addColumnToView(testViewId, false, "ModifiedDate", 9, 93, null);
        addColumnToView(testViewId, false, "ValidFrom",   10, 93, null);
        addColumnToView(testViewId, false, "ValidTo",     11, 93, null);
        whiteList(TEST_REGISTER + "/" + TEST_DATATYPE + "/v1");

        return testViewId;
    }

    public void removeView(long viewId) throws SQLException {
        Connection conn = null;
        QueryRunner run = new QueryRunner();
        try {
            conn = connectionProvider.get();
            run.update(conn, "DELETE FROM SKRSViewMapping WHERE idSKRSViewMapping=?", viewId);
        } finally {
            DbUtils.close(conn);
        }
    }

    public void insertTestRow(String tekst, int tal, int stortTal, double decimal, java.util.Date dato, java.util.Date datoTid,
                               float flydende, boolean falg) throws SQLException {
        String sql = "INSERT INTO testtable_skrs(tekst, tal, stort_tal, decimal_tal, dato, dato_tid, floatingpoint, flag," +
                " ModifiedDate, ValidFrom, ValidTo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Timestamp now = new Timestamp((new java.util.Date()).getTime());
        Connection conn = null;
        QueryRunner run = new QueryRunner();
        try {
            conn = connectionProvider.get();
            run.update(conn, sql, tekst, tal, stortTal, decimal, dato, new Timestamp((new java.util.Date()).getTime()), flydende,
                    falg, now, now, null);
        } finally {
            DbUtils.close(conn);
        }
    }

    public void updateModifiedDateOnRecordsWithTekst(String tekst) throws SQLException {
        String sql = "UPDATE testtable_skrs SET ModifiedDate=? WHERE tekst=?";
        Timestamp now = new Timestamp((new java.util.Date()).getTime());
        Connection conn = null;
        QueryRunner run = new QueryRunner();
        try {
            conn = connectionProvider.get();
            run.update(conn, sql, now, tekst);
        } finally {
            DbUtils.close(conn);
        }
    }

    private void addColumnToView(long viewId, boolean isPid, String columnName, int position, int dataType, Integer maxLength) throws SQLException {
        String sql = "INSERT INTO SKRSColumns(viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength)";
        sql += " VALUES (?,?,?,?,?,?,?)";
        Connection conn = null;
        QueryRunner run = new QueryRunner();
        try {
            conn = connectionProvider.get();
            run.update(conn, sql, viewId, isPid, columnName, columnName, position, dataType, maxLength);
        } finally {
            DbUtils.close(conn);
        }
    }

    private long insertNewView(String register, String datatype, Long version, String tableName) throws SQLException {
        String sql1 = "INSERT INTO SKRSViewMapping(register, datatype, version, tableName, createdDate) ";
        sql1 += " VALUES(?,?,?,?,?)";
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        QueryRunner run = new QueryRunner();
        try {
            conn = connectionProvider.get();
            preparedStatement = conn.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);

            run.fillStatement(preparedStatement, register, datatype, version, tableName, new Timestamp((new java.util.Date()).getTime()));
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getLong(1);
            }
        } finally {
            DbUtils.close(preparedStatement);
            DbUtils.close(conn);
        }
        return -1;
    }

    private void whiteList(String permission) {
        Transaction t = session.beginTransaction();
        Client client = clientDao.findByCvr(WHITELISTED_CVR);
        if (client == null) {
            client = clientDao.create("Region Syd", String.format("CVR:%s-UID:1234", WHITELISTED_CVR));
        }
        client.addPermission(permission);
        session.persist(client);
        t.commit();
    }
}
