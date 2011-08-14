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

package com.trifork.stamdata.importer.jobs.dkma;

import static com.trifork.stamdata.Helpers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

import com.google.common.collect.Lists;
import com.trifork.stamdata.importer.jobs.dkma.model.*;
import com.trifork.stamdata.importer.util.Dates;


public class DKMAParserTest
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
	public void can_parse_system_file() throws Exception
	{
		DKMAParser takstParser = new DKMAParser(FAKE_TIME_GAP);

		Date date = takstParser.getValidFromDate("0012.0 LMS-TAKST                               20090713                0131LMS.ZIP     200929");

		DateTime parsedDate = new DateTime(date);

		assertEquals(2009, parsedDate.getYear());
		assertEquals(7, parsedDate.getMonthOfYear());
		assertEquals(13, parsedDate.getDayOfMonth());
	}

	@Test
	public void can_parse_LMS1() throws Exception
	{
		LaegemiddelFactory config = new LaegemiddelFactory();
		File file = createFileWithContent(config, "28100009555SPLMKEMADR00100065Kemadrin                      tabletter           TAB           5 mg                0000005000MG 059300059300 N04AA04OR               D                ");
		List<Laegemiddel> drugs = new FixedLengthFileParser(new File[] { file }).parse(config, Laegemiddel.class);

		Date now = new Date();
		Takst takst = new Takst(now, now);
		TakstDataset<Laegemiddel> ds = new TakstDataset<Laegemiddel>(takst, drugs, Laegemiddel.class);
		takst.addDataset(ds);

		Laegemiddel drug = drugs.get(0);
		assertEquals("Kemadrin", drug.getNavn());
		assertEquals(new Long(28100009555l), drug.getDrugid());
		assertEquals(new Double(5.0), drug.getStyrkeNumerisk());
		assertEquals("5 mg", drug.getStyrkeKlarTekst());
		assertEquals("D", drug.getEgnetTilDosisdispensering());
		assertEquals("N04AA04", drug.getATC());
		assertEquals("TAB", drug.getFormKode());
		assertEquals("tabletter", drug.getLaegemiddelformTekst());
		assertEquals("OR", drug.getAdministrationsvej());
		assertEquals("KEMADR001", drug.getAlfabetSekvensplads());
		assertEquals(null, drug.getDatoForAfregistrAfLaegemiddel());
		assertEquals(null, drug.getKodeForYderligereFormOplysn());
		assertEquals(null, drug.getLaegemidletsSubstitutionsgruppe());
		assertEquals(new Long(59300), drug.getMTIndehaverKode());
		assertEquals(new Long(59300), drug.getRepraesentantDistributoerKode());
		assertEquals(new Long(65), drug.getSpecNummer());
		assertEquals(null, drug.getSubstitution());
		assertEquals(null, drug.getTrafikadvarsel());
		assertEquals("LM", drug.getVaredeltype());
		assertEquals("SP", drug.getVaretype());

		config = new LaegemiddelFactory();
		file = createFileWithContent(config, "28100110949SPLMXYLOCA00200985Xylocain                      inj.væske, opløsningINJVSKO       10 mg/ml            0000010000MGM056100056100 N01BB02EDIRIVPE                          ");
		drugs = new FixedLengthFileParser(new File[] { file }).parse(config, Laegemiddel.class);

		// Check the fields missing in the drug above.

		takst = new Takst(now, now);
		ds = new TakstDataset<Laegemiddel>(takst, drugs, Laegemiddel.class);
		takst.addDataset(ds);

		drug = drugs.get(0);
		assertEquals("Xylocain", drug.getNavn());
		assertEquals(new Long(28100110949l), drug.getDrugid());
		assertEquals(new Double(10), drug.getStyrkeNumerisk());
		assertEquals("10 mg/ml", drug.getStyrkeKlarTekst());
		assertEquals(null, drug.getEgnetTilDosisdispensering());
		assertEquals("N01BB02", drug.getATC());
		assertEquals("INJVSKO", drug.getFormKode());
		assertEquals("EDIRIVPE", drug.getAdministrationsvej());
		assertEquals("inj.væske, opløsning", drug.getLaegemiddelformTekst());
	}

	@Test
	public void can_parse_LMS2() throws Exception
	{
		PakningFactory config = new PakningFactory();
		File file = createFileWithContent(config, "28103623003049360010      1  56 stk. (blister) (Paranova)  00005600STBLI B         A      00000000002YA200803102009113020100222AF  083200");

		List<Pakning> packages = new FixedLengthFileParser(new File[] { file }).parse(config, Pakning.class);

		Date now = new Date();
		Takst takst = new Takst(now, now);
		TakstDataset<Pakning> ds = new TakstDataset<Pakning>(takst, packages, Pakning.class);
		takst.addDataset(ds);

		Pakning p = packages.get(0);
		assertThat(p.getDrugID(), is(28103623003L));
		assertThat(p.getVarenummer(), is(49360L));
		assertThat(p.getAlfabetSekvensnr(), is(10L));
		assertThat(p.getVarenummerForDelpakning(), is(nullValue()));
		assertThat(p.getAntalDelpakninger(), is(1L));
		assertThat(p.getPakningsstoerrelseKlartekst(), is("56 stk. (blister) (Paranova)"));
		assertThat(p.getPakningsstoerrelseNumerisk(), is(5600L));
		assertThat(p.getPakningsstorrelseEnhed(), is("ST"));
		assertThat(p.getEmballagetype(), is("BLI"));
		assertThat(p.getUdleveringsbestemmelse(), is("B"));
		assertThat(p.getUdleveringSpeciale(), is(nullValue()));
		assertThat(p.getMedicintilskudskode(), is("A"));
		assertThat(p.getKlausulForMedicintilskud(), is(nullValue()));
		assertThat(p.getAntalDDDPrPakning(), is(0L));
		assertThat(p.getOpbevaringstidNumerisk(), is(2L));
		assertThat(p.getOpbevaringstidEnhed(), is("Y"));
		assertThat(p.getOpbevaringsbetingelser(), is("A"));
		assertThat(p.getOprettelsesdato(), is(Dates.newDateDK(2008, 03, 10)));
		assertThat(p.getDatoForSenestePrisaendring(), is(Dates.newDateDK(2009, 11, 30)));
		assertThat(p.getUdgaaetDato(), is(Dates.newDateDK(2010, 02, 22)));
		assertThat(p.getBeregningskodeFraAIPTilRegPris(), is("A"));
		assertThat(p.getPakningOptagetITilskudsgruppe(), is("F"));
		assertThat(p.getFaerdigfremstillingsgebyr(), is(nullValue()));
		assertThat(p.getPakningsdistributoer(), is(83200L));

		// TODO: Check a packaging that have values where this one has null.
	}

	@Test(expected = Exception.class)
	public void Should_complain_if_no_file_are_present() throws Exception
	{
		// Try parsing with empty list of files.

		new DKMAParser(FAKE_TIME_GAP).parseFiles(new File[] {});
	}

	@Test(expected = Exception.class)
	public void exceptionHandlingRequiredFilesMissing() throws Exception
	{
		// Try parsing with empty list of files.

		DKMAParser takstParser = new DKMAParser(FAKE_TIME_GAP);
		File dir = FileUtils.toFile(getClass().getClassLoader().getResource("data/takst/incomplete/"));

		takstParser.parseFiles(dir.listFiles());
	}

	@Test(expected = Exception.class)
	public void exceptionHandlingUnparsable() throws Exception
	{
		// Arrange. Try parsing with empty list of files
		DKMAParser takstParser = new DKMAParser(FAKE_TIME_GAP);
		File dir = FileUtils.toFile(getClass().getClassLoader().getResource("data/takst/unparsable/"));
		takstParser.parseFiles(dir.listFiles());
	}
	
	@Test
	public void testManyToMany() throws Exception
	{
		Date from = Dates.newDateDK(2000, 1, 1);
		Date to = Dates.newDateDK(2000, 12, 1);
		Takst takst = new Takst(from, to);
		TakstDataset<ATC> atckoder = new TakstDataset<ATC>(takst, new ArrayList<ATC>(), ATC.class);
		takst.addDataset(atckoder);
	}

	@Test
	public void testVetFiltering()
	{
		// setup some objects
		ATC atcVet = new ATC();
		atcVet.setKode("QQ"); // starter med Q = til dyr

		ATC atcHum = new ATC();
		atcHum.setKode("AB"); // ikke starter med Q = til mennesker

		Laegemiddel lmVet = new Laegemiddel();
		lmVet.setDrugID(1l);
		lmVet.setATC("QB"); // starter med Q = til dyr

		Pakning pakVet = new Pakning();
		pakVet.setVarenummer(1l);
		pakVet.setDrugid(1l);

		Laegemiddel lmHum = new Laegemiddel();
		lmHum.setDrugID(2l);
		lmHum.setATC("AB"); // ikke starter med Q = til mennesker

		Pakning pakHuman = new Pakning();
		pakHuman.setVarenummer(2l);
		pakHuman.setDrugid(2l);

		// put the objects into arrays
		ArrayList<ATC> atcKoder = Lists.newArrayList();
		atcKoder.add(atcHum);
		atcKoder.add(atcVet);

		ArrayList<Pakning> pakninger = Lists.newArrayList();
		pakninger.add(pakVet);
		pakninger.add(pakHuman);

		ArrayList<Laegemiddel> laegemidler = Lists.newArrayList();
		laegemidler.add(lmHum);
		laegemidler.add(lmVet);

		// Add the arrays to the takst as datasets
		Takst takst = new Takst(new Date(), new Date());
		TakstDataset<Laegemiddel> lmr = new TakstDataset<Laegemiddel>(takst, laegemidler, Laegemiddel.class);
		TakstDataset<Pakning> pkr = new TakstDataset<Pakning>(takst, pakninger, Pakning.class);
		TakstDataset<ATC> atcr = new TakstDataset<ATC>(takst, atcKoder, ATC.class);
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
		assertNotNull(takst.getEntity(ATC.class, atcVet.getKey()));
		assertNotNull(takst.getEntity(ATC.class, atcHum.getKey()));

		// Filter the takst
		DKMAParser.filterOutVetDrugs(takst);

		// Check that the correct entities were removed from takst
		assertNull(takst.getEntity(Pakning.class, pakVet.getKey()));
		assertNotNull(takst.getEntity(Pakning.class, pakHuman.getKey()));
		assertNull(takst.getEntity(Laegemiddel.class, lmVet.getKey()));
		assertNotNull(takst.getEntity(Laegemiddel.class, lmHum.getKey()));
		assertNull(takst.getEntity(ATC.class, atcVet.getKey()));
		assertNotNull(takst.getEntity(ATC.class, atcHum.getKey()));
	}
}
