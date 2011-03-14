package com.trifork.sdm.replication.replication.models.cpr;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.sdm.replication.replication.models.Record;

@Entity(name = "cpr/umyndigoerelsevaergerelation/v1")
@Table(name = "UmyndiggoerelseVaergeRelation")
public class UmyndiggoerelseVaergeRelation extends Record
{
	@Id
	@GeneratedValue
	@Column(name = "UmyndiggoerelseVaergeRelationPID")
	private BigInteger recordID;

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

	@Column(name = "RelationCprStartDato")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date relationCprStartDato;

	@Column(name = "VaergesNavn")
	protected String vaergesNavn;

	@Column(name = "VaergesNavnStartDato")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date vaergesNavnStartDato;

	@Column(name = "relationsTekst1")
	protected String RelationsTekst1;

	@Column(name = "relationsTekst2")
	protected String RelationsTekst2;

	@Column(name = "relationsTekst3")
	protected String RelationsTekst3;

	@Column(name = "relationsTekst4")
	protected String RelationsTekst4;

	@Column(name = "relationsTekst5")
	protected String RelationsTekst5;

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
