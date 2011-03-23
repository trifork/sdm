package com.trifork.stamdata.replication.replication.views.dkma;

import java.math.BigInteger;
import java.util.Date;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import com.trifork.stamdata.replication.replication.views.View;


@Entity(name = "dkma/dosering/v1")
public class Dosering extends View {

	@Id
	@GeneratedValue
	@XmlTransient
	@Column(name = "DoseringPID")
	protected BigInteger recordID;

	@Column(name = "DoseringKode")
	protected BigInteger kode;

	@Column(name = "DoseringsTekst")
	protected String tekst;

	@Column(name = "DoseringKortTekst")
	protected String kortTekst;

	@Column(name = "DoseringstekstLinie1")
	protected String beskrivelseLinje1;

	@Column(name = "DoseringstekstLinie2")
	protected String beskrivelseLinje2;

	@Column(name = "DoseringstekstLinie3")
	protected String beskrivelseLinje3;

	@Column(name = "AntalEnhederPrDoegn")
	protected Float antalEnhederPrDoegn;

	@Column(name = "Aktiv")
	protected Boolean aktiv;

	@XmlTransient
	@Column(name = "ModifiedDate")
	protected Date modifiedDate;

	@Column(name = "ValidFrom")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date validFrom;

	@Column(name = "ValidTo")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date validTo;

	@Override
	public BigInteger getRecordID() {

		return recordID;
	}

	@Override
	public String getId() {

		return kode.toString();
	}

	@Override
	public Date getUpdated() {

		return modifiedDate;
	}
}
