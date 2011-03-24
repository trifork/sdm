package com.trifork.stamdata.replication.replication.views.dkma;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.stamdata.replication.replication.views.View;


@Entity(name = "dkma/tidsenhed/v1")
@Table(name = "Tidsenhed")
public class Tidsenhed extends View {

	@Id
	@GeneratedValue
	@Column(name = "TidsenhedPID")
	@XmlTransient
	private BigInteger recordID;

	@Column(name = "TidsenhedKode")
	protected String id;

	@Column(name = "TidsenhedTekst")
	protected String tekst;

	@Column(name = "KortTekst")
	protected String kortTekst;

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

		return id.toString();
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
