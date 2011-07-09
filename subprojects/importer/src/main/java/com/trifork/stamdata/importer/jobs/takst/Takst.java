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

package com.trifork.stamdata.importer.jobs.takst;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.trifork.stamdata.importer.persistence.CompleteDataset;
import com.trifork.stamdata.importer.persistence.Dataset;
import com.trifork.stamdata.importer.persistence.Id;
import com.trifork.stamdata.importer.persistence.Output;
import com.trifork.stamdata.importer.persistence.StamdataEntity;


/**
 * A version of the Takst
 *
 * @author Rune
 */
@Output(name = "TakstVersion")
public class Takst extends TakstEntity
{
	private final List<CompleteDataset<? extends StamdataEntity>> datasets = new ArrayList<CompleteDataset<? extends StamdataEntity>>();

	// The week-number for which LMS guarantees some sort of stability/validity
	// for a subset of this takst. (The stable subset excludes pricing and
	// substitions and possibly more)
	private int validityYear, validityWeekNumber;

	private Date validFrom, validTo;

	public Takst(Date validFrom, Date validTo)
	{
		this.validFrom = validFrom;
		this.validTo = validTo;
	}

	public void addDataset(TakstDataset<?> dataset)
	{
		datasets.add(dataset);
	}

	/**
	 * @param clazz the Class that the returned entities should have
	 * @return All entities of the given type in this takst.
	 */
	public <A extends TakstEntity> TakstDataset<A> getDatasetOfType(Class<A> clazz)
	{
		for (Dataset<? extends StamdataEntity> dataset : datasets)
		{
			if (clazz.equals(dataset.getType())) return (TakstDataset<A>) dataset;
		}
		
		return null;
	}

	public List<CompleteDataset<? extends StamdataEntity>> getDatasets()
	{
		return datasets;
	}

	/**
	 * @param type, the type of the requested entity
	 * @param entityId the id of the requested entity
	 * @return the requested entity
	 */
	public <T extends TakstEntity> T getEntity(Class<T> type, Object entityId)
	{
		if (entityId == null) return null;

		Dataset<?> avds = getDatasetOfType(type);

		if (avds == null) return null;

		return (T)avds.getEntityById(entityId);
	}

	@Output(name = "TakstUge")
	public String getStableWeek()
	{
		return "" + validityYear + validityWeekNumber;
	}

	@Id
	@Override
	public Date getValidFrom()
	{
		return validFrom;
	}

	@Override
	public Date getValidTo()
	{
		return validTo;
	}

	public void setValidityWeekNumber(int validityWeekNumber)
	{
		this.validityWeekNumber = validityWeekNumber;
	}

	public void setValidityYear(int validityYear)
	{
		this.validityYear = validityYear;
	}
}
