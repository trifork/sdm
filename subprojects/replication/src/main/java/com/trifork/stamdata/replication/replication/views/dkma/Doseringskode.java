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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.stamdata.replication.replication.annotations.ViewPath;
import com.trifork.stamdata.replication.replication.views.View;


@Entity
@XmlRootElement
@Table(name = "LaegemiddelDoseringRef")
@ViewPath("dkma/drugsdosagesrelation/v1")
public class Doseringskode extends View {

	@Id
	@GeneratedValue
	@Column(name = "LaegemiddelDoseringRefPID")
	@XmlTransient
	private BigInteger recordID;

	@Column(name = "CID")
	private String id;

	@Column(name = "DrugID")
	protected BigInteger drugId; // TODO: BigInt Why?

	@Column(name = "DoseringKode")
	protected BigInteger dosageCode;

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
