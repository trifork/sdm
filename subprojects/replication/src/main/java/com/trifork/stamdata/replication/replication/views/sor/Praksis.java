package com.trifork.stamdata.replication.replication.views.sor;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.math.BigInteger;
import java.util.Date;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import com.trifork.stamdata.replication.replication.views.View;


@Entity(name = "sor/praksis/v1")
@Table(name = "Praksis")
public class Praksis extends View {

	@Id
	@GeneratedValue
	@Column(name = "praksisPID")
	private BigInteger recordID;

	@Column(name = "SorNummer")
	protected BigInteger sorNummer;

	@Column(name = "EanLokationsnummer")
	protected BigInteger eanLokationsnummer;

	@Column(name = "RegionCode")
	protected BigInteger regionCode;

	@Column(name = "Navn")
	protected String navn;

	@XmlTransient
	@Column(name = "ModifiedDate")
	@Temporal(TIMESTAMP)
	protected Date modifiedDate;

	@XmlTransient
	@Column(name = "ValidFrom")
	@Temporal(TIMESTAMP)
	protected Date validFrom;

	@XmlTransient
	@Column(name = "ValidTo")
	@Temporal(TIMESTAMP)
	protected Date validTo;

	@Override
	public BigInteger getRecordID() {

		return recordID;
	}

	@Override
	public String getId() {

		return sorNummer.toString();
	}

	@Override
	public Date getUpdated() {

		return modifiedDate;
	}
}
