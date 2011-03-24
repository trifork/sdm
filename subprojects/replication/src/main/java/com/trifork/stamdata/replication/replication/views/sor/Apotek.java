package com.trifork.stamdata.replication.replication.views.sor;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.stamdata.replication.replication.annotations.ViewPath;
import com.trifork.stamdata.replication.replication.views.View;


@Entity
@XmlRootElement
@ViewPath("sor/apotek/v1")
public class Apotek extends View {

	@Id
	@GeneratedValue
	@XmlTransient
	@Column(name = "ApotekPID")
	private BigInteger recordID;

	@Column(name = "SorNummer")
	protected BigInteger sorNummer;

	@Column(name = "ApotekNummer")
	protected BigInteger apotekNummer;

	@Column(name = "FilialNummer")
	protected BigInteger filialNummer;

	@Column(name = "EanLokationsnummer")
	protected BigInteger eanLokationsnummer;

	@Column(name = "cvr")
	protected BigInteger cvr;

	@Column(name = "pcvr")
	protected BigInteger pcvr;

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

	@XmlTransient
	@Column(name = "ModifiedDate")
	@Temporal(TIMESTAMP)
	protected Date modifiedDate;

	@XmlTransient
	@Column(name = "ValidFrom")
	@Temporal(TIMESTAMP)
	protected Date validFrom;

	@XmlTransient
	@Column(name = "ValidTo")
	@Temporal(TIMESTAMP)
	protected Date validTo;

	@Override
	public BigInteger getRecordID() {

		return recordID;
	}

	@Override
	public String getId() {

		return sorNummer.toString();
	}

	@Override
	public Date getUpdated() {

		return modifiedDate;
	}
}
