package com.trifork.stamdata.replication.replication.views.dkma;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.math.BigInteger;
import java.util.Date;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import com.trifork.stamdata.replication.replication.views.View;


@Entity(name = "dkma/beregningsregler/v1")
public class Beregningsregler extends View {

	@Id
	@GeneratedValue
	@Column(name = "BeregningsreglerPID")
	@XmlTransient
	private BigInteger recordID;

	@Column(name = "Kode")
	protected String kode;

	@Column(name = "Tekst")
	protected String tekst;

	@XmlTransient
	@Column(name = "ModifiedDate")
	private Date modifiedDate;

	@XmlTransient
	@Column(name = "ValidFrom")
	@Temporal(TIMESTAMP)
	protected Date validFrom;

	@XmlTransient
	@Column(name = "ValidTo")
	@Temporal(TIMESTAMP)
	protected Date validTo;

	@Override
	public String getId() {

		return kode;
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
