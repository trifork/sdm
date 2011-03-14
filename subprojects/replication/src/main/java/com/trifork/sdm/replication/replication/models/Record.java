package com.trifork.sdm.replication.replication.models;

import java.math.BigInteger;
import java.util.Date;

import javax.xml.bind.annotation.*;

import com.trifork.sdm.replication.util.HistoryOffset;
import com.trifork.sdm.replication.util.Namespace;

@XmlRootElement(namespace = Namespace.STAMDATA_3_0)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlAccessorOrder(XmlAccessOrder.UNDEFINED)
public abstract class Record {

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
	public String getOffset() {
		return new HistoryOffset(getRecordID(), getUpdated()).toString();
	}
}
