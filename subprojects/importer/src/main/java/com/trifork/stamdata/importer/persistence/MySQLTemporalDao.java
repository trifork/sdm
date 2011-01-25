package com.trifork.stamdata.importer.persistence;


import java.sql.Connection;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trifork.stamdata.Record;
import com.trifork.stamdata.importer.jobs.FilePersistException;
import com.trifork.stamdata.importer.persistence.MySQLTemporalTable.RecordVersion;
import com.trifork.stamdata.persistence.CompleteDataset;
import com.trifork.stamdata.persistence.Dataset;


public class MySQLTemporalDao implements RecordPersister
{

	private static final Logger LOGGER = LoggerFactory.getLogger(MySQLTemporalDao.class);
	protected final Connection connection;


	public MySQLTemporalDao(Connection connection)
	{
		this.connection = connection;
	}


	public void persistCompleteDatasets(List<CompleteDataset<? extends Record>> datasets) throws FilePersistException
	{
		LOGGER.debug("Starting to put records from dataset group.");

		for (CompleteDataset<? extends Record> dataset : datasets)
		{

			persistCompleteDataset(dataset);
		}

		LOGGER.debug("Done putting records from dataset group.");
	}


	@Override
	public void persistCompleteDataset(CompleteDataset<?> dataset) throws FilePersistException
	{

		if (!dataset.getType().isAnnotationPresent(Entity.class)) return;

		updateValidToOnRecordsNotInDataset(dataset);
		persistDeltaDataset(dataset);
	}


