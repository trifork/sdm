package com.trifork.stamdata.replication.replication.views.dkma;

import java.math.BigInteger;
import java.util.Date;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import com.trifork.stamdata.replication.replication.views.View;


@Entity(name = "dkma/atc/v1")
public class ATC extends View {

	@Id
	@GeneratedValue
	@Column(name = "ATCPID")
	@XmlTransient
	private BigInteger recordID;

	@Column(name = "ATC")
	protected String kode;

	@Column(name = "ATCTekst")
	protected String tekst;

	@Column(name = "ATCNiveau1")
	protected String niveau1;

	@Column(name = "ATCNiveau2")
	protected String niveau2;

	@Column(name = "ATCNiveau3")
	protected String niveau3;

	@Column(name = "ATCNiveau4")
	protected String niveau4;

	@Column(name = "ATCNiveau5")
	protected String niveau5;

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
