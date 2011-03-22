package dk.trifork.sdm.importer.doseringsforslag;

import static java.util.Arrays.asList;
import static org.apache.commons.io.FileUtils.toFile;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Generated;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import dk.trifork.sdm.importer.dosagesuggestions.DosageSuggestionImporter;
import dk.trifork.sdm.importer.dosagesuggestions.models.DosageStructure;
import dk.trifork.sdm.importer.dosagesuggestions.models.DosageUnit;
import dk.trifork.sdm.importer.dosagesuggestions.models.DosageVersion;
import dk.trifork.sdm.importer.exceptions.FileImporterException;
import dk.trifork.sdm.model.CompleteDataset;


/**
 * Tests that the dosage suggestion importer works as expected.
 * 
 * @author Thomas Børlum (thb@trifork.com)
 */
@Ignore
public class DoseringsforslagImporterTest {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private DosageSuggestionImporter importer;

	private DosageVersion version;

	private File versionFile;
	private File drugsFile;
	private File dosageStructureFile;
	private File unitsFile;
	private File relationFile;

	@Before
	public void setUp() {

		importer = new DosageSuggestionImporter();
		version = mock(DosageVersion.class);

		// The 'single' files only contain one record each.
		// This makes it easy to know that is imported and
		// it is alot faster.
		//
		// The other files contain several records and are
		// used to count the number of records importet.
		
		versionFile = getFile("single/Version.json");
		drugsFile = getFile("single/Drugs.json");
		dosageStructureFile = getFile("single/DosageStructures.json");
		unitsFile = getFile("single/DosageUnits.json");
		relationFile = getFile("single/DrugsDosageStructures.json");
	}

	@Test
	public void Should_import_the_version_file_correctly() throws Exception {

		runImporter();

		assertThat(version.getReleaseNumber(), equalTo(125L));
		assertThat(version.getReleaseDate(), equalTo(date("2011-02-15")));
		assertThat(version.getLmsDate(), equalTo(date("2011-02-02")));
		assertThat(version.getDaDate(), equalTo(date("2011-01-24")));
	}

	@Test
	public void Should_import_all_dosage_structures() throws Exception {

		dosageStructureFile = getFile("multiple/DosageStructures.json");
		
		runImporter();

		assertThat(dosageStructures.size(), equalTo(587));
	}

	@Test
	public void Should_import_the_structures_correctly() throws Exception {

		runImporter();

		DosageStructure structure = dosageStructures.getEntities().iterator().next();

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

		runImporter();
	
		assertThat(units.size(), equalTo(21));
	}

	@Test
	public void Should_import_dosage_units_correctly() throws Exception {

		runImporter();
		
		/*
		 * assertThat(unit.getReleaseNumber(), equalTo(125L));
		 * assertThat(unit.getCode(), equalTo(8));
		 * assertThat(unit.getTextSingular(), equalTo("brusetablet"));
		 * assertThat(unit.getTextPlural(), equalTo("brusetabletter"));
		 */
	}

	@Test
	public void Should_import_all_drugs() throws Exception {

		runImporter();
	}
	
	// HELPER METHODS

	private Date date(String dateString) throws Exception {

		return dateFormat.parse(dateString);
	}
	
	public File getFile(String filename) {

		return toFile(getClass().getClassLoader().getResource("data/doseringsforslag/" + filename));
	}

	public void runImporter() throws Exception {

		List<File> files = asList(versionFile, drugsFile, dosageStructureFile, unitsFile, relationFile);
		importer.run(files);
	}
}
