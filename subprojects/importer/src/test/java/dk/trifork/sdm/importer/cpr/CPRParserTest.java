package dk.trifork.sdm.importer.cpr;

import static dk.trifork.sdm.util.DateUtils.yyyyMMddHHmm;
import static dk.trifork.sdm.util.DateUtils.yyyy_MM_dd;
import static org.junit.Assert.*;

import org.junit.Test;

import dk.trifork.sdm.importer.cpr.model.AktuelCivilstand;
import dk.trifork.sdm.importer.cpr.model.AktuelCivilstand.Civilstand;
import dk.trifork.sdm.importer.cpr.model.BarnRelation;
import dk.trifork.sdm.importer.cpr.model.CPRDataset;
import dk.trifork.sdm.importer.cpr.model.Foedselsregistreringsoplysninger;
import dk.trifork.sdm.importer.cpr.model.Folkekirkeoplysninger;
import dk.trifork.sdm.importer.cpr.model.Folkekirkeoplysninger.Folkekirkeforhold;
import dk.trifork.sdm.importer.cpr.model.ForaeldreMyndighedRelation;
import dk.trifork.sdm.importer.cpr.model.Haendelse;
import dk.trifork.sdm.importer.cpr.model.Klarskriftadresse;
import dk.trifork.sdm.importer.cpr.model.KommunaleForhold;
import dk.trifork.sdm.importer.cpr.model.KommunaleForhold.Kommunalforholdstype;
import dk.trifork.sdm.importer.cpr.model.NavneBeskyttelse;
import dk.trifork.sdm.importer.cpr.model.Navneoplysninger;
import dk.trifork.sdm.importer.cpr.model.Personoplysninger;
import dk.trifork.sdm.importer.cpr.model.Statsborgerskab;
import dk.trifork.sdm.importer.cpr.model.Udrejseoplysninger;
import dk.trifork.sdm.importer.cpr.model.UmyndiggoerelseVaergeRelation;
import dk.trifork.sdm.importer.cpr.model.Valgoplysninger;
import dk.trifork.sdm.importer.cpr.model.Valgoplysninger.Valgret;
import dk.trifork.sdm.util.DateUtils;

public class CPRParserTest {

