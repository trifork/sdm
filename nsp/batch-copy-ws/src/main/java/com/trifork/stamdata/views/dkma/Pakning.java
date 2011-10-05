/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */


package com.trifork.stamdata.views.dkma;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.stamdata.views.View;
import com.trifork.stamdata.views.ViewPath;


@Entity
@XmlRootElement
@ViewPath("dkma/pakning/v1")
public class Pakning extends View {

	@Id
	@GeneratedValue
	@Column(name = "PakningPID")
	@XmlTransient
	private BigInteger recordID;

	@Column(name = "Varenummer")
	protected BigInteger varenummer;

	@Column(name = "VarenummerDelpakning")
	protected BigInteger varenummerDelpakning;

	@Column(name = "DrugID")
	protected String drugId;

	@Column(name = "PakningsstoerrelseNumerisk")
	protected Double pakningsstoerrelseNumerisk;

	@Column(name = "Pakningsstoerrelsesenhed")
	protected String pakningsstoerrelseEnhed;

	@Column(name = "PakningsstoerrelseTekst")
	protected String pakningsstoerrelseTekst;

	@Column(name = "EmballageTypeKode")
	protected String emballageTypeKode;

	@Column(name = "Dosisdispenserbar")
	protected Boolean dosisdispenserbar;

	@Column(name = "MedicintilskudsKode")
	protected String medicintilskudsKode;

	@Column(name = "KlausuleringsKode")
	protected String klausuleringsKode;

	@Column(name = "AlfabetSekvensnr")
	protected BigInteger alfabetSekvensNummer;

	@Column(name = "AntalDelpakninger")
	protected BigInteger antalDelpakninger;

	@Column(name = "Udleveringsbestemmelse")
	protected String udleveringsbestemmelse;

	@Column(name = "UdleveringSpeciale")
	protected String udleveringSpeciale;

	@Column(name = "AntalDDDPrPakning")
	protected Double antalDDDPrPakning;

	@Column(name = "OpbevaringstidNumerisk")
	protected BigInteger opbevaringstidNumerisk;

	@Column(name = "Opbevaringstid")
	protected BigInteger opbevaringstid;

	@Column(name = "Opbevaringsbetingelser")
	protected String opbevaringsbetingelser; // TODO: VARCHAR(1) why?

	@Column(name = "Oprettelsesdato")
	protected String oprettelsesdato; // TODO: VARCHAR why?

	@Column(name = "DatoForSenestePrisaendring")
	protected String datoForSenestePrisaendring; // TODO: VARCHAR why?

	@Column(name = "UdgaaetDato")
	protected String udgaaetDato;

	@Column(name = "BeregningskodeAIRegpris")
	protected String BeregningskodeAIRegpris;

	@Column(name = "PakningOptagetITilskudsgruppe")
	protected Boolean pakningOptagetITilskudsgruppe;

	@Column(name = "Faerdigfremstillingsgebyr")
	protected Boolean faerdigfremstillingsgebyr;

	@Column(name = "Pakningsdistributoer")
	protected BigInteger pakningsdistributoer;

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
