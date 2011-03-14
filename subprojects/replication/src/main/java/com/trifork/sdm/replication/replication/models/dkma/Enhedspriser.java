package com.trifork.sdm.replication.replication.models.dkma;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.sdm.replication.replication.models.Record;

@Entity(name = "dkma/enhedspriser/v1")
@Table(name = "Enhedspriser")
public class Enhedspriser extends Record
{
	@Id
	@GeneratedValue
	@Column(name = "EnhedspriserPID")
	@XmlTransient
	private BigInteger recordID;

	@Column(name = "Kode")
	private String id;

	@Column(name = "Tekst")
	protected String text;

	@Column(name = "KortTekst")
	protected String shortText;
	
	// Metadata

	@XmlTransient
	@Column(name = "ModifiedDate")
	private Date modifiedDate;

	@Column(name = "ValidFrom")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date validFrom;

	@Column(name = "ValidTo")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date validTo;


	@Override
	public String getID()
	{
		return id;
	}


	@Override
	public Date getUpdated()
	{
		return modifiedDate;
	}


	@Override
	public BigInteger getRecordID()
	{
		return recordID;
	}
}