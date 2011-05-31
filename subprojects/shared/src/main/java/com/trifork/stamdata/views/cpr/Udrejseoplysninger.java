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

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.trifork.stamdata.views.ViewPath;

@Entity
@XmlRootElement
@ViewPath("cpr/udrejseoplysninger/v1")
@XmlAccessorType(XmlAccessType.FIELD)
@AttributeOverride(name = "recordID",column = @Column(name = "UdrejseoplysningerPID"))
public class Udrejseoplysninger extends CprView {

	@XmlElement(required = true)
	public String udrejseLandekode;

	@XmlElement(required = true)
	public Date udrejsedato;

	public String udrejsedatoUsikkerhedsmarkering;

	public String udlandsadresse1;
	public String udlandsadresse2;
	public String udlandsadresse3;
	public String udlandsadresse4;
	public String udlandsadresse5;

	@Override
	public String getId() {
		return cpr;
	}

	@Override
	public String toString() {
		return "Udrejseoplysninger[cpr=" + cpr + ", landekode=" + udrejseLandekode + ", udrejsedato=" + udrejsedato + "]";
	}
}
