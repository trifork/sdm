package com.trifork.stamdata.replication.replication.views.cpr;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.stamdata.replication.replication.annotations.ViewPath;
import com.trifork.stamdata.replication.replication.views.View;

@Entity
@XmlRootElement
@ViewPath("cpr/folkekirkeoplysninger/v1")
@XmlAccessorType(XmlAccessType.FIELD)
public class Folkekirkeoplysninger extends View {

	@Id
	@GeneratedValue
	@XmlTransient
	@Column(name = "FolkekirkeoplysningerPID")
	private BigInteger recordID;

	protected String cpr;
	
	protected String forholdsKode;
	@Temporal(TIMESTAMP)
	protected Date startdato;


	@XmlTransient
	@Temporal(TIMESTAMP)
	protected Date modifiedDate;

	@Temporal(TIMESTAMP)
	protected Date validFrom;

	@Temporal(TIMESTAMP)
	protected Date validTo;

	@Override
	public String getId() {
		return cpr;
	}

	@Override
	public BigInteger getRecordID() {
		return recordID;
	}

	@Override
	public Date getUpdated() {
		return modifiedDate;
	}

}
