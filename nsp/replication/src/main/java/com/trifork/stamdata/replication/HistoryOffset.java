// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
// 
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
// 
// Contributor(s): Contributors are attributed in the source code
// where applicable.
// 
// The Original Code is "Stamdata".
// 
// The Initial Developer of the Original Code is Trifork Public A/S.
// 
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
// 
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

package com.trifork.stamdata.replication;

import static java.lang.Long.parseLong;

import java.util.Date;

import org.apache.commons.lang.StringUtils;


/**
 * The format is:
 *
 * [-Updated Date-][-----ID-----]
 *
 * Each of the two section is 10 characters long and padded width 0's. The
 * updated date is represented in seconds since the last epoch.
 */
public class HistoryOffset {

	protected static int HISTORY_ID_SEGMENT_LENGTH = 10;
	protected static int SECS_TO_MILLIS = 1000;

	protected String recordID;
	protected Date modifiedDate;

	public HistoryOffset(String id, Date modifiedDate) {

		this.recordID = StringUtils.leftPad(id, 10, "0");
		this.modifiedDate = modifiedDate;
	}

	public HistoryOffset(String offsetString) {

		if (offsetString == null) {
			recordID = "0000000000";
			modifiedDate = new Date(0);
		}
		else {
			offsetString = StringUtils.leftPad(offsetString, 20, "0");

			String sinceDateParam = offsetString.substring(0, HISTORY_ID_SEGMENT_LENGTH);
			modifiedDate = new Date(parseLong(sinceDateParam) * SECS_TO_MILLIS);

			recordID = offsetString.substring(HISTORY_ID_SEGMENT_LENGTH);
		}
	}

	public Date getModifiedDate() {

		return modifiedDate;
	}

	public String getRecordID() {

		return recordID;
	}

	@Override
	public String toString() {

		return String.format("%010d%s", modifiedDate.getTime() / 1000, recordID);
	}
}
