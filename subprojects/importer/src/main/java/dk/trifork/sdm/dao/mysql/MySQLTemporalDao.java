package dk.trifork.sdm.dao.mysql;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

import dk.trifork.sdm.dao.StamdataVersionedDao;
import dk.trifork.sdm.dao.mysql.MySQLTemporalTable.StamdataEntityVersion;
import dk.trifork.sdm.importer.exceptions.FilePersistException;
import dk.trifork.sdm.model.CompleteDataset;
import dk.trifork.sdm.model.Dataset;
import dk.trifork.sdm.model.Output;
import dk.trifork.sdm.model.StamdataEntity;


public class MySQLTemporalDao implements StamdataVersionedDao {

	private static Logger logger = Logger.getLogger(MySQLTemporalDao.class);

	protected Connection con;

	public MySQLTemporalDao(Connection con) {

		this.con = con;
	}

	public void persist(CompleteDataset<? extends StamdataEntity>... datasets) throws FilePersistException {

		persistCompleteDatasets(Arrays.asList(datasets));
	}

	@Override
	public void persistCompleteDatasets(List<CompleteDataset<? extends StamdataEntity>> datasets) throws FilePersistException {

		logger.debug("Starting to put entities from datasetgroup");

		for (CompleteDataset<? extends StamdataEntity> dataset : datasets) {
			persistCompleteDataset(dataset);
		}

		logger.debug("Done putting entities from datasetgroup");
	}

