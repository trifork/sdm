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

public class ColumnMapVO {
    private boolean isPid;
    private String tableColumnName;
    private String feedColumnName;
    private int feedPosition;
    private int dataType;
    private Integer maxLength;

    public boolean isPid() {
        return isPid;
    }

    public void setPid(boolean pid) {
        isPid = pid;
    }

    public String getTableColumnName() {
        return tableColumnName;
    }

    public void setTableColumnName(String tableColumnName) {
        this.tableColumnName = tableColumnName;
    }

    public String getFeedColumnName() {
        return feedColumnName;
    }

    public void setFeedColumnName(String feedColumnName) {
        this.feedColumnName = feedColumnName;
    }

    public int getFeedPosition() {
        return feedPosition;
    }

    public void setFeedPosition(int feedPosition) {
        this.feedPosition = feedPosition;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ColumnMapVO that = (ColumnMapVO) o;

        if (dataType != that.dataType) return false;
        if (feedPosition != that.feedPosition) return false;
        if (isPid != that.isPid) return false;
        if (feedColumnName != null ? !feedColumnName.equals(that.feedColumnName) : that.feedColumnName != null)
            return false;
        if (maxLength != null ? !maxLength.equals(that.maxLength) : that.maxLength != null) return false;
        if (!tableColumnName.equals(that.tableColumnName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (isPid ? 1 : 0);
        result = 31 * result + tableColumnName.hashCode();
        result = 31 * result + (feedColumnName != null ? feedColumnName.hashCode() : 0);
        result = 31 * result + feedPosition;
        result = 31 * result + dataType;
        result = 31 * result + (maxLength != null ? maxLength.hashCode() : 0);
        return result;
    }
}
