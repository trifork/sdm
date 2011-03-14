package com.trifork.sdm.replication.replication.models.dkma;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.sdm.replication.replication.models.Record;

@Entity(name = "dkma/beregningsregler/v1")
@Table(name = "Beregningsregler")
public class Beregningsregler extends Record {

	@Id
	@GeneratedValue
	@Column(name = "BeregningsreglerPID")
	@XmlTransient
	private BigInteger recordID;

	@Column(name = "Kode")
	protected String code;

	@Column(name = "Tekst")
	protected String text;

	@XmlTransient
	@Column(name = "ModifiedDate")
	private Date modifiedDate;

	@XmlTransient
	@Column(name = "ValidFrom")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date validFrom;

	@XmlTransient
	@Column(name = "ValidTo")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date validTo;


	@Override
	public String getID() {
		return code;
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
