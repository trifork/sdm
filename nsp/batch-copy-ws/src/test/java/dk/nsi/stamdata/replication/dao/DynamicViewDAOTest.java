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
package dk.nsi.stamdata.replication.dao;

import com.google.inject.Inject;
import dk.nsi.stamdata.replication.vo.ColumnMapVO;
import dk.nsi.stamdata.replication.vo.ViewMapVO;
import dk.nsi.stamdata.replication.webservice.GuiceTestRunner;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(GuiceTestRunner.class)
public class DynamicViewDAOTest {

    private final String testRegister = "test_register";
    private final String testDatatype = "test_type";
    private final String testTableName = "TestTable";

    @Inject
    private DynamicViewDAO dao;

    @Inject
    private Connection connection;

    private Long id;

    @Before
    public void setUp() throws SQLException {
        id = insertTestMapping();
    }

    @After
    public void tearDown() throws SQLException {
        removeMapping(id);
        DbUtils.close(connection);
    }

    @Test
    public void testGetViewMap() throws SQLException {

        ViewMapVO viewMapForView = dao.getViewMapForView(testRegister, testDatatype, 1);
        assertNotNull(viewMapForView);

        assertEquals(testRegister, viewMapForView.getRegister());
        assertEquals(testDatatype, viewMapForView.getDatatype());
        assertEquals(1, viewMapForView.getVersion());
        assertEquals(testTableName, viewMapForView.getTableName());
        // Not sure what created date is, but make sure its not null at least
        assertNotNull(viewMapForView.getCreatedDate());

        List<ColumnMapVO> columnMaps = viewMapForView.getColumnMaps();
        assertEquals(1, columnMaps.size());

        ColumnMapVO column1 = columnMaps.iterator().next();
        assertEquals(-1, column1.getDataType());
        assertEquals(1, column1.getFeedPosition());
        assertEquals((Integer)100, column1.getMaxLength());
        assertEquals("test_column", column1.getTableColumnName());
        assertEquals("test_feed_column", column1.getFeedColumnName());

        removeMapping(id);
    }

    private void removeMapping(Long id) throws SQLException {
        QueryRunner qr = new QueryRunner();
        qr.update(connection, "DELETE FROM SKRSViewMapping WHERE idSKRSViewMapping=?", id);
    }

    private Long insertTestMapping() throws SQLException {
        ScalarHandler handler = new ScalarHandler();
        QueryRunner qr = new QueryRunner();
        String sql = "INSERT INTO SKRSViewMapping (register, datatype, version, tableName, createdDate) VALUES (?, ?, ?, ?, ?)";
        qr.update(connection, sql, testRegister, testDatatype, 1, testTableName, new Timestamp((new Date()).getTime()));

        String select = "SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register=? AND datatype=? AND version=?";
        Long id = (Long) qr.query(connection, select, handler, testRegister, testDatatype, 1);
        insertTestColumnMapping(connection, id);

        return id;
    }

    private void insertTestColumnMapping(Connection connection, Long viewId) throws SQLException {
        QueryRunner qr = new QueryRunner();
        String insert = "INSERT INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength)";
        insert += " VALUES(?, ?, ?, ?, ?, ?, ?)";
        qr.update(connection, insert, viewId, true, "test_column", "test_feed_column", 1, -1, 100);
    }

}
