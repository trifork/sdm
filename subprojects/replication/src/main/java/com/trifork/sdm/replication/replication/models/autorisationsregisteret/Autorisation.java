package com.trifork.sdm.replication.replication.models.autorisationsregisteret;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.sdm.replication.replication.models.Record;

@Entity(name = "autorisationsregisteret/autorisation/v1")
@Table(name = "Autorisation")
public class Autorisation extends Record {

	@Id
	@GeneratedValue
	@XmlTransient
	@Column(name = "AutorisationPID")
	private BigInteger recordID;

	@Column(name = "Autorisationsnummer")
	protected String id;

	@Column(name = "cpr")
	protected String cpr;

	@Column(name = "fornavn")
	protected String fornavn;

	@Column(name = "efternavn")
	protected String efternavn;

	@Column(name = "UddannelsesKode")
	protected String uddannelsesKode;

	@Column(name = "ValidFrom")
	protected Date validFrom;

	@Column(name = "ValidTo")
	protected Date validTo;

	@XmlTransient
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ModifiedDate")
	private Date modifiedDate;


	@Override
	public BigInteger getRecordID() {
		return recordID;
	}


	@Override
	public String getID() {
		return id;
	}


	@Override
	public Date getUpdated() {
		return modifiedDate;
	}
}
