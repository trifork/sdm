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

import com.trifork.stamdata.importer.jobs.takst.TakstEntity;
import com.trifork.stamdata.importer.persistence.Id;
import com.trifork.stamdata.importer.persistence.Output;


@Output(name = "IndikationATCRef")
public class Indikationskode extends TakstEntity
{
	private String atc; // Ref. t. LMS01
	private Long indikationskode; // Ref. t. LMS26
	private Long drugID; // Ref. t. LMS01, felt 01

	@Output
	public String getATC()
	{
		return atc;
	}

	@Id
	@Output
	public String getCID()
	{
		// A calculated ID.
		// Necessary because the DAO implementation needs a single key.
		return atc + "-" + indikationskode;
	}

	@Output
	public Long getDrugID()
	{
		return drugID;
	}

	@Output(name = "IndikationKode")
	public Long getIndikationskode()
	{
		return indikationskode;
	}

	public void setATC(String atc)
	{
		this.atc = atc;
	}

	public void setDrugID(Long drugID)
	{
		this.drugID = drugID;
	}

	public void setIndikationskode(Long indikationskode)
	{
		this.indikationskode = indikationskode;
	}
}
