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
package dk.nsi.stamdata.replication.vo;

import java.util.*;

public class ViewMapVO {
    private String register;
    private String datatype;
    private long version;
    private String tableName;
    private Date createdDate;

    private List<ColumnMapVO> columnMaps = new LinkedList<ColumnMapVO>();

    public String getRegister() {
        return register;
    }

    public void setRegister(String register) {
        this.register = register;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public void addColumn(ColumnMapVO columnMapVO) {
        columnMaps.add(columnMapVO);
    }

    public List<ColumnMapVO> getColumnMaps() {
        return columnMaps;
    }

    @Override
    public String toString() {
        return "ViewMapVO{" +
                "register='" + register + '\'' +
                ", datatype='" + datatype + '\'' +
                ", version=" + version +
                ", tableName='" + tableName + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ViewMapVO viewMapVO = (ViewMapVO) o;

        if (version != viewMapVO.version) return false;
        if (columnMaps != null ? !columnMaps.equals(viewMapVO.columnMaps) : viewMapVO.columnMaps != null) return false;
        if (createdDate != null ? !createdDate.equals(viewMapVO.createdDate) : viewMapVO.createdDate != null)
            return false;
        if (!datatype.equals(viewMapVO.datatype)) return false;
        if (!register.equals(viewMapVO.register)) return false;
        if (!tableName.equals(viewMapVO.tableName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = register.hashCode();
        result = 31 * result + datatype.hashCode();
        result = 31 * result + (int) (version ^ (version >>> 32));
        result = 31 * result + tableName.hashCode();
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        result = 31 * result + (columnMaps != null ? columnMaps.hashCode() : 0);
        return result;
    }
}
