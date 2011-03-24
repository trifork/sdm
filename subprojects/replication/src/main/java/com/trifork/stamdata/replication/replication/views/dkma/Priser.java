package com.trifork.stamdata.replication.replication.views.dkma;

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
@ViewPath("dkma/pris/v1")
public class Priser extends View {

	@Id
	@GeneratedValue
	@Column(name = "PriserPID")
	@XmlTransient
	private BigInteger recordID;

	@Column(name = "Varenummer")
	protected String varenummer;

	@Column(name = "apoteketsIndkoebspris")
	protected BigInteger apoteketsIndkoebspris;

	@Column(name = "Registerpris")
	protected BigInteger registerpris;

	@Column(name = "ekspeditionensSamledePris")
	protected BigInteger ekspeditionensSamledePris;

	@Column(name = "tilskudspris")
	protected BigInteger tilskudspris;

	@Column(name = "LeveranceprisTilHospitaler")
	protected BigInteger leveranceprisTilHospitaler;

	@Column(name = "IkkeTilskudsberettigetDel")
	protected BigInteger ikkeTilskudsberettigetDel;

	// Metadata

	@XmlTransient
	@Column(name = "ModifiedDate")
	private Date modifiedDate;

	@Column(name = "ValidFrom")
	@Temporal(TIMESTAMP)
	protected Date validFrom;

	@Column(name = "ValidTo")
	@Temporal(TIMESTAMP)
	protected Date validTo;

	@Override
	public String getId() {

		return varenummer.toString();
	}

	@Override
	public Date getUpdated() {

		return modifiedDate;
	}

	@Override
	public BigInteger getRecordID() {

		return recordID;
	}
}
