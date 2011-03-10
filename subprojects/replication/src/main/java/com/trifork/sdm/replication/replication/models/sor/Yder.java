package com.trifork.sdm.replication.replication.models.sor;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

import com.trifork.sdm.replication.replication.models.Record;
import com.trifork.sdm.replication.util.Namespace;

@Entity(name = "sor/yder/v1")
@Table(name = "Yder")
@XmlRootElement(namespace = Namespace.STAMDATA_3_0)
@XmlAccessorType(XmlAccessType.FIELD)
public class Yder extends Record
{
	@Id
	@GeneratedValue
	@Column(name = "YderPID")
	private BigInteger recordID;

	@Column(name = "SorNummer")
	protected BigInteger sorNummer;
	
	@Column(name = "PraksisSorNummer")
	protected BigInteger praksisSorNummer;

	@Column(name = "EanLokationsnummer")
	protected BigInteger eanLokationsnummer;

	@Column(name = "Nummer")
	protected BigInteger nummer;

	@Column(name = "Navn")
	protected String navn;

	@Column(name = "Telefon")
	protected String telefon;

	@Column(name = "Vejnavn")
	protected String vejnavn;

	@Column(name = "Postnummer")
	protected String postnummer;

	@Column(name = "Bynavn")
	protected String bynavn;

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
		return sorNummer.toString();
	}


	@Override
	public Date getUpdated()
	{
		return modifiedDate;
	}
}
