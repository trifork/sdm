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

package dk.trifork.sdm.importer.autorisationsregister.model;

import dk.trifork.sdm.model.AbstractStamdataEntity;
import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;
import dk.trifork.sdm.model.StamdataEntity;

import java.util.Calendar;
import java.util.StringTokenizer;

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

