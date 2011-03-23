package com.trifork.stamdata.replication.replication.views.autorisationsregisteret;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.math.BigInteger;
import java.util.Date;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import com.trifork.stamdata.replication.replication.views.View;


@Entity(name = "autorisationsregisteret/autorisation/v1")
@Table(name = "Autorisation")
public class Autorisation extends View {

	@Id
	@GeneratedValue
	@XmlTransient
	@Column(name = "AutorisationPID")
	private BigInteger recordID;

	@Column(name = "Autorisationsnummer")
	protected String id;

	protected String cpr;

	@Column(name = "Fornavn")
	protected String fornavn;

	@Column(name = "Efternavn")
	protected String efternavn;

	@Column(name = "UddannelsesKode")
	protected String uddannelsesKode;

	@Column(name = "ValidFrom")
	protected Date validFrom;

	@Column(name = "ValidTo")
	protected Date validTo;

	@XmlTransient
	@Temporal(TIMESTAMP)
	@Column(name = "ModifiedDate")
	private Date modifiedDate;

	@Override
	public BigInteger getRecordID() {

		return recordID;
	}

	@Override
	public String getId() {

		return id;
	}

	@Override
	public Date getUpdated() {

		return modifiedDate;
	}
}
