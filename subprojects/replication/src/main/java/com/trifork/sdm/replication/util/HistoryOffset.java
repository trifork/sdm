package com.trifork.sdm.replication.util;

import java.math.BigInteger;
import java.util.Date;

public class HistoryOffset {

	protected static int HISTORY_ID_SEGMENT_LENGTH = 10;
	protected static int SECS_TO_MILLIS = 1000;

	protected BigInteger recordID;
	protected Date modifiedDate;


	public HistoryOffset(BigInteger id, Date modifiedDate) {

		this.recordID = id;
		this.modifiedDate = modifiedDate;
	}


	public HistoryOffset(String id, Date modifiedDate) {
		this(new BigInteger(id), modifiedDate);
	}


	public HistoryOffset(String offsetString) {

		if (offsetString == null) {
			recordID = new BigInteger("0");
			modifiedDate = new Date(0);
		}
		else {
			String sinceDateParam = offsetString.substring(0, offsetString.length() - HISTORY_ID_SEGMENT_LENGTH);
			modifiedDate = new Date(Long.parseLong(sinceDateParam) * SECS_TO_MILLIS);

			String sinceIdParam = offsetString.substring(offsetString.length() - HISTORY_ID_SEGMENT_LENGTH);
			recordID = new BigInteger(sinceIdParam);
		}
	}


	public Date getModifiedDate() {
		return modifiedDate;
	}


	public BigInteger getRecordID() {
		return recordID;
	}


	@Override
	public String toString() {

		return String.format("%010d%010d", modifiedDate.getTime(), recordID);
	}
}
