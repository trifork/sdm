package com.trifork.sdm.replication.replication.models.yderregisteret;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

import com.trifork.sdm.replication.replication.models.Record;
import com.trifork.sdm.replication.util.Namespace;

@Entity(name = "yderegisteret/person/v1")
@Table(name = "Apotek")
@XmlRootElement(namespace = Namespace.STAMDATA_3_0)
@XmlAccessorType(XmlAccessType.FIELD)
public class Person extends Record
{
	@Id
	@GeneratedValue
	@Column(name = "YderregisterPersonPID")
	private BigInteger recordID;

	@Column(name = "Nummer")
	protected String nummer;

	@Column(name = "Id")
	protected String id;

	@Column(name = "CPR")
	protected String cpr;

	@Column(name = "personrolleKode")
	protected BigInteger personrolleKode;

	@Column(name = "personrolleTxt")
	protected String personrolleTekst;

	@Column(name = "HistIDPerson")
	protected String histId;

	@XmlTransient
	@Column(name = "ModifiedDate")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date modifiedDate;

	@XmlTransient
	@Column(name = "ValidFrom")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date validFrom;

	@XmlTransient
	@Column(name = "ValidTo")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date validTo;
	public BigInteger getRecordID()
	{
		return recordID;
	}


	@Override
	public String getID()
	{
		return id.toString();
	}


	@Override
	public Date getUpdated()
	{
		return modifiedDate;
	}
}
