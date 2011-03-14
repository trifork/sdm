package com.trifork.stamdata.replication.replication.views.dkma;

import java.math.BigInteger;
import java.util.Date;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import com.trifork.stamdata.replication.replication.views.View;


@Entity(name = "dkma/indikation/v1")
@Table(name = "Indikation")
public class Indikation extends View {

	@Id
	@GeneratedValue
	@Column(name = "IndikationPID")
	@XmlTransient
	private BigInteger recordID;

	@Column(name = "IndikationKode")
	protected BigInteger id;

	@Column(name = "IndikationTekst")
	protected String tekst;

	@Column(name = "IndikationstekstLinie1")
	protected String tekstLinje1;

	@Column(name = "IndikationstekstLinie2")
	protected String tekstLinje2;

	@Column(name = "IndikationstekstLinie3")
	protected String tekstLinje3;

	@Column(name = "aktiv")
	protected Boolean aktiv;

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
