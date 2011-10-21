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


package dk.nsi.stamdata.views.doseringsforslag;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import dk.nsi.stamdata.views.View;
import dk.nsi.stamdata.views.ViewPath;


@Entity
@XmlRootElement
@ViewPath("doseringsforslag/dosagestructure/v1")
public class DosageStructure extends View
{
	@Id
	@GeneratedValue
	@XmlTransient
	@Column(name = "DosageStructurePID")
	protected BigInteger recordID;

	// Unik kode for doseringstrukturen. Obligatorisk. Heltal, 11 cifre.
	@Column(length = 11)
	protected String code;

	// Reference til releaseNumber i Version. Obligatorisk. Heltal, 15 cifre.
	@Column(length = 15)
	protected long releaseNumber;

	// Typen af dosering, enten "M+M+A+N", "PN", "N daglig",
	// "Fritekst" eller "Kompleks". Obligatorisk. Streng, 100 tegn.
	@Column(length = 100)
	protected String type;

	// For simple typer (dvs. alt andet end "Kompleks")
	// indeholder feltet doseringen på simpel form. Optionelt. Streng, 100 tegn.
	@Column(length = 100)
	protected String simpleString;

	// For simple typer en eventuel supplerende tekst.
	// Optionelt. Streng, 200 tegn.
	@Column(length = 200)
	protected String supplementaryText;

	// FMKs strukturerede dosering i XML format. Bemærk at enkelte
	// værdier vil være escaped. Obligatorisk. Streng, 10000 tegn.
	@Column(length = 10000)
	protected String xml;

	// Såfremt det er muligt at lave en kort
	// doseringstekst på baggrund af xml og lægemidlets doseringsenhed vil
	// dette felt indeholde denne. Optionelt. Streng, 70 tegn.
	@Column(length = 70)
	protected String shortTranslation;

	// En lang doseringstekst baggrund af xml og
	// lægemidlets doseringsenhed. Obligatorisk. Streng 10000 tegn.
	@Column(length = 10000)
	protected String longTranslation;

	@Temporal(TIMESTAMP)
	@Column(name = "ValidFrom")
	protected Date validFrom;

	@Temporal(TIMESTAMP)
	protected Date validTo;

	@XmlTransient
	@Temporal(TIMESTAMP)
	@Column(name = "ModifiedDate")
	protected Date modifiedDate;

	@Override
	public String getId()
	{
		return code;
	}

	@Override
	public BigInteger getRecordID()
	{
		return recordID;
	}

	@Override
	public Date getUpdated()
	{
		return modifiedDate;
	}
}
