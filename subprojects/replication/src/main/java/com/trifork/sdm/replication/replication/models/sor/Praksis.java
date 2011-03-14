package com.trifork.sdm.replication.replication.models.sor;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.sdm.replication.replication.models.Record;

@Entity(name = "sor/praksis/v1")
@Table(name = "Praksis")
public class Praksis extends Record
{
	@Id
	@GeneratedValue
	@Column(name = "praksisPID")
	private BigInteger recordID;

	@Column(name = "SorNummer")
	protected BigInteger sorNummer;
	
	@Column(name = "EanLokationsnummer")
	protected BigInteger eanLokationsnummer;
	
	@Column(name = "RegionCode")
	protected BigInteger regionCode;
	
	@Column(name = "Navn")
	protected String navn;

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
		return sorNummer.toString();
	}


	@Override
	public Date getUpdated()
	{
		return modifiedDate;
	}
}
