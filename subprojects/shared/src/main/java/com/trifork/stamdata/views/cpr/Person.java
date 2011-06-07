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

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import com.trifork.stamdata.views.ViewPath;


@Entity
@XmlRootElement
@ViewPath("cpr/person/v1")
@AttributeOverride(name = "recordID",column = @Column(name = "PersonPID"))
public class Person extends CprView {

	@Column(name = "Koen")
	public String koen;

	@Column(name = "Fornavn")
	public String fornavn;

	@Column(name = "Mellemnavn")
	public String mellemnavn;

	@Column(name = "Efternavn")
	public String efternavn;
	
	@Column
	public String adresseringsNavn;

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

	public Date navneBeskyttelsestartdato;
	public Date navnebeskyttelseslettedato;

	@Override
	public String getId() {
		return cpr;
	}

	@Override
	public String toString() {
		return "Person[" + cpr + ", fornavn=" + fornavn + ", efternavn=" + efternavn + ", køn=" + koen + "], validFrom=" + validFrom + ", validTo=" + validTo;
	}
}
