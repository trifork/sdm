package com.trifork.stamdata.replication.replication.views.dkma;

import java.math.BigInteger;
import java.util.Date;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import com.trifork.stamdata.replication.replication.views.View;


@Entity(name = "dkma/emballagetypekoder/v1")
public class EmballagetypeKoder extends View {

	@Id
	@GeneratedValue
	@Column(name = "EmballagetypeKoderPID")
	@XmlTransient
	private BigInteger recordID;

	@Column(name = "Kode")
	private String id;

	@Column(name = "Tekst")
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

		return id;
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
