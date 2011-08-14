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

package com.trifork.stamdata.importer.jobs.dkma.model;

import com.trifork.stamdata.importer.jobs.dkma.TakstEntity;
import com.trifork.stamdata.importer.persistence.*;


@Output
public class Dosering extends TakstEntity
{
	private Long kode; // Ref. t. LMS27
	private String kortTekst;
	private String tekst; // Felt 05 + 06 + 07
	private Long antalEnhDoegn;
	
	// TODO: What is meant by this?
	// A = Aktiv kode. I = Inaktiv kode (bør ikke anvendes)
	private String aktivInaktiv; 

	@Output
	public String getAktiv()
	{
		return aktivInaktiv;
	}

	@Output
	public Long getAntalEnhederPrDoegn()
	{
		return antalEnhDoegn;
	}

	@Id
	@Output
	public Long getKode()
	{
		return kode;
	}

	@Output
	public String getKortTekst()
	{
		return kortTekst;
	}

	@Output
	public String getTekst()
	{
		return tekst;
	}

	@Override
	public Long getKey()
	{
		return kode;
	}

	public void setAktiv(String value)
	{
		this.aktivInaktiv = value;
	}

	public void setAntalEnhDoegn(Long value)
	{
		this.antalEnhDoegn = value;
	}

	public void setKortTekst(String value)
	{
		this.kortTekst = value;
	}

	public void setKode(Long value)
	{
		this.kode = value;
	}

	public void setTekst(String doseringstekstTotal)
	{
		this.tekst = doseringstekstTotal;
	}
}
