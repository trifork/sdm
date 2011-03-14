package com.trifork.stamdata.replication.replication.views.doseringsforslag;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.stamdata.Documented;
import com.trifork.stamdata.replication.replication.views.View;


@Entity(name = "doseringsforslag/drugdosageunitrelation/v1")
@Documented("Indeholder information om lægemidlers drug-id og doseringsenhed.")
public class DrugDosageUnitRelation extends View {

	@Id
	@GeneratedValue
	@Column(name = "DosageDrugPID")
	protected BigInteger id;

	protected int releaseNumber;

	@Column(length = 11)
	protected long drugId;

	@Column(length = 4)
	protected int dosageUnitCode;

	@XmlTransient
	@Temporal(TemporalType.TIMESTAMP)
	protected Date modifiedDate;

	@Temporal(TemporalType.TIMESTAMP)
	protected Date validFrom;

	@Override
	public String getId() {

		return Long.toString(drugId);
	}

	@Override
	public BigInteger getRecordID() {

		return id;
	}

	@Override
	public Date getUpdated() {

		return modifiedDate;
	}
}
