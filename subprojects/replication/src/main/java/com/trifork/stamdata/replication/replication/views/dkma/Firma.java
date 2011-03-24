package com.trifork.stamdata.replication.replication.views.dkma;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.stamdata.replication.replication.annotations.ViewPath;
import com.trifork.stamdata.replication.replication.views.View;


@Entity
@XmlRootElement
@ViewPath("dkma/firma/v1")
public class Firma extends View {

	@Id
	@GeneratedValue
	@Column(name = "FirmaPID")
	@XmlTransient
	private BigInteger recordID;

	@Column(name = "Firmanummer")
	private String firmanummer;

	@Column(name = "FirmamaerkeKort")
	protected String firmamaerkeKortNavn;

	@Column(name = "FirmamaerkeLangtNavn")
	protected String firmamaerkeLangtNavn;

	@Column(name = "ParallelimportoerKode")
	protected String parallelimportoerKode;

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
	public String getId() {

		return firmanummer;
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
