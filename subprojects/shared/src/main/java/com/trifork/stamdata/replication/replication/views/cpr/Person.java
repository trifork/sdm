// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
// 
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
// 
// Contributor(s): Contributors are attributed in the source code
// where applicable.
// 
// The Original Code is "Stamdata".
// 
// The Initial Developer of the Original Code is Trifork Public A/S.
// 
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
// 
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

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
