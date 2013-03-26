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

import dk.nsi.stamdata.replication.exceptions.DynamicViewException;
import dk.nsi.stamdata.replication.vo.ColumnMapVO;
import dk.nsi.stamdata.replication.vo.ViewMapVO;

import java.sql.Timestamp;
import java.util.Map;

public class DynamicRow {

    private Map<String,Object> content;
    private final ViewMapVO view;

    public DynamicRow(ViewMapVO view, Map<String, Object> content) {
        this.content = content;
        this.view = view;
    }

    public Map<String, Object> getContent() {
        return content;
    }

    public Timestamp getValidTo() {
        Timestamp result = (Timestamp) content.get("ValidTo");
        if (result == null) {
            result = (Timestamp) content.get("validTo");
        }
        return result;
    }

    public Timestamp getValidFrom() {
        Timestamp result = (Timestamp) content.get("ValidFrom");
        if (result == null) {
            result = (Timestamp) content.get("validFrom");
        }
        return result;
    }

    public Timestamp getModifiedDate() {
        Timestamp result = (Timestamp) content.get("ModifiedDate");
        if (result == null) {
            result = (Timestamp) content.get("modifiedDate");
        }
        return result;
    }

    public long getPid() {
        for (ColumnMapVO column : view.getColumnMaps()) {
            if (column.isPid()) {
                return (Long) content.get(column.getTableColumnName());
            }
        }
        throw new DynamicViewException("Could not find pid column in viewMap:" + view.toString());
    }

}
