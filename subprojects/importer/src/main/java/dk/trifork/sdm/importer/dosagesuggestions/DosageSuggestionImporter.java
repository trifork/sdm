package dk.trifork.sdm.importer.dosagesuggestions;

import static dk.trifork.sdm.util.DateUtils.FUTURE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import dk.trifork.sdm.config.MySQLConnectionManager;
import dk.trifork.sdm.dao.mysql.MySQLTemporalDao;
import dk.trifork.sdm.importer.FileImporter;
import dk.trifork.sdm.importer.dosagesuggestions.models.DosageStructure;
import dk.trifork.sdm.importer.dosagesuggestions.models.DosageUnit;
import dk.trifork.sdm.importer.dosagesuggestions.models.DosageVersion;
import dk.trifork.sdm.importer.dosagesuggestions.models.Drug;
import dk.trifork.sdm.importer.dosagesuggestions.models.DrugDosageStructureRelation;
import dk.trifork.sdm.importer.exceptions.FileImporterException;
import dk.trifork.sdm.model.CompleteDataset;
import dk.trifork.sdm.model.StamdataEntity;


/**
 * Importer for drug dosage suggestions data.
 * 
 * @author Thomas Børlum (thb@trifork.com)
 */
public class DosageSuggestionImporter implements FileImporter {

	public DosageSuggestionImporter() {

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

			// OTHER FILES
			//
			// There are data files and relation file.
			// Relation files act as one-to-one etc. relations.
			//
			// This data source represents the 'whole truth' for
			// the validity period. That means that complete
			// datasets will be used for persisting, and no existing
			// records will be valid in the period.

			CompleteDataset<?> drugs = parseDataFile(getFile(files, "Drugs.json"), "drugs", version, Drug.class);
			CompleteDataset<?> units = parseDataFile(getFile(files, "DosageUnit.json"), "dosageUnits", version, DosageUnit.class);
			CompleteDataset<?> structures = parseDataFile(getFile(files, "Drugs.json"), "drugStructures", version, DosageStructure.class);
			CompleteDataset<?> relations = parseDataFile(getFile(files, "Drugs.json"), "drugsDosageStructures", version, DrugDosageStructureRelation.class);

			// PERSIST THE DATA

			connection = MySQLConnectionManager.getConnection();
			new MySQLTemporalDao(connection).persist(versionDataset, drugs, structures, units, relations);
			connection.commit();
		}
		catch (Exception e) {

			// TODO (thb): Should the transaction not be rolled back for all
			// these situations?

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

		return (DosageVersion) versions.get("version");
	}

	/**
	 * Parses other data files.
	 */
	private <T extends StamdataEntity> CompleteDataset<?> parseDataFile(File file, String root, DosageVersion version, Class<T> type) throws FileNotFoundException {

		Reader reader = new InputStreamReader(new FileInputStream(file));

		Type containerType = new TypeToken<Map<String, T>>() {}.getType();
		Map<String, Collection<T>> parsedData = new Gson().fromJson(reader, containerType);

		CompleteDataset<T> dataset = new CompleteDataset<T>(type, version.getValidFrom(), version.getValidTo());

		for (T structure : parsedData.get(root)) {
			dataset.addEntity(structure);
		}

		return dataset;
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
		present &= getFile(files, "Version.json") != null;

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
	
	// TODO: The current design is not test friendly. This means
	// we have to set the dependencies with setters to override
	// the default behaviour. The design should be updated.
	
	void setPersister() {
		
	}
}
