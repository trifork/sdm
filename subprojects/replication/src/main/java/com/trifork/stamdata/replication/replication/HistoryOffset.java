// Stamdata - Copyright (C) 2011 National Board of e-Health (NSI)
// 
// All source code and information supplied as part of Stamdata is
// copyright to National Board of e-Health.
// 
// The source code has been released under a dual license - meaning you can
// use either licensed version of the library with your code.
// 
// It is released under the Common Public License 1.0, a copy of which can
// be found at the link below.
// http://www.opensource.org/licenses/cpl1.0.php
// 
// It is released under the LGPL (GNU Lesser General Public License), either
// version 2.1 of the License, or (at your option) any later version. A copy
// of which can be found at the link below.
// http://www.gnu.org/copyleft/lesser.html

package com.trifork.stamdata.replication.replication;

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
