package com.trifork.stamdata.replication.replication.views;

import java.math.BigInteger;
import java.util.Date;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import com.trifork.stamdata.replication.replication.HistoryOffset;
import com.trifork.stamdata.replication.util.Namespace;


@XmlRootElement(namespace = Namespace.STAMDATA_3_0)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlAccessorOrder(XmlAccessOrder.UNDEFINED)
@MappedSuperclass
public abstract class View {

	public abstract String getId();

	public abstract BigInteger getRecordID();

	public abstract Date getUpdated();

	/**
	 * Gets the offset (revision) for the record.
	 * 
	 * @see HistoryOffset
	 */
	public String getOffset() {

		return new HistoryOffset(getRecordID().toString(), getUpdated()).toString();
	}
}
