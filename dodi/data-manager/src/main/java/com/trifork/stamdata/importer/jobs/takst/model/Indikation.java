/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */


package com.trifork.stamdata.importer.jobs.takst.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.trifork.stamdata.importer.jobs.takst.TakstEntity;


@Entity(name = "Indikation")
public class Indikation extends TakstEntity
{
	private Long indikationskode; // Ref. t. LMS25
	private String indikationstekstTotal; // Felt 03 + 04 + 05
	private String indikationstekstLinie1;
	private String indikationstekstLinie2;
	private String indikationstekstLinie3;
	private String aktivInaktiv; // A = Aktiv kode. I = Inaktiv kode (bør ikke anvendes)
	// TODO: Hvorfor står denne som "bør ikke anvendes"?

	@Column(name = "aktiv")
	public boolean isActive()
	{
		return "A".equalsIgnoreCase(this.aktivInaktiv);
	}

	@Id
	@Column(name = "IndikationKode")
	public Long getIndikationskode()
	{
		return indikationskode;
	}

	@Column
	public String getIndikationstekstLinie1()
	{
		return indikationstekstLinie1;
	}

	@Column
	public String getIndikationstekstLinie2()
	{
		return indikationstekstLinie2;
	}

	@Column
	public String getIndikationstekstLinie3()
	{
		return indikationstekstLinie3;
	}

	@Column(name = "IndikationTekst")
	public String getIndikationstekstTotal()
	{
		return indikationstekstTotal;
	}

	@Override
	public Long getKey()
	{
		return indikationskode;
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
