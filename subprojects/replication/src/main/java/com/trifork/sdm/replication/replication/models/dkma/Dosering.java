package com.trifork.sdm.replication.replication.models.dkma;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.sdm.replication.replication.models.Record;

@Entity(name = "dkma/dosering/v1")
@Table(name = "Dosering")
public class Dosering extends Record
{
	@Id
	@GeneratedValue
	@XmlTransient
	@Column(name = "DoseringPID")
	protected BigInteger recordID;

	@Column(name = "DoseringKode")
	protected BigInteger code; // TODO: BigInt Why?

	@Column(name = "DoseringsTekst")
	protected String text;

	@Column(name = "DoseringKortTekst")
	protected String shortText;

	@Column(name = "DoseringstekstLinie1")
	protected String descriptionLine1;

	@Column(name = "DoseringstekstLinie2")
	protected String descriptionLinie2;

	@Column(name = "DoseringstekstLinie3")
	protected String descriptionLinie3;

	@Column(name = "AntalEnhederPrDoegn")
	protected Float numberOfUnitsPerDay;

	@Column(name = "Aktiv")
	protected Boolean active;

	@XmlTransient
	@Column(name = "ModifiedDate")
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
		return code.toString();
	}


	@Override
	public Date getUpdated()
	{
		return modifiedDate;
	}
}
