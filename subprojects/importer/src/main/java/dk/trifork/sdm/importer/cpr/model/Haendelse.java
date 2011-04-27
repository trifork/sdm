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

package dk.trifork.sdm.importer.cpr.model;

import java.util.Date;

import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;

public class Haendelse extends CPREntity {
	String uuid;
	Date ajourfoeringsdato;
	String haendelseskode;
	String afledtMarkering;
	String noeglekonstant;

	@Id
	@Output
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	@Output
	public String getCpr() {
		return cpr;
	}
	
	@Output
	public Date getAjourfoeringsdato() {
		return ajourfoeringsdato;
	}

	public void setAjourfoeringsdato(Date ajourfoeringsdato) {
		this.ajourfoeringsdato = ajourfoeringsdato;
	}

	@Output
	public String getHaendelseskode() {
		return haendelseskode;
	}

	public void setHaendelseskode(String haendelseskode) {
		this.haendelseskode = haendelseskode;
	}
	
	@Output
	public String getAfledtMarkering() {
		return afledtMarkering;
	}

	public void setAfledtMarkering(String afledtMarkering) {
		this.afledtMarkering = afledtMarkering;
	}

	@Output
	public String getNoeglekonstant() {
		return noeglekonstant;
	}

	public void setNoeglekonstant(String noeglekonstant) {
		this.noeglekonstant = noeglekonstant;
	}
}
