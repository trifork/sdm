
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

package com.trifork.stamdata.persistence;

import java.sql.Connection;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trifork.stamdata.importer.exceptions.FilePersistException;
import com.trifork.stamdata.model.CompleteDataset;
import com.trifork.stamdata.model.Dataset;
import com.trifork.stamdata.model.Output;
import com.trifork.stamdata.model.StamdataEntity;
import com.trifork.stamdata.persistence.DatabaseTableWrapper.StamdataEntityVersion;



public class AuditingPersister implements Persister {

	private static Logger logger = LoggerFactory.getLogger(AuditingPersister.class);

	protected Connection connection;

	public AuditingPersister(Connection connection) {

		this.connection = connection;
	}

	public void persistCompleteDataset(List<CompleteDataset<? extends StamdataEntity>> datasets) throws FilePersistException {

		// TODO: Remove this method. We should use the version below.

		@SuppressWarnings("unchecked")
		CompleteDataset<? extends StamdataEntity>[] array = datasets.toArray(new CompleteDataset[] {});

		persistCompleteDataset(array);
	}

	public void persistCompleteDataset(CompleteDataset<? extends StamdataEntity>... datasets) throws FilePersistException {

		for (CompleteDataset<? extends StamdataEntity> dataset : datasets) {

			if (!dataset.getType().isAnnotationPresent(Output.class)) continue;

			updateValidToOnRecordsNotInDataset(dataset);
			persistDeltaDataset(dataset);
		}
	}

	/**
	 * For each entity of this dataset, it is checked if it is changed from what
	 * is present in mysql. If an entity is changed, the existing, mysql record
	 * is "closed" by assigning validto, and a new MySQL record created to
	 * represent the new state of the entity.
	 * <p/>
	 * It is also checked, if some of the "open" MySQL records are not present
	 * in this dataset. If an entity is no longer in the dataset, its record
	 * will be "closed" in mysql by assigning validto.
	 */
	public <T extends StamdataEntity> void persistDeltaDataset(Dataset<T> dataset) throws FilePersistException {

		Calendar now = Calendar.getInstance();

		DatabaseTableWrapper<T> table = getTable(dataset.getType());

		logger.debug("persistDeltaDataset dataset: " + dataset.getEntityTypeDisplayName() + " with: " + dataset.getEntities().size() + " entities...");

		int processedEntities = 0;

		for (T record : dataset.getEntities()) {

			processedEntities++;

			Calendar validFrom = record.getValidFrom();

			boolean exists = table.fetchEntityVersions(record.getKey(), validFrom, record.getValidTo());

			if (!exists) {
				// Entity was not found, so create it
				table.insertRow(record, now);
			}
			else {
				// At least one version was found in the same validity range.
				boolean insertVersion = true;

				do {
					Calendar existingValidFrom = table.getCurrentRowValidFrom();
					Calendar existingValidTo = table.getCurrentRowValidTo();

					boolean dataEquals = table.dataInCurrentRowEquals(record);

					if (existingValidFrom.before(record.getValidFrom())) {
						if (existingValidTo.equals(record.getValidFrom())) {
							// This existing row is not in the range of our
							// entity
							continue;
						}
						// our entity is newer.
						if (existingValidTo.after(record.getValidTo())) {
							// Our version is inside the existing version,
							if (!dataEquals) {
								// The existing version must be split in two.
								// Copy existing row. Set validfrom in copy
								// entity to our validto.
								table.copyCurrentRowButWithChangedValidFrom(record.getValidTo(), now);
								// Set validto in existing entity to our
								// validfrom.
								table.updateValidToOnCurrentRow(record.getValidFrom(), now);
							}
						}
						else if (existingValidTo.before(record.getValidTo())) {
							// Our version starts after the existing, but ends
							// later.
							if (dataEquals) {
								// If necesary, increase validto on existing
								// entity to our validTo.
								if (table.getCurrentRowValidTo().before(record.getValidTo())) table.updateValidToOnCurrentRow(record.getValidTo(), now);
								// No need to insert our version as the range is
								// covered by existing version
								insertVersion = false;
							}
							else {
								// Our version starts after the existing, but
								// ends at the same time.
								// Set validto in existing entity to our
								// validfrom.
								table.updateValidToOnCurrentRow(record.getValidFrom(), now);
							}

						}
						else {
							// Our version is newer. Same validTo
							if (dataEquals) {
								// do nothing
								insertVersion = false;
							}
							else {
								// invalidate the existing.
								table.updateValidToOnCurrentRow(record.getValidFrom(), now);
							}

						}
					}
					else if (existingValidFrom.after(record.getValidFrom())) {
						// Our version is older as that the existing one
						if (record.getValidTo().after((existingValidTo))) {
							// Our version encompases the entire existing
							// version,
							if (dataEquals) {
								// reuse the existing version
								table.updateValidFromOnCurrentRow(record.getValidFrom(), now);
								table.updateValidToOnCurrentRow(record.getValidTo(), now);
							}
							else {
								// The existing must be deleted
								// Delete existing row
								table.updateRow(record, now, existingValidFrom, existingValidTo);
							}
							insertVersion = false;
						}
						else if (record.getValidTo().before((existingValidTo))) {
							// Our version starts before the existing, but also
							// ends before.
							if (dataEquals) {
								// Set validfrom in existing entity to our
								// validfrom.
								table.updateValidFromOnCurrentRow(record.getValidFrom(), now);
								insertVersion = false;
							}
							else {
								// Set validfrom in existing entity to our
								// validto.
								table.updateValidFromOnCurrentRow(record.getValidTo(), now);
							}
						}
						else {
							// Our version starts before the existing, and ends
							// at the same time
							table.updateRow(record, now, existingValidFrom, existingValidTo);
							insertVersion = false;
						}

					}
					else {
						// Our version is as old as the existing one
						if (record.getValidTo().after((existingValidTo))) {
							// Our version has the same validfrom but later
							// validto as the existing.
							table.updateValidToOnCurrentRow(record.getValidTo(), now);
							insertVersion = false;
						}
						else if (record.getValidTo().before((existingValidTo))) {
							// Our version has the same validfrom but earlier
							// validto as the existing.
							if (dataEquals) {
								table.updateValidToOnCurrentRow(record.getValidTo(), now);
								insertVersion = false;
							}
							else {
								table.updateValidFromOnCurrentRow(record.getValidTo(), now);
							}
						}
						else {
							// Our version has the same validfrom and validto as
							// the existing.
							if (!dataEquals) {
								// replace the existing
								table.updateRow(record, now, existingValidFrom, existingValidTo);
							}
							insertVersion = false;
						}

					}
				} while (table.nextRow());
				if (insertVersion) table.insertAndUpdateRow(record, now);
			}
		}
		logger.debug("...persistDeltaDataset complete. " + processedEntities + " processed, " + table.getInsertedRows() + " inserted, " + table.getUpdatedRecords() + " updated, " + table.getDeletedRecords() + " deleted");

	}

