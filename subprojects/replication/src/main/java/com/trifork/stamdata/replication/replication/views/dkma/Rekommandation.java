package com.trifork.stamdata.replication.replication.views.dkma;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.stamdata.replication.replication.annotations.ViewPath;
import com.trifork.stamdata.replication.replication.views.View;


@Entity
@XmlRootElement
@Table(name = "Rekommandationer")
@ViewPath("dkma/rekommandation/v1")
public class Rekommandation extends View {

	@Id
	@GeneratedValue
	@Column(name = "RekommandationerPID")
	@XmlTransient
	private BigInteger recordID;

	@Column(name = "Varenummer")
	protected BigInteger varenummer;

	@Column(name = "Rekommandationsgruppe")
	protected BigInteger gruppe;

	@Column(name = "DrugID")
	protected BigInteger drugId;

	@Column(name = "Rekommandationsniveau")
	protected String niveau;

	// Metadata

	@XmlTransient
	@Column(name = "ModifiedDate")
	private Date modifiedDate;

	@Column(name = "ValidFrom")
	@Temporal(TIMESTAMP)
	protected Date validFrom;

	@Column(name = "ValidTo")
	@Temporal(TIMESTAMP)
	protected Date validTo;

	@Override
	public String getId() {

		return varenummer.toString();
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
