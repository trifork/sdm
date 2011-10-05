/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */


package com.trifork.stamdata.importer.jobs.takst;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.trifork.stamdata.importer.jobs.takst.model.ATCKoderOgTekst;
import com.trifork.stamdata.importer.jobs.takst.model.Laegemiddel;
import com.trifork.stamdata.importer.jobs.takst.model.LaegemiddelFactory;
import com.trifork.stamdata.importer.jobs.takst.model.Pakning;


public class TakstParserTest
{
	@Rule
	public TemporaryFolder temp = new TemporaryFolder();

	private File createFileWithContent(FixedLengthParserConfiguration<?> config, String content) throws Exception
	{
		File file = temp.newFile(config.getFilename());
		FileUtils.writeStringToFile(file, content, FixedLengthFileParser.FILE_ENCODING);
		return file;
	}

	@Test
	public void testLMS01() throws Exception
	{
		LaegemiddelFactory config = new LaegemiddelFactory();
		File file = createFileWithContent(config, "28100009555SPLMKEMADR00100065Kemadrin                      tabletter           TAB           5 mg                0000005000MG 059300059300 N04AA04OR               D                ");
		List<Laegemiddel> drugs = new FixedLengthFileParser(new File[] {file}).parse(config, Laegemiddel.class);

		Date now = new Date();
		Takst takst = new Takst(now, now);
		TakstDataset<Laegemiddel> ds = new TakstDataset<Laegemiddel>(takst, drugs, Laegemiddel.class);
		takst.addDataset(ds);

		Laegemiddel drug = drugs.get(0);
		assertEquals("Kemadrin", drug.getNavn());
		assertEquals(new Long(28100009555l), drug.getDrugid());
		assertEquals(new Double(5.0), drug.getStyrkeNumerisk());
		assertEquals("5 mg", drug.getStyrkeKlarTekst());
		assertTrue(drug.getEgnetTilDosisdispensering());
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
	}

	@Test
	public void should_parse_takst_drug_version_2() throws Exception
	{
		LaegemiddelFactory config = new LaegemiddelFactory();
		File file = createFileWithContent(config, "28100110949SPLMXYLOCA00200985Xylocain                      inj.væske, opløsningINJVSKO       10 mg/ml            0000010000MGM056100056100 N01BB02EDIRIVPE                          ");
		List<Laegemiddel> drugs = new FixedLengthFileParser(new File[] {file}).parse(config, Laegemiddel.class);

		// Assert
		
		Date now = new Date();
		Takst takst = new Takst(now, now);
		TakstDataset<Laegemiddel> ds = new TakstDataset<Laegemiddel>(takst, drugs, Laegemiddel.class);
		takst.addDataset(ds);

		Laegemiddel drug = drugs.get(0);
		assertEquals("Xylocain", drug.getNavn());
		assertEquals(new Long(28100110949l), drug.getDrugid());
		assertEquals(new Double(10), drug.getStyrkeNumerisk());
		assertEquals("10 mg/ml", drug.getStyrkeKlarTekst());
		assertFalse(drug.getEgnetTilDosisdispensering());
		assertEquals("N01BB02", drug.getATC());
		assertEquals("INJVSKO", drug.getFormKode());
		assertEquals("EDIRIVPE", drug.getAdministrationsvejKode());
		assertEquals("inj.væske, opløsning", drug.getLaegemiddelformTekst());
	}

	@Test
	public void getDateFromLineTest() throws Exception
	{
		TakstParser takstParser = new TakstParser();

		Date date = takstParser.getValidFromDate("0012.0 LMS-TAKST                               20090713                0131LMS.ZIP     200929");

		DateTime parsedDate = new DateTime(date);

		assertEquals(2009, parsedDate.getYear());
		assertEquals(7, parsedDate.getMonthOfYear());
		assertEquals(13, parsedDate.getDayOfMonth());
	}

	@Test(expected = Exception.class)
	public void Should_complain_if_no_file_are_present() throws Exception
	{
		// Try parsing with empty list of files.
		
		new TakstParser().parseFiles(new File[] {});
	}

	@Test(expected = Exception.class)
	public void exceptionHandlingRequiredFilesMissing() throws Exception
	{
		// Try parsing with empty list of files.

		TakstParser takstParser = new TakstParser();
		File dir = FileUtils.toFile(getClass().getClassLoader().getResource("data/takst/incomplete/"));

		takstParser.parseFiles(dir.listFiles());
	}

	@Test(expected = Exception.class)
	public void exceptionHandlingUnparsable() throws Exception
	{
		// Arrange. Try parsing with empty list of files
		TakstParser takstParser = new TakstParser();
		File dir = FileUtils.toFile(getClass().getClassLoader().getResource("data/takst/unparsable/"));
		takstParser.parseFiles(dir.listFiles());
	}

	@Test
	public void testVetFiltering()
	{
		// setup some objects
		ATCKoderOgTekst atcVet = new ATCKoderOgTekst();
		atcVet.setATC("QQ"); // starter med Q = til dyr
		ATCKoderOgTekst atcHum = new ATCKoderOgTekst();
		atcHum.setATC("AB"); // ikke starter med Q = til mennesker

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
		Takst takst = new Takst(new Date(), new Date());
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
	}
}
