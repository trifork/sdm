package dk.trifork.sdm.importer.dosagesuggestions;

import static dk.trifork.sdm.util.DateUtils.FUTURE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import dk.trifork.sdm.config.MySQLConnectionManager;
import dk.trifork.sdm.dao.AuditingPersister;
import dk.trifork.sdm.dao.Persister;
import dk.trifork.sdm.importer.FileImporter;
import dk.trifork.sdm.importer.dosagesuggestions.models.DosageRecord;
import dk.trifork.sdm.importer.dosagesuggestions.models.DosageStructure;
import dk.trifork.sdm.importer.dosagesuggestions.models.DosageUnit;
import dk.trifork.sdm.importer.dosagesuggestions.models.DosageVersion;
import dk.trifork.sdm.importer.dosagesuggestions.models.Drug;
import dk.trifork.sdm.importer.dosagesuggestions.models.DrugDosageStructureRelation;
import dk.trifork.sdm.importer.exceptions.FileImporterException;
import dk.trifork.sdm.model.AbstractStamdataEntity;
import dk.trifork.sdm.model.CompleteDataset;
import dk.trifork.sdm.model.StamdataEntity;


/**
 * Importer for drug dosage suggestions data.
 * 
 * @author Thomas Børlum (thb@trifork.com)
 */
public class DosageSuggestionImporter implements FileImporter {

	private Persister mockPersister = null;
	
	public DosageSuggestionImporter() {

	}
	
	public DosageSuggestionImporter(Persister mockPersister) {
		
		// HACK: These importers are really not made for
		// testing. We should refactor. (REMOVE THIS CTOR)
		
		this.mockPersister = mockPersister;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void run(List<File> files) throws FileImporterException {

		Connection connection = null;

		try {
			// METADATA FILE
			//
			// The file contains information about the validity period
			// of the data.

			DosageVersion version = parseVersionFile(getFile(files, "DosageVersion.json"));
			CompleteDataset<DosageVersion> versionDataset = new CompleteDataset<DosageVersion>(DosageVersion.class, version.getValidFrom(), FUTURE);
			versionDataset.addEntity(version);
			
			Calendar c = Calendar.getInstance();
			c.setTime(version.getReleaseDate());
			version.setVersion(c);

			// OTHER FILES
			//
			// There are data files and relation file.
			// Relation files act as one-to-one etc. relations.
			//
			// This data source represents the 'whole truth' for
			// the validity period. That means that complete
			// datasets will be used for persisting, and no existing
			// records will be valid in the period.
			//
			// We have to declare the <T> types explicitly since GSon
			// (Java is stupid) can't get the runtime types otherwise.
			
			Type type;
			
			type = new TypeToken<Map<String,Collection<Drug>>>() {}.getType();
			CompleteDataset<?> drugs = parseDataFile(getFile(files, "Drugs.json"), "drugs", version, Drug.class, type);
			setValidityPeriod(drugs, version);
			
			type = new TypeToken<Map<String,Collection<DosageUnit>>>() {}.getType();
			CompleteDataset<?> units = parseDataFile(getFile(files, "DosageUnits.json"), "dosageUnits", version, DosageUnit.class, type);
			setValidityPeriod(units, version);
			
			type = new TypeToken<Map<String,Collection<DosageStructure>>>() {}.getType();
			CompleteDataset<?> structures = parseDataFile(getFile(files, "DosageStructures.json"), "dosageStructures", version, DosageStructure.class, type);
			setValidityPeriod(structures, version);
			
			type = new TypeToken<Map<String,Collection<DrugDosageStructureRelation>>>() {}.getType();
			CompleteDataset<?> relations = parseDataFile(getFile(files, "DrugsDosageStructures.json"), "drugsDosageStructures", version, DrugDosageStructureRelation.class, type);
			setValidityPeriod(relations, version);
			
			// PERSIST THE DATA

			connection = MySQLConnectionManager.getConnection();
			
			Persister persister = (mockPersister != null) ? mockPersister : new AuditingPersister(connection);
			persister.persistCompleteDataset(versionDataset, drugs, structures, units, relations);
			
			connection.commit();
		}
		catch (Exception e) {

			throw new FileImporterException("Error during import of dosage suggestions.", e);
		}
		finally {

			MySQLConnectionManager.close(connection);
		}
	}

	/**
	 * Parses Version.json files.
	 */
	private DosageVersion parseVersionFile(File file) throws FileNotFoundException {

		Reader reader = new InputStreamReader(new FileInputStream(file));

		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		Type type = new TypeToken<Map<String, DosageVersion>>() {}.getType();

		Map<String, DosageVersion> versions = gson.fromJson(reader, type);

		return versions.get("version");
	}

	/**
	 * Parses other data files.
	 */
	private <T extends StamdataEntity> CompleteDataset<T> parseDataFile(File file, String root, DosageVersion version, Class<T> type, Type collectionType) throws FileNotFoundException {

		Reader reader = new InputStreamReader(new FileInputStream(file));

		Map<String, List<T>> parsedData = new Gson().fromJson(reader, collectionType);

		CompleteDataset<T> dataset = new CompleteDataset<T>(type, version.getValidFrom(), version.getValidTo());

		for (T structure : parsedData.get(root)) {
			dataset.addEntity(structure);
		}

		return dataset;
	}
	
	/**
	 * HACK: These dates should be taken from the complete data set.
	 * There is no reason why we set dates on each record.
	 */
	private void setValidityPeriod(CompleteDataset<?> dataset, DosageVersion version) {
		
		CompleteDataset<? extends DosageRecord> records = (CompleteDataset<? extends DosageRecord>)dataset;
		
		for (DosageRecord record : records.getEntities()) {
			record.setVersion(version.getValidFrom());
		}
	}

	@Override
	public boolean checkRequiredFiles(List<File> files) {

		// ALL THE FOLLOWING FILES MUST BE PRESENT
		//
		// The import will fail if not all of the files can be found.

		boolean present = true;

		present &= getFile(files, "DosageStructures.json") != null;
		present &= getFile(files, "DosageUnits.json") != null;
		present &= getFile(files, "Drugs.json") != null;
		present &= getFile(files, "DrugsDosageStructures.json") != null;
		present &= getFile(files, "DosageVersion.json") != null;

		return present;
	}

	/**
	 * Searches the provided file array for a specific file name.
	 * 
	 * @param files the list of files to search.
	 * @param name the file name of the file to return.
	 * 
	 * @return the file with the specified name or null if no file is found.
	 */
	private File getFile(List<File> files, String name) {

		File result = null;

		for (File file : files) {

			if (file.getName().equals(name)) {
				result = file;
				break;
			}
		}

		return result;
	}
}
