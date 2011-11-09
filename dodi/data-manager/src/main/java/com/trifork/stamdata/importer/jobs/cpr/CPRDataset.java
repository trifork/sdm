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


package com.trifork.stamdata.importer.jobs.cpr;

import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.trifork.stamdata.importer.jobs.cpr.models.BarnRelation;
import com.trifork.stamdata.importer.jobs.cpr.models.CPREntity;
import com.trifork.stamdata.importer.jobs.cpr.models.ForaeldreMyndighedRelation;
import com.trifork.stamdata.importer.jobs.cpr.models.Klarskriftadresse;
import com.trifork.stamdata.importer.jobs.cpr.models.NavneBeskyttelse;
import com.trifork.stamdata.importer.jobs.cpr.models.Navneoplysninger;
import com.trifork.stamdata.importer.jobs.cpr.models.Personoplysninger;
import com.trifork.stamdata.importer.jobs.cpr.models.UmyndiggoerelseVaergeRelation;
import com.trifork.stamdata.importer.persistence.Dataset;
import com.trifork.stamdata.models.TemporalEntity;


public class CPRDataset
{
	@SuppressWarnings("unchecked")
	private final List<Dataset<? extends CPREntity>> datasets = Lists.newArrayList(
			new Dataset<Personoplysninger>(Personoplysninger.class),
			new Dataset<Klarskriftadresse>(Klarskriftadresse.class),
			new Dataset<NavneBeskyttelse>(NavneBeskyttelse.class),
			new Dataset<Navneoplysninger>(Navneoplysninger.class),
			new Dataset<UmyndiggoerelseVaergeRelation>(UmyndiggoerelseVaergeRelation.class),
			new Dataset<ForaeldreMyndighedRelation>(ForaeldreMyndighedRelation.class),
			new Dataset<BarnRelation>(BarnRelation.class)
	);

	private Date validFrom;

	public Date getValidFrom()
	{
		return validFrom;
	}

	public void setValidFrom(Date validFrom)
	{
		this.validFrom = validFrom;
	}

	public <T extends CPREntity> void addEntity(T entity)
	{
		entity.setDataset(this);
		for (Dataset<? extends TemporalEntity> dataset : datasets)
		{
			if (dataset.getType().equals(entity.getClass()))
			{
				@SuppressWarnings("unchecked")
				Dataset<T> typedDataset = (Dataset<T>) dataset;
				typedDataset.add(entity);
			}
		}
	}

	public List<Dataset<? extends CPREntity>> getDatasets()
	{
		return datasets;
	}

	@SuppressWarnings("unchecked")
	public <T extends TemporalEntity> Dataset<T> getDataset(Class<T> entityClass)
	{
		for (Dataset<? extends TemporalEntity> dataset : datasets)
		{
			if (dataset.getType().equals(entityClass))
			{
				return (Dataset<T>) dataset;
			}
		}

		throw new IllegalArgumentException("Ukendt entitetsklasse: " + entityClass);
	}
}
