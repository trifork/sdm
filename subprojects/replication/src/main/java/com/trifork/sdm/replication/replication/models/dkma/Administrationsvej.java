package com.trifork.sdm.replication.replication.models.dkma;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

import com.trifork.sdm.replication.replication.models.Record;
import com.trifork.sdm.replication.util.Namespace;

@Entity(name = "dkma/administrationsvej/v1")
@Table(name = "Administrationsvej")
@XmlType(namespace = Namespace.STAMDATA_3_0)
@XmlAccessorType(XmlAccessType.FIELD)
public class Administrationsvej extends Record
{
	@Id
	@GeneratedValue
	@Column(name = "AdministrationsvejPID")
	@XmlTransient
	private BigInteger recordID;

	@Column(name = "AdministrationsvejKode")
	private String id;

	@Column(name = "AdministrationsvejTekst")
	protected String tekst;

	@Column(name = "AdministrationsvejKortTekst")
	protected String kortTekst;

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
