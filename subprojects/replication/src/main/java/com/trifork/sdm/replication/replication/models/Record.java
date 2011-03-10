package com.trifork.sdm.replication.replication.models;

import java.math.BigInteger;
import java.util.Date;

public abstract class Record
{
	public abstract String getID();


	public abstract BigInteger getRecordID();


	public abstract Date getUpdated();


	/**
	 * Gets the revision for the record
	 * 
	 * The format is:
	 * 
	 * [-Updated Date-][-----ID-----]
	 * 
	 * Each of the two section is 10 characters long and padded width 0's. The updated date is
	 * represented in seconds since the last epoch.
	 */
	public final String getRevision()
	{
		return String.format("%010d%010d", getUpdated().getTime(), getRecordID());
	}
}
