// Stamdata - Copyright (C) 2011 National Board of e-Health (NSI)
//
// All source code and information supplied as part of Stamdata is
// copyright to National Board of e-Health.
//
// The source code has been released under a dual license - meaning you can
// use either licensed version of the library with your code.
//
// It is released under the Common Public License 1.0, a copy of which can
// be found at the link below.
// http://www.opensource.org/licenses/cpl1.0.php
//
// It is released under the LGPL (GNU Lesser General Public License), either
// version 2.1 of the License, or (at your option) any later version. A copy
// of which can be found at the link below.
// http://www.gnu.org/copyleft/lesser.html

package com.trifork.stamdata.replication.replication.views.cpr;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.stamdata.replication.replication.views.View;
import com.trifork.stamdata.replication.replication.views.ViewPath;


@Entity
@XmlRootElement
@ViewPath("cpr/person/v1")
public class Person extends View {

	@Id
	@GeneratedValue
	@XmlTransient
	@Column(name = "PersonPID")
	private BigInteger recordID;

	@Column(name = "CPR")
	@XmlElement(required = true)
	protected String cpr;

	@Column(name = "Koen")
	protected String koen;

	@Column(name = "Fornavn")
	protected String fornavn;

	@Column(name = "Mellemnavn")
	protected String mellemnavn;

	@Column(name = "Efternavn")
	protected String efternavn;

	@Column(name = "CoNavn")
	protected String coNavn;

	@Column(name = "Lokalitet")
	protected String lokalitet;

	@Column(name = "Vejnavn")
	protected String vejnavn;

	@Column(name = "Bygningsnummer")
	protected String bygningsnummer;

	@Column(name = "Husnummer")
	protected String husnummer;

	@Column(name = "Etage")
	protected String etage;

	@Column(name = "SideDoerNummer")
	protected String sideDoerNummer;

	@Column(name = "Bynavn")
	protected String bynavn;

	@Column(name = "Postnummer")
	protected BigInteger postnummer;

	@Column(name = "PostDistrikt")
	protected String postdistrikt;

	@Column(name = "Status")
	protected String status;

	@Column(name = "GaeldendeCPR")
	protected String gaeldendeCPR;

	@Column(name = "Foedselsdato")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date foedselsdato;

	@Column(name = "Stilling")
	protected String stilling;

	@Column(name = "VejKode")
	protected BigInteger vejKode;

	@Column(name = "KommuneKode")
	protected BigInteger kommuneKode;

	@XmlTransient
	@Column(name = "ModifiedDate")
	@Temporal(TIMESTAMP)
	protected Date modifiedDate;

	protected Date navneBeskyttelsestartdato;
	protected Date navnebeskyttelseslettedato;

	@Column(name = "ValidFrom")
	@Temporal(TIMESTAMP)
	@XmlElement(required = true)
	protected Date validFrom;

	@Column(name = "ValidTo")
	@Temporal(TIMESTAMP)
	protected Date validTo;

	@Override
	public BigInteger getRecordID() {
		return recordID;
	}

	@Override
	public String getId() {
		return cpr;
	}

	@Override
	public Date getUpdated() {
		return modifiedDate;
	}

	@Override
	public String toString() {
		return "Person[" + cpr + ", fornavn=" + fornavn + ", efternavn=" + efternavn + ", køn=" + koen + "]";
	}
}
