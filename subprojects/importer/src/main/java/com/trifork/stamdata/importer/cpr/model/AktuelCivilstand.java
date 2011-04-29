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

package com.trifork.stamdata.importer.cpr.model;

import java.util.Calendar;
import java.util.Date;

import com.trifork.stamdata.model.Id;
import com.trifork.stamdata.model.Output;
import com.trifork.stamdata.util.DateUtils;


public class AktuelCivilstand extends CPREntity {
	public enum Civilstand {
		ugift("U"), gift("G"), fraskilt("F"), enkeEllerEnkemand("E"), registreretPartnerskab("P"),
		ophaevetPartnerskab("O"), laengstlevendePartner("L"), doed("D");

		private final String code;
		private Civilstand(String code) {
			this.code = code;
		}
		public String getCode() {
			return code;
		}
		public static Civilstand fromCode(String code) {
			for (Civilstand civilstand : values()) {
				if (civilstand.getCode().equals(code)) {
					return civilstand;
				}
			}
			throw new IllegalArgumentException("Ugyldig civilstand: '" + code + "'");
		}
	}

	private Civilstand civilstand;
	private String aegtefaellepersonnummer;
	private Date aegtefaellefoedselsdato;
	private String aegtefaellefoedselsdatomarkering;
	private String aegtefaellenavn;
	private String aegtefaellenavnmarkering;
	private Calendar validFrom;
	private String startdatomarkering;
	private Date separation;

	@Id
	@Output
	public String getCpr() {
		return cpr;
	}

	public void setCivilstand(Civilstand civilstand) {
		this.civilstand = civilstand;
	}

	public Civilstand getCivilstand() {
		return civilstand;
	}

	public void setCivilstandskode(String kode) {
		this.civilstand = Civilstand.fromCode(kode);
	}

	@Output
	public String getCivilstandskode() {
		return civilstand.getCode();
	}

	public void setAegtefaellepersonnummer(String aegtefaellepersonnummer) {
		this.aegtefaellepersonnummer = aegtefaellepersonnummer;
	}

	@Output
	public String getAegtefaellepersonnummer() {
		return aegtefaellepersonnummer;
	}

	public void setAegtefaellefoedselsdato(Date aegtefaellefoedselsdato) {
		this.aegtefaellefoedselsdato = aegtefaellefoedselsdato;
	}

	@Output
	public Date getAegtefaellefoedselsdato() {
		return aegtefaellefoedselsdato;
	}

	public void setAegtefaellefoedselsdatomarkering(String aegtefaellefoedselsdatomarkering) {
		this.aegtefaellefoedselsdatomarkering = aegtefaellefoedselsdatomarkering;
	}

	public String getAegtefaellefoedselsdatomarkering() {
		return aegtefaellefoedselsdatomarkering;
	}

	public void setAegtefaellenavn(String aegtefaellenavn) {
		this.aegtefaellenavn = aegtefaellenavn;
	}

	@Output
	public String getAegtefaellenavn() {
		return aegtefaellenavn;
	}

	public void setAegtefaellenavnmarkering(String aegtefaellenavnmarkering) {
		this.aegtefaellenavnmarkering = aegtefaellenavnmarkering;
	}

	public String getAegtefaellenavnmarkering() {
		return aegtefaellenavnmarkering;
	}


	public Calendar getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Calendar validFrom) {
		if(validFrom == null) {
			this.validFrom = DateUtils.PAST;
		}
		else {
			this.validFrom = validFrom;
		}
	}

	public void setStartdatomarkering(String startdatomarkering) {
		this.startdatomarkering = startdatomarkering;
	}

	public String getStartdatomarkering() {
		return startdatomarkering;
	}

	public void setSeparation(Date separation) {
		this.separation = separation;
	}

	@Output
	public Date getSeparation() {
		return separation;
	}
}
