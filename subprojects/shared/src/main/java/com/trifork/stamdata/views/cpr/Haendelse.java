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

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.trifork.stamdata.views.ViewPath;

@Entity
@XmlRootElement
@ViewPath("cpr/haendelse/v1")
@XmlAccessorType(XmlAccessType.FIELD)
@AttributeOverride(name = "recordID",column = @Column(name = "HaendelsePID"))
public class Haendelse extends CprView {

	@XmlElement(required = true)
	protected String uuid;

	@Temporal(TIMESTAMP)
	protected Date ajourfoeringsdato;

	protected String haendelseskode;
	protected String afledtMarkering;
	protected String noeglekonstant;

	@Override
	public String getId() {
		return uuid;
	}

	@Override
	public String toString() {
		return "Hændelse[cpr=" + cpr + ", ajourføringsdato=" + ajourfoeringsdato + ", hændelseskode=" + haendelseskode + ", nøglekonstant=" + noeglekonstant + "]";
	}
}
