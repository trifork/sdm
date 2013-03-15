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
package dk.nsi.stamdata.replication.introspection;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.apache.commons.dbutils.DbUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

@Singleton
public class TableIntrospector {

    @Inject
    private Provider<Connection> connectionProvider;

    /**
     * Introspect a given table
     * @param tableName table to introspect
     * @return a table descriptor
     * @throws SQLException
     */
    public TableDescriptor performIntrospection(String tableName) throws SQLException {
        TableDescriptor descriptor = new TableDescriptor();
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = connectionProvider.get();
            DatabaseMetaData metaData = connection.getMetaData();
            resultSet = metaData.getColumns(null, null, tableName, null);
            while (resultSet.next()) {
                String columnName = resultSet.getString("COLUMN_NAME");
                int columnType = resultSet.getInt("DATA_TYPE");
                descriptor.addColumn(columnName, columnType);
            }
            DbUtils.close(resultSet);
            resultSet = metaData.getPrimaryKeys(null, null, tableName);
            if (resultSet.next()) {
                String columnName = resultSet.getString("COLUMN_NAME");
                descriptor.setPrimaryColumnName(columnName);
            } else {
                // TODO Throw introspection exception
            }
        } finally {
            DbUtils.close(resultSet);
            DbUtils.close(connection);
        }
        return descriptor;
    }



}
