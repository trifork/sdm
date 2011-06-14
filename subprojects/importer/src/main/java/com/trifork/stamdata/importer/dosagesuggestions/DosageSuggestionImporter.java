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

package com.trifork.stamdata.importer.dosagesuggestions;

import static com.trifork.stamdata.util.DateUtils.FUTURE;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.trifork.stamdata.config.MySQLConnectionManager;
import com.trifork.stamdata.importer.FileImporter;
import com.trifork.stamdata.importer.dosagesuggestions.models.DosageRecord;
import com.trifork.stamdata.importer.dosagesuggestions.models.DosageStructure;
import com.trifork.stamdata.importer.dosagesuggestions.models.DosageUnit;
import com.trifork.stamdata.importer.dosagesuggestions.models.DosageVersion;
import com.trifork.stamdata.importer.dosagesuggestions.models.Drug;
import com.trifork.stamdata.importer.dosagesuggestions.models.DrugDosageStructureRelation;
import com.trifork.stamdata.importer.exceptions.FileImporterException;
import com.trifork.stamdata.model.CompleteDataset;
import com.trifork.stamdata.model.StamdataEntity;
import com.trifork.stamdata.persistence.AuditingPersister;
import com.trifork.stamdata.persistence.Persister;



/**
 * Importer for drug dosage suggestions data.
 *
 * @author Thomas Børlum (thb@trifork.com)
 */
public class DosageSuggestionImporter implements FileImporter {

	private static final Logger logger = getLogger(DosageSuggestionImporter.class);

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

			// CHECK PREVIOUS VERSION
			//
			// Check that the version in imported in
			// sequence and that we haven't missed one.

			connection = MySQLConnectionManager.getConnection();

			Statement versionStatement = connection.createStatement();
			ResultSet queryResults = versionStatement.executeQuery("SELECT MAX(releaseNumber) FROM DosageVersion");

			if (!queryResults.next()) {
				throw new FileImporterException("SQL statement returned no rows, expected NULL or int value.");
			}

			int maxVersion = queryResults.getInt(1);

			if (queryResults.wasNull()) {
				logger.warn("No previous version of Dosage Suggestion registry found, assuming initial import.");
			}
			else if (version.getReleaseNumber() != maxVersion + 1) {
				throw new FileImporterException("The Dosage Suggestion files are out of sequence! Expected " + (maxVersion + 1) + ", but was " + version.getReleaseNumber() + ".");
			}

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

			Persister persister = (mockPersister != null) ? mockPersister : new AuditingPersister(connection);
			persister.persistCompleteDataset(versionDataset, drugs, structures, units, relations);

			connection.commit();

			logger.info("Dosage Suggestion Registry v" + version.getReleaseNumber() + " was successfully imported.");
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
