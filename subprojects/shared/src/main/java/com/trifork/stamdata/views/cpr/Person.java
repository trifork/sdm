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

package com.trifork.stamdata.views.cpr;

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

import com.trifork.stamdata.views.View;
import com.trifork.stamdata.views.ViewPath;


@Entity
@XmlRootElement
@ViewPath("cpr/person/v1")
public class Person extends View {
	
	@XmlTransient
	public Date createdDate;
	
	@XmlTransient
	public static int i = 100;

	protected Person() {
		
	}
	
	/**
	 * WARNING: For testing purpose only!
	 */
	public Person(String cpr, String koen, String fornavn, String mellemnavn, String efternavn, String coNavn, String lokalitet,String vejnavn,	String bygningsnummer, String husnummer, String etage, 
			String sideDoerNummer, String bynavn, BigInteger postnummer, String postdistrikt, String status, String gaeldendeCPR, 
			Date foedselsdato, String stilling, BigInteger vejKode, BigInteger kommuneKode,
			Date modifiedDate, Date navnebeskyttelseslettedato, Date navnebeskyttelsestartdato, Date validFrom, Date validTo) {

		this.cpr = cpr;
		this.koen = koen;
		this.fornavn = fornavn;
		this.mellemnavn = mellemnavn;
		this.efternavn = efternavn;
		this.coNavn = coNavn;
		this.lokalitet = lokalitet;
		this.vejnavn = vejnavn;
		this.bygningsnummer = bygningsnummer;
		this.husnummer = husnummer;
		this.etage = etage;
		this.sideDoerNummer = sideDoerNummer;
		this.bynavn = bynavn;
		this.postnummer = postnummer;
		this.postdistrikt = postdistrikt;
		this.status = status;
		this.gaeldendeCPR = gaeldendeCPR;
		this.foedselsdato = foedselsdato;
		this.stilling = stilling;
		this.vejKode = vejKode;
		this.kommuneKode = kommuneKode;
		this.modifiedDate = modifiedDate;
		this.navnebeskyttelseslettedato = navnebeskyttelsestartdato;
		this.navnebeskyttelsestartdato = navnebeskyttelsestartdato;
		this.validFrom = validFrom;
		this.validTo = validTo;
		this.createdDate = new Date();
		i += 10000;
		this.validFrom = new Date(new Date().getTime() + i);
	}
	
	@Id
	@GeneratedValue
	@XmlTransient
	@Column(name = "PersonPID")
	private BigInteger recordID;

	@Column(name = "CPR")
	@XmlElement(required = true)
	public String cpr;

	@Column(name = "Koen")
	public String koen;

	@Column(name = "Fornavn")
	public String fornavn;

	@Column(name = "Mellemnavn")
	public String mellemnavn;

	@Column(name = "Efternavn")
	public String efternavn;

	@Column(name = "CoNavn")
	public String coNavn;

	@Column(name = "Lokalitet")
	public String lokalitet;

	@Column(name = "Vejnavn")
	public String vejnavn;

	@Column(name = "Bygningsnummer")
	public String bygningsnummer;

	@Column(name = "Husnummer")
	public String husnummer;

	@Column(name = "Etage")
	public String etage;

	@Column(name = "SideDoerNummer")
	public String sideDoerNummer;

	@Column(name = "Bynavn")
	public String bynavn;

	@Column(name = "Postnummer")
	public BigInteger postnummer;

	@Column(name = "PostDistrikt")
	public String postdistrikt;

	@Column(name = "Status")
	public String status;

	@Column(name = "GaeldendeCPR")
	public String gaeldendeCPR;

	@Column(name = "Foedselsdato")
	@Temporal(TemporalType.TIMESTAMP)
	public Date foedselsdato;

	@Column(name = "Stilling")
	public String stilling;

	@Column(name = "VejKode")
	public BigInteger vejKode;

	@Column(name = "KommuneKode")
	public BigInteger kommuneKode;

	@XmlTransient
	@Column(name = "ModifiedDate")
	@Temporal(TIMESTAMP)
	public Date modifiedDate;

	@Temporal(TIMESTAMP)
	public Date navnebeskyttelseslettedato;
	
	@Temporal(TIMESTAMP)
	public Date navnebeskyttelsestartdato;

	@Column(name = "ValidFrom")
	@Temporal(TIMESTAMP)
	@XmlElement(required = true)
	public Date validFrom;

	@Column(name = "ValidTo")
	@Temporal(TIMESTAMP)
	public Date validTo;

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
		return "Person[" + cpr + ", fornavn=" + fornavn + ", efternavn=" + efternavn + ", køn=" + koen + "], validFrom=" + validFrom + ", validTo=" + validTo;
	}
}
