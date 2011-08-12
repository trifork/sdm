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

package com.trifork.stamdata.importer.jobs.cpr;

import static com.trifork.stamdata.importer.util.DateUtils.yyyyMMddHHmm;
import static com.trifork.stamdata.importer.util.DateUtils.yyyy_MM_dd;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.trifork.stamdata.importer.jobs.cpr.models.BarnRelation;
import com.trifork.stamdata.importer.jobs.cpr.models.CPRDataset;
import com.trifork.stamdata.importer.jobs.cpr.models.ForaeldreMyndighedRelation;
import com.trifork.stamdata.importer.jobs.cpr.models.Klarskriftadresse;
import com.trifork.stamdata.importer.jobs.cpr.models.NavneBeskyttelse;
import com.trifork.stamdata.importer.jobs.cpr.models.Navneoplysninger;
import com.trifork.stamdata.importer.jobs.cpr.models.Personoplysninger;
import com.trifork.stamdata.importer.jobs.cpr.models.UmyndiggoerelseVaergeRelation;


public class CPRParserTest
{

	@Test
	public void testRecord01() throws Exception
	{
		String LINE = "0010101965058010196505901200012240000*K1896-01-01*1997-09-09*2007-09-09*Pensionist";

		Personoplysninger record = CPRParser.personoplysninger(LINE);

		assertEquals("0101965058", record.getCpr());
		assertEquals("0101965059", record.getGaeldendeCpr());
		assertEquals("01", record.getStatus());
		assertEquals(yyyyMMddHHmm.parse("200012240000"), record.getStatusDato());
		assertEquals("*", record.getStatusMakering());
		assertEquals("K", record.getKoen());
		assertEquals(yyyy_MM_dd.parse("1896-01-01"), record.getFoedselsdato());
		assertEquals("*", record.getFoedselsdatoMarkering());
		assertEquals(yyyy_MM_dd.parse("1997-09-09"), record.getStartDato());
		assertEquals("*", record.getStartDatoMarkering());
		assertEquals(yyyy_MM_dd.parse("2007-09-09"), record.getSlutDato());
		assertEquals("*", record.getSlutDatoMarkering());
		assertEquals("Pensionist", record.getStilling());
	}

	@Test
	public void testRecord03() throws Exception
	{
		String LINE = "0030712614455Henriksen,Klaus                   Buchholdt                         Solhavehjemmet                    Industrivænget 2,2 mf             Hasseris                          9000Aalborg             08518512002 02  mf12  Industrivænget";

		Klarskriftadresse record = CPRParser.klarskriftadresse(LINE);

		assertEquals("0712614455", record.getCpr());
		assertEquals("Henriksen,Klaus", record.getAdresseringsNavn());
		assertEquals("Buchholdt", record.getCoNavn());
		assertEquals("Solhavehjemmet", record.getLokalitet());
		assertEquals("Industrivænget 2,2 mf", record.getVejnavnTilAdresseringsNavn());
		assertEquals("Hasseris", record.getByNavn());
		assertEquals(new Long(9000L), record.getPostNummer());
		assertEquals("Aalborg", record.getPostDistrikt());
		assertEquals(new Long(851L), record.getKommuneKode());
		assertEquals(new Long(8512L), record.getVejKode());
		assertEquals("2", record.getHusNummer());
		assertEquals("2", record.getEtage());
		assertEquals("mf", record.getSideDoerNummer());
		assertEquals("12", record.getBygningsNummer());
		assertEquals("Industrivænget", record.getVejNavn());
	}

	@Test
	public void testRecord04() throws Exception
	{
		String LINE = "004280236303900011997-09-092001-02-20";

		NavneBeskyttelse record = CPRParser.navneBeskyttelse(LINE);

		assertEquals("2802363039", record.getCpr());
		assertEquals(yyyy_MM_dd.parse("1997-09-09"), record.getNavneBeskyttelseStartDato());
		assertEquals(yyyy_MM_dd.parse("2001-02-20"), record.getNavneBeskyttelseSletteDato());
	}

