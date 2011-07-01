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

package com.trifork.stamdata.importer.parsers.doseringsforslag;

import static java.util.Arrays.asList;
import static org.apache.commons.io.FileUtils.toFile;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.trifork.stamdata.importer.model.CompleteDataset;
import com.trifork.stamdata.importer.model.Dataset;
import com.trifork.stamdata.importer.model.StamdataEntity;
import com.trifork.stamdata.importer.parsers.dosagesuggestions.DosageSuggestionImporter;
import com.trifork.stamdata.importer.parsers.dosagesuggestions.models.DosageStructure;
import com.trifork.stamdata.importer.parsers.dosagesuggestions.models.DosageUnit;
import com.trifork.stamdata.importer.parsers.dosagesuggestions.models.DosageVersion;
import com.trifork.stamdata.importer.parsers.dosagesuggestions.models.Drug;
import com.trifork.stamdata.importer.parsers.exceptions.FilePersistException;
import com.trifork.stamdata.importer.persistence.Persister;



/**
 * Tests that the dosage suggestion importer works as expected.
 *
 * @author Thomas Børlum (thb@trifork.com)
 */
public class DoseringsforslagImporterTest {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private DosageSuggestionImporter importer;

	private File versionFile;
	private File drugsFile;
	private File dosageStructureFile;
	private File unitsFile;
	private File relationFile;

	private MockPersister persister;

	@Before
	public void setUp() {

		// The 'single' files only contain one record each.
		// This makes it easy to know that is imported and
		// it is a lot faster.
		//
		// The other files contain several records and are
		// used to count the number of records importet.

		versionFile = getFile("single/DosageVersion.json");
		drugsFile = getFile("single/Drugs.json");
		dosageStructureFile = getFile("single/DosageStructures.json");
		unitsFile = getFile("single/DosageUnits.json");
		relationFile = getFile("single/DrugsDosageStructures.json");
	}

	@Test
	public void Should_import_the_version_file_correctly() throws Exception {

		runImporter();

		DosageVersion version = getFirst(DosageVersion.class);

		assertThat(version.getReleaseNumber(), equalTo(125L));
		assertThat(version.getReleaseDate(), equalTo(date("2011-02-15")));
		assertThat(version.getLmsDate(), equalTo(date("2011-02-02")));
		assertThat(version.getDaDate(), equalTo(date("2011-01-24")));
	}

	@Test
	public void Should_import_all_dosage_structures() throws Exception {

		dosageStructureFile = getFile("multiple/DosageStructures.json");

		runImporter();

		CompleteDataset<DosageStructure> structures = persister.getDataset(DosageStructure.class);

		assertThat(structures.size(), equalTo(587));
	}

	@Test
	public void Should_import_the_structures_correctly() throws Exception {

		runImporter();

		DosageStructure structure = getFirst(DosageStructure.class);

		assertThat(structure.getReleaseNumber(), equalTo(125L));
		assertThat(structure.getCode(), equalTo(3L));
		assertThat(structure.getType(), equalTo("M+M+A+N"));
		assertThat(structure.getSimpleString(), equalTo("0.5"));
		assertThat(structure.getShortTranslation(), equalTo("1/2 tablet morgen"));
		assertThat(structure.getXml(), equalTo("<b:DosageStructure\n   xsi:schemaLocation=\"http://www.dkma.dk/medicinecard/xml.schema/2009/01/01 DKMA_DosageStructure.xsd\"\n   xmlns:a=\"http://www.dkma.dk/medicinecard/xml.schema/2008/06/01\"\n   xmlns:b=\"http://www.dkma.dk/medicinecard/xml.schema/2009/01/01\"\n   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n   <b:DosageTimesStructure>\n      <a:DosageTimesIterationIntervalQuantity>1</a:DosageTimesIterationIntervalQuantity>\n      <a:DosageTimesStartDate>2000-01-01</a:DosageTimesStartDate>\n      <b:DosageQuantityUnitText>tablet</b:DosageQuantityUnitText>\n      <b:DosageDayElementStructure>\n         <a:DosageDayIdentifier>1</a:DosageDayIdentifier>\n         <b:MorningDosageTimeElementStructure>\n            <a:DosageQuantityValue>0.5</a:DosageQuantityValue>\n         </b:MorningDosageTimeElementStructure>\n      </b:DosageDayElementStructure>\n   </b:DosageTimesStructure>\n</b:DosageStructure>"));
		assertThat(structure.getLongTranslation(), equalTo("Daglig 1/2 tablet morgen"));
		assertThat(structure.getSupplementaryText(), nullValue());
	}

	@Test
	public void Should_import_all_dosage_units() throws Exception {

		unitsFile = getFile("multiple/DosageUnits.json");

		runImporter();

		CompleteDataset<DosageUnit> units = persister.getDataset(DosageUnit.class);

		assertThat(units.size(), equalTo(21));
	}

	@Test
	public void Should_import_dosage_units_correctly() throws Exception {

		runImporter();

		DosageUnit unit = getFirst(DosageUnit.class);

		assertThat(unit.getReleaseNumber(), equalTo(125L));
		assertThat(unit.getCode(), equalTo(8));
		assertThat(unit.getTextSingular(), equalTo("brusetablet"));
		assertThat(unit.getTextPlural(), equalTo("brusetabletter"));
	}

	@Test
	public void Should_import_all_drugs() throws Exception {

		drugsFile = getFile("multiple/Drugs.json");

		runImporter();

		CompleteDataset<?> records = persister.getDataset(Drug.class);

		assertThat(records.size(), equalTo(3710));
	}

	// HELPER METHODS

	private Date date(String dateString) throws Exception {

		return dateFormat.parse(dateString);
	}

	public File getFile(String filename) {

		return toFile(getClass().getClassLoader().getResource("data/doseringsforslag/" + filename));
	}

	public void runImporter() throws Exception {

		persister = new MockPersister();

		importer = new DosageSuggestionImporter(persister);
		List<File> files = asList(versionFile, drugsFile, dosageStructureFile, unitsFile, relationFile);
		importer.run(files);
	}

	public <T extends StamdataEntity> T getFirst(Class<T> type) {

		CompleteDataset<T> structures = persister.getDataset(type);
		return structures.getEntities().iterator().next();
	}


	// HACK: MOCK PERSISTER

	private class MockPersister implements Persister {

		Map<Class<? extends StamdataEntity>, CompleteDataset<? extends StamdataEntity>> results;

		@Override
		public void persistCompleteDataset(CompleteDataset<? extends StamdataEntity>... datasets) throws FilePersistException {

			results = new HashMap<Class<? extends StamdataEntity>, CompleteDataset<? extends StamdataEntity>>();

			for (CompleteDataset<? extends StamdataEntity> dataset : datasets) {
				results.put(dataset.getType(), dataset);
			}
		}

		@Override
		public <T extends StamdataEntity> void persistDeltaDataset(Dataset<T> dataset) throws FilePersistException {

		}

		@SuppressWarnings("unchecked")
		public <T extends StamdataEntity> CompleteDataset<T> getDataset(Class<T> type) {

			return (CompleteDataset<T>) results.get(type);
		}
	}
}