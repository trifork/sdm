package com.trifork.sdm.replication.replication.models.dkma;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

import com.trifork.sdm.replication.replication.models.Record;
import com.trifork.sdm.replication.util.Namespace;

@Entity(name = "dkma/indikation/v1")
@Table(name = "Indikation")
@XmlRootElement(namespace = Namespace.STAMDATA_3_0)
@XmlAccessorType(XmlAccessType.FIELD)
public class Indikation extends Record
{
	@Id
	@GeneratedValue
	@Column(name = "IndikationPID")
	@XmlTransient
	private BigInteger recordID;

	@Column(name = "IndikationKode")
	protected BigInteger id;

	@Column(name = "IndikationTekst")
	protected String tekst;

	@Column(name = "IndikationstekstLinie1")
	protected String tekstLinje1;

	@Column(name = "IndikationstekstLinie2")
	protected String tekstLinje2;

	@Column(name = "IndikationstekstLinie3")
	protected String tekstLinje3;

	@Column(name = "aktiv")
	protected Boolean aktiv;

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
