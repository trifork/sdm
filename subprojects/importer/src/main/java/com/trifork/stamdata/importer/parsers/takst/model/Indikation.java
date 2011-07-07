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

package com.trifork.stamdata.importer.parsers.takst.model;

import java.util.ArrayList;
import java.util.List;

import com.trifork.stamdata.importer.parsers.takst.TakstDataset;
import com.trifork.stamdata.importer.parsers.takst.TakstEntity;
import com.trifork.stamdata.importer.persistence.Id;
import com.trifork.stamdata.importer.persistence.Output;


@Output(name = "Indikation")
public class Indikation extends TakstEntity
{

	private Long indikationskode; // Ref. t. LMS25
	private String indikationstekstTotal; // Felt 03 + 04 + 05
	private String indikationstekstLinie1;
	private String indikationstekstLinie2;
	private String indikationstekstLinie3;
	private String aktivInaktiv; // A = Aktiv kode. I = Inaktiv kode (bør ikke
									// anvendes)

	@Output(name = "aktiv")
	public Boolean getAktivInaktiv()
	{
		return "A".equalsIgnoreCase(this.aktivInaktiv);
	}

	public List<ATCKoderOgTekst> getATC()
	{
		TakstDataset<Indikationskode> indikationskoder = takst.getDatasetOfType(Indikationskode.class);
		List<ATCKoderOgTekst> atcKoder = new ArrayList<ATCKoderOgTekst>();
		for (Indikationskode ik : indikationskoder.getEntities())
		{
			if (ik.getIndikationskode().equals(this.getIndikationskode())) atcKoder.add(takst.getEntity(ATCKoderOgTekst.class, ik.getATC()));
		}
		return atcKoder;
	}

	@Id
	@Output(name = "IndikationKode")
	public Long getIndikationskode()
	{
		return this.indikationskode;
	}

	@Output
	public String getIndikationstekstLinie1()
	{
		return this.indikationstekstLinie1;
	}

	@Output
	public String getIndikationstekstLinie2()
	{
		return this.indikationstekstLinie2;
	}

	@Output
	public String getIndikationstekstLinie3()
	{
		return this.indikationstekstLinie3;
	}

	@Output(name = "IndikationTekst")
	public String getIndikationstekstTotal()
	{
		return this.indikationstekstTotal;
	}

	@Override
	public Long getKey()
	{
		return this.indikationskode;
	}

	public void setAktivInaktiv(String aktivInaktiv)
	{
		this.aktivInaktiv = aktivInaktiv;
	}

	public void setIndikationskode(Long indikationskode)
	{
		this.indikationskode = indikationskode;
	}

	public void setIndikationstekstLinie1(String indikationstekstLinie1)
	{
		this.indikationstekstLinie1 = indikationstekstLinie1;
	}

	public void setIndikationstekstLinie2(String indikationstekstLinie2)
	{
		this.indikationstekstLinie2 = indikationstekstLinie2;
	}

	public void setIndikationstekstLinie3(String indikationstekstLinie3)
	{
		this.indikationstekstLinie3 = indikationstekstLinie3;
	}

	public void setIndikationstekstTotal(String indikationstekstTotal)
	{
		this.indikationstekstTotal = indikationstekstTotal;
	}

}
