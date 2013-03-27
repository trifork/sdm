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
package dk.nsi.stamdata.replication.dynamic;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import dk.nsi.stamdata.replication.exceptions.DynamicViewException;
import dk.nsi.stamdata.replication.vo.ColumnMapVO;
import dk.nsi.stamdata.replication.vo.ViewMapVO;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.Instant;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Singleton
public class DynamicRowFetcher {

    private static final Logger logger = Logger.getLogger(DynamicRowFetcher.class);
    private final Provider<Connection> connectionProvider;

    @Inject
    public DynamicRowFetcher(Provider<Connection> connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public List<DynamicRow> fetchRows(ViewMapVO view, long fromPid, Instant fromModifiedDate, int limit) throws SQLException {
        List<DynamicRow> resultRows = new LinkedList<DynamicRow>();
        String pidColumn = findPidColumnName(view);
        String[] columns = selectColumnsFromRow(view);
        String columnsForSelect = StringUtils.join(columns, ",");
        logger.debug("Fetching rows with columns: " + columnsForSelect);

        String queryString = String.format("SELECT " + columnsForSelect + " FROM %s WHERE " +
                "(" + pidColumn + " > ? AND ModifiedDate = ?) OR " +
                "(ModifiedDate > ?) " +
                "ORDER BY ModifiedDate, " + pidColumn + " LIMIT %d", view.getTableName(), limit);

        QueryRunner qr = new QueryRunner();
        MapListHandler handler = new MapListHandler();
        Connection conn = null;
        try {
            conn = connectionProvider.get();
            Timestamp fromModifiedTimestamp = new Timestamp(fromModifiedDate.getMillis());
            List<Map<String,Object>> rows = qr
                    .query(conn, queryString, handler, fromPid, fromModifiedTimestamp, fromModifiedTimestamp);
            for (Map<String,Object> objMap : rows) {
                resultRows.add(createDynamicRowFromObjects(view, objMap));
            }
        } finally {
            DbUtils.close(conn);
        }
        return resultRows;
    }

    private String findPidColumnName(ViewMapVO view) {
        for (ColumnMapVO column : view.getColumnMaps()) {
            if (column.isPid()) {
                return column.getTableColumnName();
            }
        }
        throw new DynamicViewException("PID Column not found for view " + view.toString());
    }

    private String[] selectColumnsFromRow(ViewMapVO view) {
        List<ColumnMapVO> columnMaps = view.getColumnMaps();
        List<String> columnNames = new LinkedList<String>();
        for (ColumnMapVO column : columnMaps) {
            columnNames.add(column.getTableColumnName());
        }
        return columnNames.toArray(new String[columnNames.size()]);
    }

    private DynamicRow createDynamicRowFromObjects(ViewMapVO view, Map<String,Object> objMap) {
        return new DynamicRow(view, objMap);
    }
}
