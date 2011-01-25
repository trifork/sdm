package com.trifork.sdm.importer.importers.takst;


import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.*;

import org.junit.Test;
import org.slf4j.Logger;

import com.trifork.sdm.importer.TestHelper;
import com.trifork.stamdata.importer.jobs.FileParseException;
import com.trifork.stamdata.importer.jobs.takst.TakstParser;
import com.trifork.stamdata.importer.jobs.takst.factories.LaegemiddelFactory;
import com.trifork.stamdata.registre.takst.*;


public class TakstParserTest
{

	public static final String TAKST_TESTDATA_DIR = "testdata/takst";


	@Test
	public void testLMS01() throws Exception
	{
		/*
		// Arrange
		String line = "28100009555SPLMKEMADR00100065Kemadrin                      tabletter           TAB           5 mg                0000005000MG 059300059300 N04AA04OR               D                ";

		// Act
		Laegemiddel drug = LaegemiddelFactory.parse(line);

		// Assert
		List<Laegemiddel> drugs = new ArrayList<Laegemiddel>();
		drugs.add(drug);

		Date now = new Date();

		Takst takst = new Takst(now, now);
		TakstDataset<Laegemiddel> ds = new TakstDataset<Laegemiddel>(takst, drugs, Laegemiddel.class);
		takst.addDataset(ds);

		drug = drugs.get(0);
		assertEquals("Kemadrin", drug.getNavn());
		assertEquals(new Long(28100009555l), drug.getDrugid());
		assertEquals(new Double(5.0), drug.getStyrkeNumerisk());
		assertEquals("5 mg", drug.getStyrkeKlarTekst());
		assertEquals(new Integer(1), drug.getEgnetTilDosisdispensering());
		assertEquals("N04AA04", drug.getATC());
		assertEquals("TAB", drug.getFormKode());
		assertEquals("tabletter", drug.getLaegemiddelformTekst());
		assertEquals("OR", drug.getAdministrationsvejKode());
		assertEquals("KEMADR001", drug.getAlfabetSekvensplads());
		assertEquals(null, drug.getDatoForAfregistrAfLaegemiddel());
		assertEquals(null, drug.getKodeForYderligereFormOplysn());
		assertEquals(null, drug.getLaegemidletsSubstitutionsgruppe());
		assertEquals(new Long(59300), drug.getMTIndehaverKode());
		assertEquals(new Long(59300), drug.getRepraesentantDistributoerKode());
		assertEquals(new Long(65), drug.getSpecNummer());
		assertEquals(null, drug.getSubstitution());
		assertEquals(false, drug.getTrafikadvarsel());
		assertEquals("LM", drug.getVaredeltype());
		assertEquals("SP", drug.getVaretype());
		*/
	}


	@Test
	public void testLMS01_2() throws Exception
	{
		/*
		// Arrange
		String line = "28100110949SPLMXYLOCA00200985Xylocain                      inj.væske, opløsningINJVSKO       10 mg/ml            0000010000MGM056100056100 N01BB02EDIRIVPE                          ";

		// Act
		Laegemiddel drug = LaegemiddelFactory.parse(line);

		// Assert
		List<Laegemiddel> drugs = new ArrayList<Laegemiddel>();
		drugs.add(drug);

		Date now = new Date();
		Takst takst = new Takst(now, now);

		TakstDataset<Laegemiddel> ds = new TakstDataset<Laegemiddel>(takst, drugs, Laegemiddel.class);
		takst.addDataset(ds);

		drug = drugs.get(0);
		assertEquals("Xylocain", drug.getNavn());
		assertEquals(new Long(28100110949l), drug.getDrugid());
		assertEquals(new Double(10), drug.getStyrkeNumerisk());
		assertEquals("10 mg/ml", drug.getStyrkeKlarTekst());
		assertEquals(new Integer(0), drug.getEgnetTilDosisdispensering());
		assertEquals("N01BB02", drug.getATC());
		assertEquals("INJVSKO", drug.getFormKode());
		assertEquals("EDIRIVPE", drug.getAdministrationsvejKode());
		assertEquals("inj.væske, opløsning", drug.getLaegemiddelformTekst());
		*/
	}


	@Test
	public void getDateFromLineTest() throws Exception
	{
		/*
		// Arrange
		TakstParser takstParser = new TakstParser();

		// Act
		Date date = takstParser.getEffectuationDate("0012.0 LMS-TAKST                               20090713                0131LMS.ZIP     200929");
		Calendar c = Calendar.getInstance();
		c.setTime(date);

		// Assert
		assertEquals(2009, c.get(Calendar.YEAR));
		assertEquals(6, c.get(Calendar.MONTH)); // 0 indexed
		assertEquals(13, c.get(Calendar.DAY_OF_MONTH));
		*/
	}


	@Test
	public void exceptionHandlingTestEmptyInput() throws Exception
	{
		/*
		// Arrange. Try parsing with empty list of files
		List<File> files = new ArrayList<File>();
		TakstParser takstParser = new TakstParser();

		try
		{
			// Act
			takstParser.parseTakst(files);
			
			// Assert
			fail("No exceptions thrown");
		}
		catch (Exception e)
		{
			assertEquals(FileParseException.class, e.getClass());
		}
		*/
	}