	@Test
	public void testRecord01() throws Exception {

		// Arrange
		String LINE = "0010101965058010196505901200012240000*K1896-01-01*1997-09-09*2007-09-09*Pensionist";

		// Act
		Personoplysninger record = CPRParser.personoplysninger(LINE);

		// Assert
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
	public void testRecord03() throws Exception {
		String LINE = "0030712614455Henriksen,Klaus                   Buchholdt                         Solhavehjemmet                    Industrivænget 2,2 mf             Hasseris                          9000Aalborg             08518512002 02  mf12  Industrivænget";

		Klarskriftadresse record = CPRParser.klarskriftadresse(LINE);

		assertEquals("0712614455", record.getCpr());
		assertEquals("Henriksen,Klaus", record.getAdresseringsNavn());
		assertEquals("Buchholdt", record.getCoNavn());
		assertEquals("Solhavehjemmet", record.getLokalitet());
		assertEquals("Industrivænget 2,2 mf", record.getStandardAdresse());
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
	public void testRecord04() throws Exception {
		String LINE = "004280236303900011997-09-092001-02-20";

		NavneBeskyttelse record = CPRParser.navneBeskyttelse(LINE);

		assertEquals("2802363039", record.getCpr());
		assertEquals(yyyy_MM_dd.parse("1997-09-09"), record.getNavneBeskyttelseStartDato());
		assertEquals(yyyy_MM_dd.parse("2001-02-20"), record.getNavneBeskyttelseSletteDato());
	}
	
	@Test
	public void testRecord05() throws Exception {
		String LINE = "00507086143355180199902011300 Berlinerstrasse 102               Udlandsadresse linje 2 2 2 2 2 2 2Et sted i Tyskland                Udlandsadresse linje 3 3 3 3 3 3 3Udlandsadresse linje 4 4 4 4 4 4 4";
		Udrejseoplysninger record = CPRParser.udrejseoplysninger(LINE);
		assertEquals("0708614335", record.getCpr());
		assertEquals("5180", record.getUdrejseLandekode());
		assertEquals(yyyyMMddHHmm.parse("199902011300"), record.getUdrejsedato());
		assertEquals(" ", record.getUdrejsedatoUsikkerhedsmarkering());
		assertEquals("Berlinerstrasse 102", record.getUdlandsadresse1());
		assertEquals("Udlandsadresse linje 2 2 2 2 2 2 2", record.getUdlandsadresse2());
		assertEquals("Et sted i Tyskland", record.getUdlandsadresse3());
		assertEquals("Udlandsadresse linje 3 3 3 3 3 3 3", record.getUdlandsadresse4());
		assertEquals("Udlandsadresse linje 4 4 4 4 4 4 4", record.getUdlandsadresse5());
	}

	@Test
	public void testRecord08() throws Exception {

		// Arrange
		String LINE = "0080702614155Hans-Martin                                       *Buchholdt                               *Wicker                                  *197902152000 Wicker,Hans-Martin";

		// Act
		Navneoplysninger record = CPRParser.navneoplysninger(LINE);

		// Assert
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
	public void testRecord09() throws Exception {
		String LINE = "00901019650585180";
		Foedselsregistreringsoplysninger record = CPRParser.foedselsregistreringsoplysninger(LINE);
		assertEquals("0101965058", record.getCpr());
		assertEquals("5180", record.getFoedselsregistreringsstedkode());
		assertEquals("", record.getFoedselsregistreringstekst());
	}

	@Test
	public void testRecord10() throws Exception {
		String LINE = "01031120000105180190502201043";
		Statsborgerskab record = CPRParser.statsborgerskab(LINE);
		assertEquals("3112000010", record.getCpr());
		assertEquals("5180", record.getLandekode());
		assertEquals(yyyyMMddHHmm.parse("190502201043"), record.getStatsborgerskabstartdato());
		assertEquals("", record.getStatsborgerskabstartdatousikkerhedsmarkering());
	}

	@Test
	public void testRecord14() throws Exception {

		// Arrange
		String LINE = "01409014140250707614293";

		// Act
		BarnRelation record = CPRParser.barnRelation(LINE);

		// Assert
		assertEquals("0901414025", record.getCpr());
		assertEquals("0707614293", record.getBarnCpr());
	}

	@Test
	public void testRecord16() throws Exception {

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
		assertEquals(DateUtils.toCalendar(yyyy_MM_dd.parse("2008-01-01")), record.getValidFrom());
		assertEquals(DateUtils.toCalendar(yyyy_MM_dd.parse("2009-01-01")), record.getValidTo());
	}

	@Test
	public void testRecord17() throws Exception {

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
		cpr.setValidFrom(DateUtils.toCalendar(yyyy_MM_dd.parse("2000-02-27")));
		assertEquals(DateUtils.toCalendar(yyyy_MM_dd.parse("2000-02-28")), record.getValidFrom());
		// Insert a date into the 'DataSet' after 'UmyndigStartDato' and test
		// that we get the date from the dataset
		cpr.setValidFrom(DateUtils.toCalendar(yyyy_MM_dd.parse("2000-03-01")));
		assertEquals(DateUtils.toCalendar(yyyy_MM_dd.parse("2000-03-01")), record.getValidFrom());
		// Clear 'UmyndigStartDato' and check that we get the one from the
		// dataset
		record.setUmyndigStartDato(null);
		assertEquals(DateUtils.toCalendar(yyyy_MM_dd.parse("2000-03-01")), record.getValidFrom());

		assertEquals(DateUtils.toCalendar(yyyy_MM_dd.parse("2008-02-28")), record.getValidTo());
	}

	@Test
	public void canParseRecord11_Folkekirkeoplysninger() throws Exception {
		String line = "0110709614126F2008-02-28*";

		Folkekirkeoplysninger record = CPRParser.folkekirkeoplysninger(line);

		assertEquals("0709614126", record.getCpr());
		assertEquals(Folkekirkeforhold.medlemAfFolkekirken, record.getForhold());
		assertEquals("F", record.getForholdskode());
		assertEquals(yyyy_MM_dd.parse("2008-02-28"), record.getStartdato());
		assertEquals("*", record.getStartdatomarkering());
	}

	@Test
	public void canParseRecord12_AktuelCivilstand() throws Exception {
		String line = "0120901414025G0912414426                                              196103132000";

		AktuelCivilstand record = CPRParser.aktuelCivilstand(line);

		assertEquals("0901414025", record.getCpr());
		assertEquals(Civilstand.gift, record.getCivilstand());
		assertEquals("G", record.getCivilstandskode());
		assertEquals("0912414426", record.getAegtefaellepersonnummer());
		assertNull(record.getAegtefaellefoedselsdato());
		assertEquals(" ", record.getAegtefaellefoedselsdatomarkering());
		assertEquals("", record.getAegtefaellenavn());
		assertEquals(" ", record.getAegtefaellenavnmarkering());
		assertEquals(yyyyMMddHHmm.parse("196103132000"), record.getStartdato());
		assertEquals("", record.getStartdatomarkering());
		assertNull(record.getSeparation());
	}

	@Test
	public void canParseRecord18_KommunaleForhold() throws Exception {
		String line = "01807016140541A    1991-05-06 KOMFOR-BEMÆRK";

		KommunaleForhold record = CPRParser.kommunaleForhold(line);

		assertEquals("0701614054", record.getCpr());
		assertEquals("1", record.getKommunalforholdstypekode());
		assertEquals(Kommunalforholdstype.adskilt, record.getKommunalforholdstype());
		assertEquals("A", record.getKommunalforholdskode());
		assertEquals(yyyy_MM_dd.parse("1991-05-06"), record.getStartdato());
		assertEquals(" ", record.getStartdatomarkering());
		assertEquals("KOMFOR-BEMÆRK", record.getBemaerkninger());
	}

	@Test
	public void canParseRecord20_Valgoplysninger() throws Exception {
		String line = "020070861433500011999-03-101999-02-012001-03-10";

		Valgoplysninger record = CPRParser.valgoplysninger(line);

		assertEquals("0708614335", record.getCpr());
		assertEquals(Valgret.almindeligValgret, record.getValgret());
		assertEquals("1", record.getValgkode());
		assertEquals(yyyy_MM_dd.parse("1999-03-10"), record.getValgretsdato());
		assertEquals(yyyy_MM_dd.parse("1999-02-01"), record.getStartdato());
		assertEquals(yyyy_MM_dd.parse("2001-03-10"), record.getSlettedato());
	}
	
	@Test
	public void canParseRecord99_Haendelser() throws Exception {
		String line = "0993012995007200111061637A05";
		
		Haendelse record = CPRParser.haendelse(line);
		
		assertNotNull(record.getUuid());
		assertEquals("3012995007", record.getCpr());
		assertEquals(yyyyMMddHHmm.parse("200111061637"), record.getAjourfoeringsdato());
		assertEquals("A05", record.getHaendelseskode());
		assertEquals("", record.getAfledtMarkering());
		assertEquals("", record.getNoeglekonstant());
	}
}