	@Test
	public void testRecord08() throws Exception
	{
		String LINE = "0080702614155Hans-Martin                                       *Buchholdt                               *Wicker                                  *197902152000 Wicker,Hans-Martin";

		Navneoplysninger record = CPRParser.navneoplysninger(LINE);

		assertEquals("0702614155", record.getCpr());
		assertEquals("Hans-Martin", record.getFornavn());
		assertEquals("*", record.getFornavnMarkering());
		assertEquals("Buchholdt", record.getMellemnavn());
		assertEquals("*", record.getMellemnavnMarkering());
		assertEquals("Wicker", record.getEfternavn());
		assertEquals("*", record.getEfternavnMarkering());
		assertEquals(yyyyMMddHHmm.parse("197902152000"), record.getStartDato());
		assertEquals(" ", record.getStartDatoMarkering());
		assertEquals("Wicker,Hans-Martin", record.getAdresseringsNavn());
	}

	@Test
	public void testRecord14() throws Exception
	{
		// Arrange
		String LINE = "01409014140250707614293";

		// Act
		BarnRelation record = CPRParser.barnRelation(LINE);

		// Assert
		assertEquals("0901414025", record.getCpr());
		assertEquals("0707614293", record.getBarnCpr());
	}

	@Test
	public void testRecord16() throws Exception
	{

		// Arrange
		String LINE = "016311297002800032008-01-01*2009-01-0106016412762008-06-01";

		// Act
		ForaeldreMyndighedRelation record = CPRParser.foraeldreMyndighedRelation(LINE);

		// Assert
		assertEquals("3112970028", record.getCpr());
		assertEquals("0003", record.getTypeKode());
		assertEquals("Mor", record.getTypeTekst());
		assertEquals(yyyy_MM_dd.parse("2008-01-01"), record.getForaeldreMyndighedStartDato());
		assertEquals("*", record.getForaeldreMyndighedMarkering());
		assertEquals(yyyy_MM_dd.parse("2009-01-01"), record.getForaeldreMyndighedSlettedato());
		assertEquals("0601641276", record.getRelationCpr());
		assertEquals(yyyy_MM_dd.parse("2008-06-01"), record.getRelationCprStartDato());
		assertEquals(yyyy_MM_dd.parse("2008-01-01"), record.getValidFrom());
		assertEquals(yyyy_MM_dd.parse("2009-01-01"), record.getValidTo());
	}

	@Test
	public void testRecord17() throws Exception
	{

		// Arrange
		String LINE = "01707096141262000-02-28*2008-02-28000109044141312008-06-01Roberto Andersen                  2007-01-0199 Tarragon Ln, Edgewater, MD, USA";

		// Act
		UmyndiggoerelseVaergeRelation record = CPRParser.umyndiggoerelseVaergeRelation(LINE);
		CPRDataset cpr = new CPRDataset();
		record.setDataset(cpr);

		// Assert
		assertEquals("0709614126", record.getCpr());
		assertEquals(yyyy_MM_dd.parse("2000-02-28"), record.getUmyndigStartDato());
		assertEquals("*", record.getUmyndigStartDatoMarkering());
		assertEquals(yyyy_MM_dd.parse("2008-02-28"), record.getUmyndigSletteDato());
		assertEquals("0001", record.getTypeKode());
		assertEquals("Værges CPR findes", record.getTypeTekst());
		assertEquals("0904414131", record.getRelationCpr());
		assertEquals(yyyy_MM_dd.parse("2008-06-01"), record.getRelationCprStartDato());
		assertEquals("Roberto Andersen", record.getVaergesNavn());
		assertEquals(yyyy_MM_dd.parse("2007-01-01"), record.getVaergesNavnStartDato());
		assertEquals("99 Tarragon Ln, Edgewater, MD, USA", record.getRelationsTekst1());
		assertEquals("", record.getRelationsTekst2());
		assertEquals("", record.getRelationsTekst3());
		assertEquals("", record.getRelationsTekst4());
		assertEquals("", record.getRelationsTekst5());
		// Insert a date into the 'DataSet' before 'UmyndigStartDato' and test
		// that we get the date from the record
		cpr.setValidFrom(yyyy_MM_dd.parse("2000-02-27"));
		assertEquals(yyyy_MM_dd.parse("2000-02-28"), record.getValidFrom());
		// Insert a date into the 'DataSet' after 'UmyndigStartDato' and test
		// that we get the date from the dataset
		cpr.setValidFrom(yyyy_MM_dd.parse("2000-03-01"));
		assertEquals(yyyy_MM_dd.parse("2000-03-01"), record.getValidFrom());
		// Clear 'UmyndigStartDato' and check that we get the one from the
		// dataset
		record.setUmyndigStartDato(null);
		assertEquals(yyyy_MM_dd.parse("2000-03-01"), record.getValidFrom());

		assertEquals(yyyy_MM_dd.parse("2008-02-28"), record.getValidTo());
	}
}
