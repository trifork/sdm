package com.trifork.sdm.replication.replication.models.dkma;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.sdm.replication.replication.models.Record;

@Entity(name = "dkma/atc/v1")
@Table(name = "ATC")
public class ATCKoderOgTekst extends Record
{
	@Id
	@GeneratedValue
	@Column(name = "ATCPID")
	@XmlTransient
	private BigInteger recordID;

	@Column(name = "ATC")
	protected String code;

	@Column(name = "ATCTekst")
	protected String text;

	@Column(name = "ATCNiveau1")
	protected String niveau1;

	@Column(name = "ATCNiveau2")
	protected String niveau2;

	@Column(name = "ATCNiveau3")
	protected String niveau3;

	@Column(name = "ATCNiveau4")
	protected String niveau4;

	@Column(name = "ATCNiveau5")
	protected String niveau5;

	@XmlTransient
	@Column(name = "ModifiedDate")
	private Date modifiedDate;

	@XmlTransient
	@Column(name = "ValidFrom")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date validFrom;

	@XmlTransient
	@Column(name = "ValidTo")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date validTo;


	@Override
	public String getID()
	{
		return code;
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
