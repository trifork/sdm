package com.trifork.stamdata.replication.replication.views.cpr;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.stamdata.replication.replication.views.View;
import com.trifork.stamdata.replication.replication.views.ViewPath;

@Entity
@Table(name = "AktuelCivilstand")
@XmlRootElement
@ViewPath("cpr/civilstand/v1")
@XmlAccessorType(XmlAccessType.FIELD)
public class Civilstand extends View {

	@Id
	@GeneratedValue
	@XmlTransient
	@Column(name = "CivilstandPID")
	private BigInteger recordID;

	@XmlAttribute(required = true)
	protected String cpr;
	@XmlAttribute(required = true)
	protected String civilstandskode;

	protected String aegtefaellePersonnummer;

	protected Date aegtefaelleFoedselsdato;

	protected String aegtefaellenavn;
	protected Date startdato;
	protected Date separation;

	@XmlTransient
	@Column(name = "ModifiedDate")
	@Temporal(TIMESTAMP)
	protected Date modifiedDate;

	@Column(name = "ValidFrom")
	@Temporal(TIMESTAMP)
	protected Date validFrom;

	@Column(name = "ValidTo")
	@Temporal(TIMESTAMP)
	protected Date validTo;

	@Override
	public String getId() {
		return cpr;
	}

	@Override
	public BigInteger getRecordID() {
		return recordID;
	}

	@Override
	public Date getUpdated() {
		return modifiedDate;
	}

	@Override
	public String toString() {
		return "Civilstand[cpr=" + cpr + ", tilstandskode=" + civilstandskode + ", ægtefællepersonnummer=" + aegtefaellePersonnummer
		+ ", ægtefællefødselsdato=" + aegtefaelleFoedselsdato + ", ægtefællenavn=" + aegtefaellenavn + ", separation=" + separation + "]";
	}
}