	/**
	 * For each entity of this dataset, it is checked if it is changed from what
	 * is present in mysql. If an entity is changed, the existing, mysql record
	 * is "closed" by assigning validto, and a new MySQL record created to
	 * represent the new state of the entity.
	 * 
	 * It is also checked, if some of the "open" MySQL records are not present
	 * in this dataset. If an entity is no longer in the dataset, its record
	 * will be "closed" in mysql by assigning validto.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void persistDeltaDataset(Dataset<? extends Record> dataset) throws FilePersistException
	{

		final Date now = new Date();

		MySQLTemporalTable table = getTable(dataset.getType());

		LOGGER.debug("persistDeltaDataset dataset: " + dataset.getEntityTypeDisplayName() + " with: "
				+ dataset.getEntities().size() + " entities...");

		int processedEntities = 0;

		for (Record sde : dataset.getEntities())
		{

			processedEntities++;

			Date validFrom = sde.getValidFrom();

			boolean exists = table.fetchEntityVersions(sde.getKey(), validFrom, sde.getValidTo());

			if (!exists)
			{
				// Entity was not found, so create it
				table.insertRow(sde, now);
			}
			else
			{
				// At least one version was found in the same validity range.
				boolean insertVersion = true;

				do
				{
					Date existingValidFrom = table.getCurrentRowValidFrom();
					Date existingValidTo = table.getCurrentRowValidTo();

					boolean dataEquals = table.dataInCurrentRowEquals(sde);

					if (existingValidFrom.before(sde.getValidFrom()))
					{
						if (existingValidTo.equals(sde.getValidFrom()))
						{
							// This existing row is not in the range of our
							// entity
							continue;
						}
						// our entity is newer.
						if (existingValidTo.after(sde.getValidTo()))
						{
							// Our version is inside the existing version,
							if (!dataEquals)
							{
								// The existing version must be split in two.
								// Copy existing row. Set validfrom in copy
								// entity to our validto.
								table.copyCurrentRowButWithChangedValidFrom(sde.getValidTo(), now);
								// Set validto in existing entity to our
								// validfrom.
								table.updateValidToOnCurrentRow(sde.getValidFrom(), now);
							}
						}
						else if (existingValidTo.before(sde.getValidTo()))
						{
							// Our version starts after the existing, but ends
							// later.
							if (dataEquals)
							{
								// If necesary, increase validto on existing
								// entity to our validTo.
								if (table.getCurrentRowValidTo().before(sde.getValidTo()))
									table.updateValidToOnCurrentRow(sde.getValidTo(), now);
								// No need to insert our version as the range is
								// covered by existing version
								insertVersion = false;
							}
							else
							{
								// Our version starts after the existing, but
								// ends at the same time.
								// Set validto in existing entity to our
								// validfrom.
								table.updateValidToOnCurrentRow(sde.getValidFrom(), now);
							}
						}
						else
						{
							// Our version is newer. Same validTo
							if (dataEquals)
							{
								// do nothing
								insertVersion = false;
							}
							else
							{
								// invalidate the existing.
								table.updateValidToOnCurrentRow(sde.getValidFrom(), now);
							}
						}
					}
					else if (existingValidFrom.after(sde.getValidFrom()))
					{
						// Our version is older as that the existing one
						if (sde.getValidTo().after((existingValidTo)))
						{
							// Our version encompasses the entire existing
							// version,
							if (dataEquals)
							{
								// reuse the existing version
								table.updateValidFromOnCurrentRow(sde.getValidFrom(), now);
								table.updateValidToOnCurrentRow(sde.getValidTo(), now);
							}
							else
							{
								// The existing must be deleted
								// Delete existing row
								table.updateRow(sde, now, existingValidFrom, existingValidTo);
							}

							insertVersion = false;
						}
						else if (sde.getValidTo().before((existingValidTo)))
						{
							// Our version starts before the existing, but also
							// ends before.
							if (dataEquals)
							{
								// Set validfrom in existing entity to our
								// validfrom.
								table.updateValidFromOnCurrentRow(sde.getValidFrom(), now);
								insertVersion = false;
							}
							else
							{
								// Set validfrom in existing entity to our
								// validto.
								table.updateValidFromOnCurrentRow(sde.getValidTo(), now);
							}
						}
						else
						{
							// Our version starts before the existing, and ends
							// at the same time
							table.updateRow(sde, now, existingValidFrom, existingValidTo);
							insertVersion = false;
						}
					}
					else
					{
						// Our version is as old as the existing one
						if (sde.getValidTo().after((existingValidTo)))
						{
							// Our version has the same validfrom but later
							// validto as the existing.
							table.updateValidToOnCurrentRow(sde.getValidTo(), now);
							insertVersion = false;
						}
						else if (sde.getValidTo().before((existingValidTo)))
						{
							// Our version has the same validfrom but earlier
							// validto as the existing.
							if (dataEquals)
							{
								table.updateValidToOnCurrentRow(sde.getValidTo(), now);
								insertVersion = false;
							}
							else
							{
								table.updateValidFromOnCurrentRow(sde.getValidTo(), now);
							}
						}
						else
						{
							// Our version has the same validfrom and validto as
							// the existing.
							if (!dataEquals)
							{
								// replace the existing
								table.updateRow(sde, now, existingValidFrom, existingValidTo);
							}
							insertVersion = false;
						}

					}
				}
				while (table.hasMoreRows());

				if (insertVersion)
				{
					table.insertAndUpdateRow(sde, now);
				}
			}
		}

		LOGGER.debug("...persistDeltaDataset complete. " + processedEntities + " processed, " + table.getInsertedRows()
				+ " inserted, " + table.getUpdatedRecords() + " updated, " + table.getDeletedRecords() + " deleted");
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	public MySQLTemporalTable getTable(Class<? extends Record> type) throws FilePersistException
	{

		return new MySQLTemporalTable(connection, type);
	}


	/**
	 * @param dataset
	 * @throws FilePersistException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void updateValidToOnRecordsNotInDataset(CompleteDataset<? extends Record> dataset) throws FilePersistException
	{

		LOGGER.debug("updateValidToOnRecordsNotInDataset " + dataset.getEntityTypeDisplayName() + " starting...");

		Date now = new Date();

		MySQLTemporalTable table = getTable(dataset.getType());

		List<RecordVersion> evs = table.getRecordVersions(dataset.getValidFrom(), dataset.getValidTo());

		int nExisting = 0;

		for (RecordVersion ev : evs)
		{

			List<? extends Record> entitiesWithId = dataset.getEntitiesById(ev.id);

			boolean recordFoundInCompleteDataset = entitiesWithId != null && entitiesWithId.size() > 0;

			if (!recordFoundInCompleteDataset)
			{
				table.updateValidToOnEntityVersion(dataset.getValidFrom(), ev, now);
			}

			if (++nExisting % 10000 == 0)
			{
				LOGGER.debug("Processed " + nExisting + " existing records of type "
						+ dataset.getEntityTypeDisplayName());
			}
		}

		LOGGER.debug("...updateValidToOnRecordsNotInDataset " + dataset.getEntityTypeDisplayName()
				+ " complete. Updated: " + table.getUpdatedRecords() + " records.");
	}


	public Connection getConnection()
	{

		return this.connection;
	}
}
