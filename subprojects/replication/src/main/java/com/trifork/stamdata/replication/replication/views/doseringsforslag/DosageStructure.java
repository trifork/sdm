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

package com.trifork.stamdata.replication.replication.views.doseringsforslag;

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

import com.trifork.stamdata.Documented;
import com.trifork.stamdata.replication.replication.annotations.ViewPath;
import com.trifork.stamdata.replication.replication.views.View;


@Entity
@XmlRootElement
@Documented("Indeholder doseringsstrukturer.")
@ViewPath("doseringsforslag/dosagestructure/v1")
public class DosageStructure extends View {

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
	@Column(name="ValidFrom")
	protected Date validFrom;

	@XmlTransient
	@Temporal(TIMESTAMP)
	@Column(name="ModifiedDate")
	protected Date modifiedDate;

	@Override
	public String getId() {

		return code;
	}

	@Override
	public BigInteger getRecordID() {

		return recordID;
	}

	@Override
	public Date getUpdated() {

		return modifiedDate;
	}
}
