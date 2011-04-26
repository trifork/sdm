package com.trifork.stamdata.replication.replication.views.usagelog;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.stamdata.ClientSpecific;
import com.trifork.stamdata.UsageLogged;
import com.trifork.stamdata.replication.replication.views.View;
import com.trifork.stamdata.replication.replication.views.ViewPath;

@Entity
@ClientSpecific
@UsageLogged(false)
@XmlRootElement
@ViewPath("usage/v1")
@XmlAccessorType(XmlAccessType.FIELD)
public class UsageLogEntry extends View {
	@Id
	@GeneratedValue
	@XmlTransient
	@Column(name = "UsageLogEntryPID")
	public BigInteger recordID;

	@XmlElement(required = true)
	public String clientId;
	
	@XmlElement(required = true, name = "date")
	@Column(name="date")
	public Date modifiedDate;
	
	@XmlElement(required = true)
	public String type;
	
	@XmlElement(required = true)
	public int amount;
	
	public UsageLogEntry() {
		// JPA
	}
	
	public UsageLogEntry(String clientId, Date date, String type, int amount) {
		this.clientId = clientId;
		this.modifiedDate = date;
		this.type = type;
		this.amount = amount;
	}

	@Override
	public String getId() {
		return recordID.toString();
	}

	@Override
	public BigInteger getRecordID() {
		return recordID;
	}

	@Override
	public Date getUpdated() {
		return modifiedDate;
	}

	@Override
	public String toString() {
		return "UsageLogEntry [clientId=" + clientId + ", modifiedDate="
				+ modifiedDate + ", type=" + type + ", amount=" + amount + "]";
	}
}
