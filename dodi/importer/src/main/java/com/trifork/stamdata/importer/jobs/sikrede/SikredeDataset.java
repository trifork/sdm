package com.trifork.stamdata.importer.jobs.sikrede;

import java.util.*;

import com.trifork.stamdata.importer.persistence.*;


public class SikredeDataset
{
	@SuppressWarnings("unchecked")
	private final List<Dataset<? extends CPREntity>> datasets = Arrays.asList(new Dataset<Sikrede>(Sikrede.class), new Dataset<SikredeYderRelation>(SikredeYderRelation.class), new Dataset<SaerligSundhedskort>(SaerligSundhedskort.class));

	private Date validFrom;
	private Date previousFileValidFrom;

	public Date getValidFrom()
	{
		return validFrom;
	}

	public void setValidFrom(Date validFrom)
	{
		this.validFrom = validFrom;
	}

	public Date getPreviousFileValidFrom()
	{
		return previousFileValidFrom;
	}

	public void setPreviousFileValidFrom(Date previousFileValidFrom)
	{
		this.previousFileValidFrom = previousFileValidFrom;
	}

	public <T extends CPREntity> void addEntity(T entity)
	{
		entity.setDataset(this);
		for (Dataset<? extends StamdataEntity> dataset : datasets)
		{
			if (dataset.getType().equals(entity.getClass()))
			{
				@SuppressWarnings("unchecked")
				Dataset<T> typedDataset = (Dataset<T>) dataset;
				typedDataset.addEntity(entity);
			}
		}
	}

	public List<Dataset<? extends CPREntity>> getDatasets()
	{
		return datasets;
	}

	@SuppressWarnings("unchecked")
	public <T extends StamdataEntity> Dataset<T> getDataset(Class<T> entityClass)
	{
		for (Dataset<? extends StamdataEntity> dataset : datasets)
		{
			if (dataset.getType().equals(entityClass))
			{
				return (Dataset<T>) dataset;
			}
		}

		throw new IllegalArgumentException("Unknown entity_class=" + entityClass);
	}
}