	@Test
	public void exceptionHandlingRequiredFilesMissing() throws Exception
	{
		/*
		// Arrange. Try parsing with empty list of files
		TakstParser takstParser = new TakstParser();
		String dir = TAKST_TESTDATA_DIR + "/incomplete/";

		List<File> files = Arrays.asList(TestHelper.getFile(dir).listFiles());

		try
		{
			// Act
			takstParser.parseTakst(files);
			// Assert
			fail("No exceptions thrown");
		}
		catch (Exception e)
		{
			assertEquals(FileParseException.class, e.getClass());
		}
		*/
	}


	@Test
	public void exceptionHandlingUnparsable() throws Exception
	{
		/*
		// Arrange. Try parsing with empty list of files

		TakstParser takstParser = new TakstParser();
		String dir = TAKST_TESTDATA_DIR + "/unparsable/";

		List<File> files = Arrays.asList(TestHelper.getFile(dir).listFiles());

		try
		{
			// Act
			takstParser.parseTakst(files);
			// Assert
			fail("No exceptions thrown");
		}
		catch (Exception e)
		{
			assertEquals(FileParseException.class, e.getClass());
		}
		*/
	}


	@Test
	public void testWarningWrongVersion() throws Exception
	{
		/*
		// Arrange. Try parsing a system file with unkown version number
		TakstParser takstParser = new TakstParser();
		String dir = TAKST_TESTDATA_DIR + "/unknown_version/";
		List<File> files = Arrays.asList(TestHelper.getFile(dir).listFiles());
		TakstParser.logger = mock(Logger.class); // Mock the logger to verify
													// that a warning is logged

		// Act
		takstParser.parseTakst(files);

		// Assert
		verify(TakstParser.logger).warn(contains("version"));
		*/
	}


	@Test
	public void testVetFiltering()
	{
		/*
		// setup some objects
		ATCKoderOgTekst atcVet = new ATCKoderOgTekst();
		atcVet.setATCNiveau1("QQ"); // starter med Q = til dyr
		ATCKoderOgTekst atcHum = new ATCKoderOgTekst();
		atcHum.setATCNiveau1("AB"); // ikke starter med Q = til mennesker

		Laegemiddel lmVet = new Laegemiddel();
		lmVet.setDrugid(1l);
		lmVet.setATC("QQ"); // starter med Q = til dyr
		Pakning pakVet = new Pakning();
		pakVet.setVarenummer(1l);
		pakVet.setDrugid(1l);
		Laegemiddel lmHum = new Laegemiddel();
		lmHum.setDrugid(2l);
		lmHum.setATC("AB"); // ikke starter med Q = til mennesker
		Pakning pakHuman = new Pakning();
		pakHuman.setVarenummer(2l);
		pakHuman.setDrugid(2l);

		// put the objects into arrays
		ArrayList<ATCKoderOgTekst> atcKoder = new ArrayList<ATCKoderOgTekst>();
		atcKoder.add(atcHum);
		atcKoder.add(atcVet);
		ArrayList<Pakning> pakninger = new ArrayList<Pakning>();
		pakninger.add(pakVet);
		pakninger.add(pakHuman);
		ArrayList<Laegemiddel> laegemidler = new ArrayList<Laegemiddel>();
		laegemidler.add(lmHum);
		laegemidler.add(lmVet);

		// Add the arrays to the takst as datasets
		Date date = new Date();
		Takst takst = new Takst(date, date);
		TakstDataset<Laegemiddel> lmr = new TakstDataset<Laegemiddel>(takst, laegemidler, Laegemiddel.class);
		TakstDataset<Pakning> pkr = new TakstDataset<Pakning>(takst, pakninger, Pakning.class);
		TakstDataset<ATCKoderOgTekst> atcr = new TakstDataset<ATCKoderOgTekst>(takst, atcKoder, ATCKoderOgTekst.class);
		takst.addDataset(lmr);
		takst.addDataset(pkr);
		takst.addDataset(atcr);

		// check that all is behaving as expected before filtering
		assertFalse(pakVet.isTilHumanAnvendelse());
		assertTrue(pakHuman.isTilHumanAnvendelse());
		assertFalse(atcVet.isTilHumanAnvendelse());
		assertTrue(atcHum.isTilHumanAnvendelse());
		assertNotNull(takst.getEntity(Pakning.class, pakVet.getKey()));
		assertNotNull(takst.getEntity(Pakning.class, pakHuman.getKey()));
		assertNotNull(takst.getEntity(Laegemiddel.class, lmVet.getKey()));
		assertNotNull(takst.getEntity(Laegemiddel.class, lmHum.getKey()));
		assertNotNull(takst.getEntity(ATCKoderOgTekst.class, atcVet.getKey()));
		assertNotNull(takst.getEntity(ATCKoderOgTekst.class, atcHum.getKey()));

		// Filter the takst
		TakstParser.filterOutVetDrugs(takst);

		// Check that the correct entities were removed from takst
		assertNull(takst.getEntity(Pakning.class, pakVet.getKey()));
		assertNotNull(takst.getEntity(Pakning.class, pakHuman.getKey()));
		assertNull(takst.getEntity(Laegemiddel.class, lmVet.getKey()));
		assertNotNull(takst.getEntity(Laegemiddel.class, lmHum.getKey()));
		assertNull(takst.getEntity(ATCKoderOgTekst.class, atcVet.getKey()));
		assertNotNull(takst.getEntity(ATCKoderOgTekst.class, atcHum.getKey()));
		*/
	}
}
