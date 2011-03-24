package com.trifork.stamdata.replication.replication.views.dkma;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.stamdata.replication.replication.views.View;


@Entity(name = "dkma/tilskudsinterval/v1")
@Table(name = "Tilskudsintervaller")
public class Tilskudsinterval extends View {

	@Id
	@GeneratedValue
	@Column(name = "TilskudsintervallerPID")
	@XmlTransient
	private BigInteger recordID;

	@Column(name = "CID")
	protected BigInteger id;

	@Column(name = "Type")
	protected BigInteger type;

	@Column(name = "Niveau")
	protected BigInteger niveau;

	@Column(name = "NedreGraense")
	protected BigInteger nedreGraense;

	@Column(name = "OevreGraense")
	protected BigInteger OevreGraense;

	@Column(name = "Procent")
	protected Double procent;

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
