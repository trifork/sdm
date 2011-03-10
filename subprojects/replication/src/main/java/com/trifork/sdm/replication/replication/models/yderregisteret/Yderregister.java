package com.trifork.sdm.replication.replication.models.yderregisteret;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

import com.trifork.sdm.replication.replication.models.Record;
import com.trifork.sdm.replication.util.Namespace;

@Entity(name = "yderegisteret/yderregister/v1")
@Table(name = "Apotek")
@XmlRootElement(namespace = Namespace.STAMDATA_3_0)
@XmlAccessorType(XmlAccessType.FIELD)
public class Yderregister extends Record
{
	@Id
	@GeneratedValue
	@Column(name = "YderregisterPID")
	private BigInteger recordID;

	@Column(name = "Nummer")
	protected BigInteger nummer;

	@Column(name = "Telefon")
	protected String telefon;

	@Column(name = "Navn")
	protected String navn;

	@Column(name = "Vejnavn")
	protected String vejnavn;

	@Column(name = "Postnummer")
	protected String postnummer;

	@Column(name = "Bynavn")
	protected String bynavn;

	@Column(name = "AmtNummer")
	protected BigInteger amtNummer;

	@Column(name = "HistID")
	protected String histID;

	@Column(name = "Email")
	protected String email;

	@Column(name = "Www")
	protected String www;

	@Column(name = "HovedSpecialeKode")
	protected String hovedSpecialeKode;

	@Column(name = "HovedSpecialeTekst")
	protected String hovedSpecialeTekst;

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
	public BigInteger getRecordID()
	{
		return recordID;
	}


	@Override
	public String getID()
	{
		return nummer.toString();
	}


	@Override
	public Date getUpdated()
	{
		return modifiedDate;
	}
}
