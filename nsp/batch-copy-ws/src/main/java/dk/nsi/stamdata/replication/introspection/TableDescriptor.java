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

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * This class describes columns and types of a table
 */
public class TableDescriptor {

    private Map<String, Integer> columnTypeMap = new HashMap<String, Integer>();
    private String primaryKeyName;

    /**
     * Add a column with type to this table
     * @param name name of the column
     * @param type type of the column (as in java.sql.Types)
     */
    public void addColumn(String name, Integer type) {
        columnTypeMap.put(name, type);
    }

    /**
     * Sets the primary key name
     * @param primaryKeyName name of primary key
     */
    public void setPrimaryColumnName(String primaryKeyName) {
        this.primaryKeyName = primaryKeyName;
    }

    /**
     * Get name of primary key
     * @return primary key name
     */
    public String getPrimaryKeyName() {
        return primaryKeyName;
    }

    /**
     * Gets a map of columns and types (types as in java.sql.Types)
     * @return column type map
     */
    public Map<String, Integer> getColumnTypeMap() {
        return columnTypeMap;
    }

    /**
     * Does this table have the required fields needed to be replicated through batch copy ws
     * @return true if the required fields are present.
     */
    public boolean isStamdataCopyable() {
        if (columnTypeMap.containsKey("ValidFrom") && columnTypeMap.containsKey("ValidTo") &&
                columnTypeMap.containsKey("ModifiedDate")) {
            int validFromType = columnTypeMap.get("ValidFrom");
            int validToType = columnTypeMap.get("ValidTo");
            int modifiedDateType = columnTypeMap.get("ModifiedDate");
            if (validFromType == Types.TIMESTAMP && validToType == Types.TIMESTAMP &&
                    modifiedDateType == Types.TIMESTAMP) {
                if (primaryKeyName != null) {
                    return true;
                }
            }
        }
        return false;
    }
}
