package com.trifork.sdm.replication.replication.models.dkma;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.sdm.replication.replication.models.Record;

@Entity(name = "dkma/klausulering/v1")
@Table(name = "Klausulering")
public class Klausulering extends Record
{	
	@Id
	@GeneratedValue
	@Column(name = "KlausuleringPID")
	@XmlTransient
	private BigInteger recordID;

	@Column(name = "Kode")
	protected String id;

	@Column(name = "KortTekst")
	protected String kortTekst;

	@Column(name = "Tekst")
	protected String tekst;

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
		return id.toString();
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
