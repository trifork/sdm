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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.stamdata.views.View;
import com.trifork.stamdata.views.ViewPath;

@Entity
@XmlRootElement
@ViewPath("cpr/foedselsregistreringsoplysninger/v1")
@XmlAccessorType(XmlAccessType.FIELD)
public class Foedselsregistreringsoplysninger extends View {
	@Id
	@GeneratedValue
	@XmlTransient
	@Column(name = "FoedselsregistreringsoplysningerPID")
	protected BigInteger recordID;

	@XmlElement(required = true)
	protected String cpr;
	@XmlElement(required = true)
	protected String foedselsregistreringsstedkode;
	@XmlElement(required = true)
	protected String foedselsregistreringstekst;

	@XmlTransient
	@Temporal(TIMESTAMP)
	protected Date modifiedDate;

	@Temporal(TIMESTAMP)
	protected Date validFrom;

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
		return "Fødselsregistreringsoplysninger[cpr=" + cpr + ", registreringsstedkode=" + foedselsregistreringsstedkode
		+ ", fødselsregistreringstekst=" + foedselsregistreringstekst + "]";
	}
}