	public void persistCompleteDataset(CompleteDataset<? extends StamdataEntity> dataset) throws FilePersistException {

		if (!dataset.getType().isAnnotationPresent(Output.class)) return;

		updateValidToOnRecordsNotInDataset(dataset);
		persistDeltaDataset(dataset);
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
	public void persistDeltaDataset(Dataset<? extends StamdataEntity> dataset) throws FilePersistException {

		Calendar now = Calendar.getInstance();
		MySQLTemporalTable table = getTable(dataset.getType());
		logger.debug("persistDeltaDataset dataset: " + dataset.getEntityTypeDisplayName() + " with: " + dataset.getEntities().size() + " entities...");

		int processedEntities = 0;

		for (StamdataEntity sde : dataset.getEntities()) {
			
			processedEntities++;
			
			Calendar validFrom = sde.getValidFrom();
			
			boolean exists = table.fetchEntityVersions(sde.getKey(), validFrom, sde.getValidTo());
			
			if (!exists) {
				// Entity was not found, so create it
				table.insertRow(sde, now);
			}
			else {
				// At least one version was found in the same validity range.
				boolean insertVersion = true;
				do {
					Calendar existingValidFrom = table.getCurrentRowValidFrom();
					Calendar existingValidTo = table.getCurrentRowValidTo();
					boolean dataEquals = table.dataInCurrentRowEquals(sde);
					if (existingValidFrom.before(sde.getValidFrom())) {
						if (existingValidTo.equals(sde.getValidFrom())) {
							// This existing row is not in the range of our
							// entity
							continue;
						}
						// our entity is newer.
						if (existingValidTo.after(sde.getValidTo())) {
							// Our version is inside the existing version,
							if (!dataEquals) {
								// The existing version must be split in two.
								// Copy existing row. Set validfrom in copy
								// entity to our validto.
								table.copyCurrentRowButWithChangedValidFrom(sde.getValidTo(), now);
								// Set validto in existing entity to our
								// validfrom.
								table.updateValidToOnCurrentRow(sde.getValidFrom(), now);
							}
						}
						else if (existingValidTo.before(sde.getValidTo())) {
							// Our version starts after the existing, but ends
							// later.
							if (dataEquals) {
								// If necesary, increase validto on existing
								// entity to our validTo.
								if (table.getCurrentRowValidTo().before(sde.getValidTo())) table.updateValidToOnCurrentRow(sde.getValidTo(), now);
								// No need to insert our version as the range is
								// covered by existing version
								insertVersion = false;
							}
							else {
								// Our version starts after the existing, but
								// ends at the same time.
								// Set validto in existing entity to our
								// validfrom.
								table.updateValidToOnCurrentRow(sde.getValidFrom(), now);
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
								table.updateValidToOnCurrentRow(sde.getValidFrom(), now);
							}

						}
					}
					else if (existingValidFrom.after(sde.getValidFrom())) {
						// Our version is older as that the existing one
						if (sde.getValidTo().after((existingValidTo))) {
							// Our version encompases the entire existing
							// version,
							if (dataEquals) {
								// reuse the existing version
								table.updateValidFromOnCurrentRow(sde.getValidFrom(), now);
								table.updateValidToOnCurrentRow(sde.getValidTo(), now);
							}
							else {
								// The existing must be deleted
								// Delete existing row
								table.updateRow(sde, now, existingValidFrom, existingValidTo);
							}
							insertVersion = false;
						}
						else if (sde.getValidTo().before((existingValidTo))) {
							// Our version starts before the existing, but also
							// ends before.
							if (dataEquals) {
								// Set validfrom in existing entity to our
								// validfrom.
								table.updateValidFromOnCurrentRow(sde.getValidFrom(), now);
								insertVersion = false;
							}
							else {
								// Set validfrom in existing entity to our
								// validto.
								table.updateValidFromOnCurrentRow(sde.getValidTo(), now);
							}
						}
						else {
							// Our version starts before the existing, and ends
							// at the same time
							table.updateRow(sde, now, existingValidFrom, existingValidTo);
							insertVersion = false;
						}

					}
					else {
						// Our version is as old as the existing one
						if (sde.getValidTo().after((existingValidTo))) {
							// Our version has the same validfrom but later
							// validto as the existing.
							table.updateValidToOnCurrentRow(sde.getValidTo(), now);
							insertVersion = false;
						}
						else if (sde.getValidTo().before((existingValidTo))) {
							// Our version has the same validfrom but earlier
							// validto as the existing.
							if (dataEquals) {
								table.updateValidToOnCurrentRow(sde.getValidTo(), now);
								insertVersion = false;
							}
							else {
								table.updateValidFromOnCurrentRow(sde.getValidTo(), now);
							}
						}
						else {
							// Our version has the same validfrom and validto as
							// the existing.
							if (!dataEquals) {
								// replace the existing
								table.updateRow(sde, now, existingValidFrom, existingValidTo);
							}
							insertVersion = false;
						}

					}
				} while (table.nextRow());
				if (insertVersion) table.insertAndUpdateRow(sde, now);
			}
		}
		logger.debug("...persistDeltaDataset complete. " + processedEntities + " processed, " + table.getInsertedRows() + " inserted, " + table.getUpdatedRecords() + " updated, " + table.getDeletedRecords() + " deleted");

	}

	public <T extends StamdataEntity> MySQLTemporalTable<T> getTable(Class<T> clazz) throws FilePersistException {

		return new MySQLTemporalTable<T>(con, clazz);
	}

	/**
	 * @param dataset
	 * @throws FilePersistException
	 */
	private <T extends StamdataEntity> void updateValidToOnRecordsNotInDataset(CompleteDataset<T> dataset) throws FilePersistException {

		logger.debug("updateValidToOnRecordsNotInDataset " + dataset.getEntityTypeDisplayName() + " starting...");
		Calendar now = Calendar.getInstance();
		MySQLTemporalTable table = getTable(dataset.getType());
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

		return this.con;
	}

	/*
	 * public StamdataEntity loadStamdataEntities(String querySql, Class<?
	 * extends StamdataEntity> clazz) throws Exception{ Statement st =
	 * con.createStatement(); ResultSet rs = st.executeQuery(querySql); while
	 * (rs.next()){ StamdataEntity instance = clazz.newInstance(); List<Method>
	 * outputMethods = getOutputMethods(clazz); }
	 * 
	 * }
	 */
}
