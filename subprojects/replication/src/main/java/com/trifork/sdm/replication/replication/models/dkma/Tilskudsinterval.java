package com.trifork.sdm.replication.replication.models.dkma;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

import com.trifork.sdm.replication.replication.models.Record;
import com.trifork.sdm.replication.util.Namespace;

@Entity(name = "dkma/tilskudsinterval/v1")
@Table(name = "Tilskudsintervaller")
@XmlType(namespace = Namespace.STAMDATA_3_0)
@XmlAccessorType(XmlAccessType.FIELD)
public class Tilskudsinterval extends Record
{
	@Id
	@GeneratedValue
	@Column(name = "TilskudsintervallerPID")
	@XmlTransient
	private BigInteger recordID;

	@Column(name = "CID")
	protected BigInteger id;

	@Column(name = "Type")
	protected BigInteger type;

	@Column(name = "Niveau")
	protected BigInteger niveau;

	@Column(name = "NedreGraense")
	protected BigInteger nedreGraense;

	@Column(name = "OevreGraense")
	protected BigInteger OevreGraense;

	@Column(name = "Procent")
	protected Double procent;

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