	public <T extends StamdataEntity> DatabaseTableWrapper<T> getTable(Class<T> clazz) throws FilePersistException {

		return new DatabaseTableWrapper<T>(connection, clazz);
	}

	/**
	 * @param dataset
	 * @throws FilePersistException
	 */
	private <T extends StamdataEntity> void updateValidToOnRecordsNotInDataset(CompleteDataset<T> dataset) throws FilePersistException {

		logger.debug("updateValidToOnRecordsNotInDataset " + dataset.getEntityTypeDisplayName() + " starting...");

		Calendar now = Calendar.getInstance();
		DatabaseTableWrapper<T> table = getTable(dataset.getType());

		List<StamdataEntityVersion> evs = table.getEntityVersions(dataset.getValidFrom(), dataset.getValidTo());

		int nExisting = 0;

		for (StamdataEntityVersion ev : evs) {

			List<? extends StamdataEntity> entitiesWithId = dataset.getEntitiesById(ev.id);

			boolean recordFoundInCompleteDataset = entitiesWithId != null && entitiesWithId.size() > 0;

			if (!recordFoundInCompleteDataset) table.updateValidToOnEntityVersion(dataset.getValidFrom(), ev, now);

			if (++nExisting % 10000 == 0) {
				logger.debug("Processed " + nExisting + " existing records of type " + dataset.getEntityTypeDisplayName());
			}
		}

		logger.debug("...updateValidToOnRecordsNotInDataset " + dataset.getEntityTypeDisplayName() + " complete. Updated: " + table.getUpdatedRecords() + " records");
	}

	public Connection getConnection() {

		return connection;
	}
}
