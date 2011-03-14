package com.trifork.stamdata.replication.replication.views.yderregisteret;

import java.math.BigInteger;
import java.util.Date;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import com.trifork.stamdata.replication.replication.views.View;
import com.trifork.stamdata.replication.util.Namespace;


@Entity(name = "yderegisteret/yder/v1")
@Table(name = "YderregisterPerson")
@XmlType(name = "yder", namespace = Namespace.STAMDATA_3_0)
public class Person extends View {

	@Id
	@GeneratedValue
	@Column(name = "YderregisterPersonPID")
	private BigInteger recordID;

	@Column(name = "Nummer")
	protected String nummer;

	@Column(name = "Id")
	protected String id;

	@Column(name = "CPR")
	protected String cpr;

	@Column(name = "personrolleKode")
	protected BigInteger personrolleKode;

	@Column(name = "personrolleTxt")
	protected String personrolleTekst;

	@Column(name = "HistIDPerson")
	protected String histId;

	@XmlTransient
	@Column(name = "ModifiedDate")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date modifiedDate;

	@XmlTransient
	@Column(name = "ValidFrom")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date validFrom;

	@XmlTransient
	@Column(name = "ValidTo")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date validTo;

	@Override
	public BigInteger getRecordID() {

		return recordID;
	}

	@Override
	public String getId() {

		return id.toString();
	}

	@Override
	public Date getUpdated() {

		return modifiedDate;
	}
}
