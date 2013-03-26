package dk.nsi.stamdata.replication.dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import dk.nsi.stamdata.replication.vo.ColumnMapVO;
import dk.nsi.stamdata.replication.vo.ViewMapVO;
import dk.nsi.stamdata.replication.webservice.GuiceTestRunner;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
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
    private Provider<Connection> connectionProvider;

    @Test
    public void testGetViewMap() throws SQLException {
        Long id = insertTestMapping();

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
        assertEquals(100, column1.getMaxLength());
        assertEquals("test_column", column1.getTableColumnName());
        assertEquals("test_feed_column", column1.getFeedColumnName());

        removeMapping(id);
    }

    private void removeMapping(Long id) throws SQLException {
        QueryRunner qr = new QueryRunner();
        Connection connection = connectionProvider.get();
        try {
            qr.update(connection, "DELETE FROM SKRSViewMapping WHERE idSKRSViewMapping=?", id);
        } finally {
            DbUtils.close(connection);
        }
    }

    private Long insertTestMapping() throws SQLException {
        ScalarHandler handler = new ScalarHandler();
        QueryRunner qr = new QueryRunner();
        Connection connection = connectionProvider.get();
        try {
            String sql = "INSERT INTO SKRSViewMapping (register, datatype, version, tableName, createdDate) VALUES (?, ?, ?, ?, ?)";
            qr.update(connection, sql, testRegister, testDatatype, 1, testTableName, new Timestamp((new Date()).getTime()));

            String select = "SELECT idSKRSViewMapping FROM SKRSViewMapping WHERE register=? AND datatype=? AND version=?";
            Long id = (Long) qr.query(connection, select, handler, testRegister, testDatatype, new Integer(1));
            insertTestColumnMapping(connection, id);

            return id;
        } finally {
            DbUtils.close(connection);
        }
    }

    private void insertTestColumnMapping(Connection connection, Long viewId) throws SQLException {
        QueryRunner qr = new QueryRunner();
        String insert = "INSERT INTO SKRSColumns (viewMap, isPID, tableColumnName, feedColumnName, feedPosition, dataType, maxLength)";
        insert += " VALUES(?, ?, ?, ?, ?, ?, ?)";
        qr.update(connection, insert, viewId, true, "test_column", "test_feed_column", 1, -1, 100);
    }

}
