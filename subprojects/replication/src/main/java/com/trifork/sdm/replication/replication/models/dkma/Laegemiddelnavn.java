package com.trifork.sdm.replication.replication.models.dkma;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

import com.trifork.sdm.replication.replication.models.Record;
import com.trifork.sdm.replication.util.Namespace;

@Entity(name = "dkma/laegemiddelnavn/v1")
@Table(name = "Laegemiddelnavn")
@XmlRootElement(namespace = Namespace.STAMDATA_3_0)
@XmlAccessorType(XmlAccessType.FIELD)
public class Laegemiddelnavn extends Record
{
	@Id
	@GeneratedValue
	@Column(name = "LaegemiddelnavnPID")
	@XmlTransient
	private BigInteger recordID;

	@Column(name = "DrugID")
	protected BigInteger id;

	@Column(name = "AdministrationsvejKode")
	protected String administrationsvejId;

	@Column(name = "LaegemidletsUforkortedeNavn")
	protected String navn;

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
