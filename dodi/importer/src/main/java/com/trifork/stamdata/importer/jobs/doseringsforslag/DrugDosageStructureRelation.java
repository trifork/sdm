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

package com.trifork.stamdata.importer.jobs.doseringsforslag;

import java.util.Date;

import com.trifork.stamdata.importer.persistence.*;


@Output
public class DrugDosageStructureRelation extends AbstractStamdataEntity
{
	// Reference til releaseNumber i Version. Obligatorisk. Heltal, 15 cifre.
	private long releaseNumber;

	// Lægemidlets drug id. Reference til drugId i drugs. Obligatorisk. Heltal,
	// 11 cifre.
	private long drugId;

	// Reference til code i dosageStructure. Obligatorisk. Heltal, 11 cifre.
	private long dosageStructureCode;

	public void setReleaseNumber(long releaseNumber)
	{

		this.releaseNumber = releaseNumber;
	}

	@Id
	@Output
	public String getId()
	{

		return Long.toString(drugId) + Long.toString(dosageStructureCode);
	}

	@Output
	public long getReleaseNumber()
	{

		return releaseNumber;
	}

	public void setDrugId(long drugId)
	{

		this.drugId = drugId;
	}

	@Output
	public long getDrugId()
	{

		return drugId;
	}

	public void setDosageStructureCode(long dosageStructureCode)
	{

		this.dosageStructureCode = dosageStructureCode;
	}

	@Output
	public long getDosageStructureCode()
	{

		return dosageStructureCode;
	}

	@Override
	public Date getValidFrom()
	{
		return null;
	}
}
