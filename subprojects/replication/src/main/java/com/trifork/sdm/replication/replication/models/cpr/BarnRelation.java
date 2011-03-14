package com.trifork.sdm.replication.replication.models.cpr;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.sdm.replication.replication.models.Record;

@Entity(name = "cpr/barnrelation/v1")
@Table(name = "BarnRelation")
public class BarnRelation extends Record
{
	@Id
	@GeneratedValue
	@Column(name = "BarnRelationPID")
	private BigInteger recordID;

	@Column(name = "Id")
	protected String id;

	@Column(name = "CPR")
	protected String cpr;

	@Column(name = "BarnCPR")
	protected String barnCPR;

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


	@Override
	public BigInteger getRecordID()
	{
		return recordID;
	}


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
}
