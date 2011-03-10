package com.trifork.sdm.replication.replication.models.cpr;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

import com.trifork.sdm.replication.replication.models.Record;
import com.trifork.sdm.replication.util.Namespace;

@Entity(name = "cpr/foraeldremyndighedrelation/v1")
@Table(name = "ForaeldreMyndighedRelation")
@XmlRootElement(namespace = Namespace.STAMDATA_3_0)
@XmlAccessorType(XmlAccessType.FIELD)
public class ForaeldremyndighedsRelation extends Record
{
	@Id
	@GeneratedValue
	@Column(name = "ForaeldreMyndighedRelationPID")
	protected BigInteger recordID;

	@Column(name = "Id")
	protected String id;

	@Column(name = "CPR")
	protected String cpr;

	@Column(name = "TypeKode")
	protected String typeKode;

	@Column(name = "TypeTekst")
	protected String typeTekst;

	@Column(name = "RelationCpr")
	protected String relationCpr;

	@XmlTransient
	@Column(name = "ModifiedDate")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date modifiedDate;

	@Column(name = "ValidFrom")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date validFrom;

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
