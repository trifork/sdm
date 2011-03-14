package com.trifork.sdm.replication.replication.models.dkma;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.sdm.replication.replication.models.Record;

@Entity(name = "dkma/firma/v1")
@Table(name = "Firma")
public class Firma extends Record
{	
	@Id
	@GeneratedValue
	@Column(name = "FirmaPID")
	@XmlTransient
	private BigInteger recordID;

	@Column(name = "Firmanummer")
	private String firmanummer;

	@Column(name = "FirmamaerkeKort")
	protected String firmamaerkeKortNavn;
	
	@Column(name = "FirmamaerkeLangtNavn")
	protected String firmamaerkeLangtNavn;

	@Column(name = "ParallelimportoerKode")
	protected String parallelimportoerId;
	
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
		return firmanummer;
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