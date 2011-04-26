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
import com.trifork.stamdata.replication.replication.views.ViewPath;

@Entity
@ClientSpecific
@XmlRootElement
@ViewPath("cpr/usage/v1")
@XmlAccessorType(XmlAccessType.FIELD)
public class UsageLogEntry {
	@Id
	@GeneratedValue
	@XmlTransient
	@Column(name = "UsageLogEntryPID")
	public BigInteger recordID;

	@XmlElement(required = true)
	public String clientId;
	
	@XmlElement(required = true)
	public Date date;
	
	@XmlElement(required = true)
	public String type;
	
	@XmlElement(required = true)
	public int amount;
	
	public UsageLogEntry() {
		// JPA
	}
	
	public UsageLogEntry(String clientId, Date date, String type, int amount) {
		this.clientId = clientId;
		this.date = date;
		this.type = type;
		this.amount = amount;
	}
}
