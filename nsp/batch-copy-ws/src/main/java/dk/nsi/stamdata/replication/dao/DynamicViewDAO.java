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

import com.google.inject.Provider;
import dk.nsi.stamdata.replication.exceptions.DynamicViewException;
import dk.nsi.stamdata.replication.vo.ColumnMapVO;
import dk.nsi.stamdata.replication.vo.ViewMapVO;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

@Singleton
public class DynamicViewDAO {
    private final Provider<Connection> connectionProvider;

    @Inject
    public DynamicViewDAO(Provider<Connection> connectionProvider)
    {
        this.connectionProvider = connectionProvider;
    }

    public ViewMapVO getViewMapForView(String register, String dataType, long version) throws SQLException {
        String sql = "SELECT idSKRSViewMapping,tableName,createdDate FROM SKRSViewMapping WHERE register=? AND " +
                "datatype=? AND version=?";
        ArrayListHandler handler = new ArrayListHandler();
        QueryRunner qr = new QueryRunner();
        Connection conn = null;
        try {
            conn = connectionProvider.get();
            List<Object[]> viewMaps = qr.query(conn, sql, handler, register, dataType, version);
            if (viewMaps.size() < 1) {
                throw new DynamicViewException("View not found for " + register + "/" + dataType + "/" + version);
            } else if (viewMaps.size() > 1) {
                throw new DynamicViewException("Multiple views found for " + register + "/" + dataType + "/" + version);
            }
            Long id = (Long) viewMaps.get(0)[0];
            String tableName = (String) viewMaps.get(0)[1];
            Timestamp createTime = (Timestamp) viewMaps.get(0)[2];

            List<ColumnMapVO> columnMaps = getColumnMapsForView(conn, id);
            ViewMapVO viewMap = new ViewMapVO();
            viewMap.setCreatedDate(createTime);
            viewMap.setDatatype(dataType);
            viewMap.setRegister(register);
            viewMap.setTableName(tableName);
            viewMap.setVersion(version);
            for (ColumnMapVO columnMap : columnMaps) {
                viewMap.addColumn(columnMap);
            }
            return viewMap;
        } finally {
            DbUtils.close(conn);
        }
    }

    private List<ColumnMapVO> getColumnMapsForView(Connection conn, Long viewMapId) throws SQLException {
        List<ColumnMapVO> columnMaps = new LinkedList<ColumnMapVO>();
        String sql = "SELECT isPID,tableColumnName,feedColumnName,feedPosition,dataType,maxLength FROM SKRSColumns "+
                " WHERE viewMap=? ORDER BY feedPosition";
        ArrayListHandler handler = new ArrayListHandler();
        QueryRunner qr = new QueryRunner();
        List<Object[]> columnMappings = qr.query(conn, sql, handler, viewMapId);
        for (Object[] columnMap : columnMappings) {
            ColumnMapVO map = new ColumnMapVO();
            map.setPid((Integer) columnMap[0] > 0);
            map.setTableColumnName((String) columnMap[1]);
            map.setFeedColumnName((String) columnMap[2]);
            map.setFeedPosition((Integer) columnMap[3]);
            map.setDataType((Integer) columnMap[4]);
            map.setMaxLength((Integer) columnMap[5]);
            columnMaps.add(map);
        }
        return columnMaps;
    }
}
