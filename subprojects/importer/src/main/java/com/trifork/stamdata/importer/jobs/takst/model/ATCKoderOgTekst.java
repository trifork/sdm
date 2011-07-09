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

package com.trifork.stamdata.importer.jobs.takst.model;

import java.util.ArrayList;
import java.util.List;

import com.trifork.stamdata.importer.jobs.takst.TakstDataset;
import com.trifork.stamdata.importer.jobs.takst.TakstEntity;
import com.trifork.stamdata.importer.persistence.Id;
import com.trifork.stamdata.importer.persistence.Output;


@Output(name = "ATC")
public class ATCKoderOgTekst extends TakstEntity
{
	private String atc;
	private String tekst;

	public List<Indikation> getIndikationer()
	{
		TakstDataset<Indikationskode> indikationskoder = takst.getDatasetOfType(Indikationskode.class);
		List<Indikation> indikationer = new ArrayList<Indikation>();

		for (Indikationskode ik : indikationskoder.getEntities())
		{
			if (ik.getATC().equals(this.getKey())) indikationer.add(takst.getEntity(Indikation.class, ik.getIndikationskode()));
		}

		return indikationer;
	}

	@Override
	@Id
	@Output(name = "ATC")
	public String getKey()
	{
		return atc;
	}

	@Output(name = "ATCTekst")
	public String getTekst()
	{
		return this.tekst;
	}

	public Boolean isTilHumanAnvendelse()
	{
		return !atc.startsWith("Q");
	}

	public void setATC(String atc)
	{
		this.atc = atc;
	}

	public void setTekst(String tekst)
	{
		this.tekst = tekst;
	}
}
