
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

package com.trifork.stamdata.importer.autorisationsregister.model;


import java.util.Calendar;
import java.util.StringTokenizer;

import com.trifork.stamdata.model.AbstractStamdataEntity;
import com.trifork.stamdata.model.Id;
import com.trifork.stamdata.model.Output;
import com.trifork.stamdata.model.StamdataEntity;

@Output
public class Autorisation extends AbstractStamdataEntity implements StamdataEntity{
    private String nummer;
    private String cpr;
    private String efternavn;
    private String fornavn;
    private String uddKode;

    Autorisationsregisterudtraek dataset;

	public Autorisation(String line){
		StringTokenizer st = new StringTokenizer(line, ";");
		nummer = st.nextToken();
		cpr = st.nextToken();
		efternavn = st.nextToken();
		fornavn = st.nextToken();
		uddKode = st.nextToken();
	}

	@Output
	@Id
	public String getAutorisationsnummer() {
		return nummer;
	}

	@Output
	public String getCpr() {
		return cpr;
	}

	@Output
	public String getEfternavn() {
		return efternavn;
	}

	@Output
	public String getFornavn() {
		return fornavn;
	}

	@Output
	public String getUddannelsesKode() {
		return uddKode;
	}


	@Override
	public Calendar getValidFrom() {
		return dataset.getValidFrom();
	}

	@Override
	public Calendar getValidTo() {
		return FUTURE;
	}

}
