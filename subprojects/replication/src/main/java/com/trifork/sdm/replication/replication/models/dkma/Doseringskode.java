package com.trifork.sdm.replication.replication.models.dkma;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

import com.trifork.sdm.replication.replication.models.Record;
import com.trifork.sdm.replication.util.Namespace;

@Entity(name = "dkma/drugsdosagesrelation/v1")
@Table(name = "LaegemiddelDoseringRef")
@XmlRootElement(namespace = Namespace.STAMDATA_3_0)
@XmlAccessorType(XmlAccessType.FIELD)
public class Doseringskode extends Record
{
	@Id
	@GeneratedValue
	@Column(name = "LaegemiddelDoseringRefPID")
	@XmlTransient
	private BigInteger recordID;

	@Column(name = "CID")
	private String id;

	@Column(name = "DrugID")
	protected BigInteger drugId; // TODO: BigInt Why?

	@Column(name = "DoseringKode")
	protected BigInteger dosageCode;

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
