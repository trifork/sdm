package com.trifork.stamdata.lookup.personpart;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import oio.sagdok._2_0.PersonFlerRelationType;
import oio.sagdok._2_0.TidspunktType;
import oio.sagdok.person._1_0.AdresseType;
import oio.sagdok.person._1_0.CivilStatusKodeType;
import oio.sagdok.person._1_0.CprBorgerType;
import oio.sagdok.person._1_0.DanskAdresseType;
import oio.sagdok.person._1_0.EgenskabType;
import oio.sagdok.person._1_0.GroenlandAdresseType;
import oio.sagdok.person._1_0.LivStatusKodeType;
import oio.sagdok.person._1_0.NavnStrukturType;
import oio.sagdok.person._1_0.PersonRelationType;
import oio.sagdok.person._1_0.PersonType;
import oio.sagdok.person._1_0.VerdenAdresseType;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.trifork.stamdata.lookup.dao.CurrentPersonData;
import com.trifork.stamdata.util.DateUtils;
import com.trifork.stamdata.views.cpr.BarnRelation;
import com.trifork.stamdata.views.cpr.Civilstand;
import com.trifork.stamdata.views.cpr.Foedselsregistreringsoplysninger;
import com.trifork.stamdata.views.cpr.Folkekirkeoplysninger;
import com.trifork.stamdata.views.cpr.Person;
import com.trifork.stamdata.views.cpr.Statsborgerskab;
import com.trifork.stamdata.views.cpr.Udrejseoplysninger;
import com.trifork.stamdata.views.cpr.UmyndiggoerelseVaergeRelation;

import dk.oio.rep.cpr_dk.xml.schemas._2008._05._01.AddressCompleteGreenlandType;
import dk.oio.rep.cpr_dk.xml.schemas._2008._05._01.ForeignAddressStructureType;
import dk.oio.rep.ebxml.xml.schemas.dkcc._2003._02._13.CountryIdentificationCodeType;
import dk.oio.rep.ebxml.xml.schemas.dkcc._2003._02._13.CountryIdentificationSchemeType;
import dk.oio.rep.ebxml.xml.schemas.dkcc._2006._01._23.PersonGenderCodeType;
import dk.oio.rep.itst_dk.xml.schemas._2006._01._17.PersonNameStructureType;
import dk.oio.rep.xkom_dk.xml.schemas._2006._01._06.AddressPostalType;


public class PersonPartConverterTest {
	private PersonPartConverter converter;

	@Before
	public void before() {
		converter = new PersonPartConverter();
	}
	
	@Test
	public void fillsOutCprBorgerForPlainValidCprNumber() {
		Person person = new Person();
		person.setCpr("1020304050");
		Folkekirkeoplysninger folkekirkeoplysninger  = new Folkekirkeoplysninger();
		folkekirkeoplysninger.forholdsKode = "M";
		CurrentPersonData currentPerson = new CurrentPersonData(person, folkekirkeoplysninger, null, null, null, null, null, null, null, null);
		
		PersonType personType = converter.convert(currentPerson);
		assertNotNull(personType.getUUID());
		
		CprBorgerType cprBorger = personType.getRegistrering().get(0).getAttributListe().getRegisterOplysning().get(0).getCprBorger();
		assertEquals("1020304050", cprBorger.getPersonCivilRegistrationIdentifier());
		assertTrue(cprBorger.isPersonNummerGyldighedStatusIndikator());
		assertFalse(cprBorger.isNavneAdresseBeskyttelseIndikator());
		assertFalse(cprBorger.isTelefonNummerBeskyttelseIndikator());
		assertFalse(cprBorger.isForskerBeskyttelseIndikator());
		assertTrue(cprBorger.isFolkekirkeMedlemIndikator());
	}
	
	@Test
	public void fillsOutAddressProtection() {
		Person person = createValidPerson();
		Date now = new Date();
		Date past = new Date(now.getTime() - 24 * 60 * 60 * 1000);
		Date future = new Date(now.getTime() + 24 * 60 * 60 * 1000);
		
		CurrentPersonData currentPerson = new CurrentPersonData(person, null, null, null, null, null, null, null, null, null);
		PersonType personType = converter.convert(currentPerson);
		assertFalse(getAddressProtection(personType));
		
		person.navneBeskyttelsestartdato = past;
		personType = converter.convert(currentPerson);
		assertTrue(getAddressProtection(personType));

		person.navneBeskyttelsestartdato = future;
		personType = converter.convert(currentPerson);
		assertFalse(getAddressProtection(personType));
		
		person.navneBeskyttelsestartdato = past;
		person.navnebeskyttelseslettedato = past;
		personType = converter.convert(currentPerson);
		assertFalse(getAddressProtection(personType));
		
		person.navneBeskyttelsestartdato = past;
		person.navnebeskyttelseslettedato = future;
		personType = converter.convert(currentPerson);
		assertTrue(getAddressProtection(personType));
	}

	private boolean getAddressProtection(PersonType personType) {
		return personType.getRegistrering().get(0).getAttributListe().getRegisterOplysning().get(0).getCprBorger().isNavneAdresseBeskyttelseIndikator();
	}
	
	@Test
	public void fillsOutNameGenderAndBirthDate() throws ParseException {
		Person person = createValidPerson();
		CurrentPersonData currentPerson = new CurrentPersonData(person, null, null, null, null, null, null, null, null, null);
		PersonType personType = converter.convert(currentPerson);

		EgenskabType egenskabType = personType.getRegistrering().get(0).getAttributListe().getEgenskab().get(0);
		NavnStrukturType navnStruktur = egenskabType.getNavnStruktur();
		PersonNameStructureType personNameStructure = navnStruktur.getPersonNameStructure();
		assertEquals("Ole", personNameStructure.getPersonGivenName());
		assertEquals("Friis", personNameStructure.getPersonMiddleName());
		assertEquals("Olesen", personNameStructure.getPersonSurnameName());
		assertEquals(PersonGenderCodeType.FEMALE, egenskabType.getPersonGenderCode());
		assertEquals(DateUtils.yyyy_MM_dd.parse("1981-06-16"), egenskabType.getBirthDate().toGregorianCalendar().getTime());
	}

	@Test
	public void fillsOutDanishAddress() {
		Person person = createValidPerson();
		CurrentPersonData currentPerson = new CurrentPersonData(person, null, null, null, null, null, null, null, null, null);
		
		PersonType personType = converter.convert(currentPerson);
		
		CprBorgerType cprBorger = personType.getRegistrering().get(0).getAttributListe().getRegisterOplysning().get(0).getCprBorger();
		AdresseType adresseType = cprBorger.getFolkeregisterAdresse();
		DanskAdresseType danskAdresse = adresseType.getDanskAdresse();
		AddressPostalType postalAddress = danskAdresse.getAddressComplete().getAddressPostal();
		assertEquals("Scandinavian Congress Center", postalAddress.getMailDeliverySublocationIdentifier());
		assertEquals("Margrethepladsen", postalAddress.getStreetName()); // The best we can do
		assertEquals("Margrethepladsen", postalAddress.getStreetNameForAddressingName());
		assertEquals("4", postalAddress.getStreetBuildingIdentifier());
		assertEquals("3", postalAddress.getFloorIdentifier());
		assertEquals("th.", postalAddress.getSuiteIdentifier());
		assertEquals("Centrum af Århus", postalAddress.getDistrictSubdivisionIdentifier());
		assertEquals("8000", postalAddress.getPostCodeIdentifier());
		assertEquals("Århus C", postalAddress.getDistrictName());
		assertEquals(CountryIdentificationSchemeType.ISO_3166_ALPHA_2, postalAddress.getCountryIdentificationCode().getScheme());
		assertEquals("DK", postalAddress.getCountryIdentificationCode().getValue());
	}

	@Test
	public void fillsOutGreenlandAddress() {
		Person person = createValidPerson();
		person.postnummer = BigInteger.valueOf(3000);
		person.bygningsnummer = "123";
		CurrentPersonData currentPerson = new CurrentPersonData(person, null, null, null, null, null, null, null, null, null);

		PersonType personType = converter.convert(currentPerson);

		CprBorgerType cprBorger = personType.getRegistrering().get(0).getAttributListe().getRegisterOplysning().get(0).getCprBorger();
		AdresseType adresseType = cprBorger.getFolkeregisterAdresse();
		GroenlandAdresseType groenlandAdresse = adresseType.getGroenlandAdresse();
		assertNotNull(groenlandAdresse);
		AddressCompleteGreenlandType postalAddress = groenlandAdresse.getAddressCompleteGreenland();
		assertEquals("Scandinavian Congress Center", postalAddress.getMailDeliverySublocationIdentifier());
		assertEquals("Margrethepladsen", postalAddress.getStreetName()); // The best we can do
		assertEquals("Margrethepladsen", postalAddress.getStreetNameForAddressingName());
		assertEquals("4", postalAddress.getStreetBuildingIdentifier());
		assertEquals("3", postalAddress.getFloorIdentifier());
		assertEquals("th.", postalAddress.getSuiteIdentifier());
		assertEquals("Centrum af Århus", postalAddress.getDistrictSubdivisionIdentifier());
		assertEquals("3000", postalAddress.getPostCodeIdentifier());
		assertEquals("Århus C", postalAddress.getDistrictName());
		assertEquals(CountryIdentificationSchemeType.ISO_3166_ALPHA_2, postalAddress.getCountryIdentificationCode().getScheme());
		assertEquals("GL", postalAddress.getCountryIdentificationCode().getValue());
		assertEquals("123", postalAddress.getGreenlandBuildingIdentifier());
	}

	@Test
	public void fillsOutForeignAddress() {
		Person person = createValidPerson();
		person.status = "80"; // udrejst
		Udrejseoplysninger uo = new Udrejseoplysninger();
		uo.udrejseLandekode = "1234";
		uo.udlandsadresse1 = "line1";
		uo.udlandsadresse2 = "line2";
		uo.udlandsadresse3 = "line3";
		uo.udlandsadresse4 = "line4";
		uo.udlandsadresse5 = "line5";
		CurrentPersonData currentPerson = new CurrentPersonData(person, null, null, null, null, uo, null, null, null, null);

		PersonType personType = converter.convert(currentPerson);
		
		CprBorgerType cprBorger = personType.getRegistrering().get(0).getAttributListe().getRegisterOplysning().get(0).getCprBorger();
		AdresseType adresseType = cprBorger.getFolkeregisterAdresse();
		assertNull(adresseType.getDanskAdresse());
		VerdenAdresseType verdenAdresse = adresseType.getVerdenAdresse();
		assertNotNull(verdenAdresse);
		ForeignAddressStructureType foreignAddress = verdenAdresse.getForeignAddressStructure();
		CountryIdentificationCodeType countryIdentificationCode = foreignAddress.getCountryIdentificationCode();
		assertEquals(CountryIdentificationSchemeType.IMK, countryIdentificationCode.getScheme());
		assertEquals("1234", countryIdentificationCode.getValue());
		assertEquals("line1", foreignAddress.getPostalAddressFirstLineText());
		assertEquals("line2", foreignAddress.getPostalAddressSecondLineText());
		assertEquals("line3", foreignAddress.getPostalAddressThirdLineText());
		assertEquals("line4", foreignAddress.getPostalAddressFourthLineText());
		assertEquals("line5", foreignAddress.getPostalAddressFifthLineText());
	}
	
	@Test
	public void fillsOutMemberOfChurch() {
		// check not member
		Folkekirkeoplysninger fo = new Folkekirkeoplysninger();
		fo.forholdsKode = "U"; // uden for folkekirken
		CurrentPersonData currentPerson = new CurrentPersonData(createValidPerson(), fo, null, null, null, null, null, null, null, null);
		PersonType personType = converter.convert(currentPerson);
		CprBorgerType cprBorger = personType.getRegistrering().get(0).getAttributListe().getRegisterOplysning().get(0).getCprBorger();
		assertFalse(cprBorger.isFolkekirkeMedlemIndikator());
		
		// check member
		fo.forholdsKode = "M";
		personType = converter.convert(currentPerson);
		cprBorger = personType.getRegistrering().get(0).getAttributListe().getRegisterOplysning().get(0).getCprBorger();
		assertTrue(cprBorger.isFolkekirkeMedlemIndikator());
	}
	
	@Test
	public void fillsOutNationality() {
		Statsborgerskab sb = new Statsborgerskab();
		sb.landekode = "1234";
		CurrentPersonData cp = new CurrentPersonData(createValidPerson(), null, sb, null, null, null, null, null, null, null);

		PersonType personType = converter.convert(cp);

		CprBorgerType cprBorger = personType.getRegistrering().get(0).getAttributListe().getRegisterOplysning().get(0).getCprBorger();
		CountryIdentificationCodeType personNationalityCode = cprBorger.getPersonNationalityCode();
		assertEquals("1234", personNationalityCode.getValue());
		assertEquals(CountryIdentificationSchemeType.IMK, personNationalityCode.getScheme());
	}
	
	@Test
	@Ignore
	// TODO Vi kan ikke udfylde foedselsregistreringsinfo, da vi kun har en myndighedskode, som ikke
	// kan leveres i formatet
	public void fillsOutBirthInformation() {
		Foedselsregistreringsoplysninger fr = new Foedselsregistreringsoplysninger();
		fr.foedselsregistreringsstedkode = "foedselsKode";
		fr.foedselsregistreringstekst = "foedselsTekst";
		CurrentPersonData cp = new CurrentPersonData(createValidPerson(), null, null, fr, null, null, null, null, null, null);
		PersonType personType = converter.convert(cp);
		EgenskabType egenskabType = personType.getRegistrering().get(0).getAttributListe().getEgenskab().get(0);
	}
	
	@Test
	public void fillsOutCivilstand() {
		Civilstand cs = new Civilstand();
		cs.civilstandskode = "U";
		CurrentPersonData cp = new CurrentPersonData(createValidPerson(), null, null, null, cs, null, null, null, null, null);

		PersonType personType = converter.convert(cp);
		CivilStatusKodeType civilStatusKode = getCivilStatusKode(personType);
		assertEquals(CivilStatusKodeType.UGIFT, civilStatusKode);
	}
	
	@Test
	public void fillsOutLivsStatus() {
		Person person = createValidPerson();
		CurrentPersonData cp = new CurrentPersonData(person, null, null, null, null, null, null, null, null, null);
		PersonType personType = converter.convert(cp);
		assertEquals(LivStatusKodeType.FOEDT,getLivStatus(personType));
		person.status = "90"; // doed
		personType = converter.convert(cp);
		assertEquals(LivStatusKodeType.DOED,getLivStatus(personType));
		person.status = "80"; // udrejst
		personType = converter.convert(cp);
		assertEquals(LivStatusKodeType.FOEDT,getLivStatus(personType));
		person.status = "70"; // udrejst
		personType = converter.convert(cp);
		assertEquals(LivStatusKodeType.FORSVUNDET,getLivStatus(personType));
	}

	private LivStatusKodeType getLivStatus(PersonType personType) {
		return personType.getRegistrering().get(0).getTilstandListe().getLivStatus().getLivStatusKode();
	}
	
	@Test
	public void fillsOutAegtefaelle() {
		Civilstand cs = new Civilstand();
		cs.civilstandskode = "G";
		cs.aegtefaellePersonnummer = "1234567890";
		CurrentPersonData cp = new CurrentPersonData(createValidPerson(), null, null, null, cs, null, null, null, null, null);
		PersonType personType = converter.convert(cp);
		assertEquals("URN:CPR:1234567890", personType.getRegistrering().get(0).getRelationListe().getAegtefaelle().get(0).getReferenceID().getURNIdentifikator());
	}
	
	@Test
	public void fillsOutRegistreretPartner() {
		Civilstand cs = new Civilstand();
		cs.civilstandskode = "P";
		cs.aegtefaellePersonnummer = "1234567890";
		CurrentPersonData cp = new CurrentPersonData(createValidPerson(), null, null, null, cs, null, null, null, null, null);
		PersonType personType = converter.convert(cp);
		assertEquals("URN:CPR:1234567890", personType.getRegistrering().get(0).getRelationListe().getRegistreretPartner().get(0).getReferenceID().getURNIdentifikator());
	}
	@Test
	public void fillsOutRetligHandleevneVaergeForPerson() {
		UmyndiggoerelseVaergeRelation umyndiggoerelse = createUmyndiggoerelseVaergeRelation(null, "1020304050", at(2005, Calendar.JANUARY, 25), at(2020, Calendar.MARCH, 17));
		CurrentPersonData cp = new CurrentPersonData(createValidPerson(), null, null, null, null, null, umyndiggoerelse, null, null, null);

		PersonType personType = converter.convert(cp);
		
		PersonRelationType personRelationType = personType.getRegistrering().get(0).getRelationListe().getRetligHandleevneVaergeForPersonen().get(0);
		assertEquals("URN:CPR:1020304050", personRelationType.getReferenceID().getURNIdentifikator());
		assertTidspunktTypeEquals(at(2005, Calendar.JANUARY, 25), personRelationType.getVirkning().getFraTidspunkt());
		assertTidspunktTypeEquals(at(2020, Calendar.MARCH, 17), personRelationType.getVirkning().getTilTidspunkt());
		assertNotNull(personRelationType.getVirkning().getAktoerRef()); // Field is required
		assertEquals("Linje 1\n\n\nLinje 4\n", personRelationType.getCommentText());
	}
	
	@Test
	public void leavesOutTimestampsIfUndefined() {
		UmyndiggoerelseVaergeRelation umyndiggoerelse = createUmyndiggoerelseVaergeRelation(null, "1020304050", DateUtils.PAST.getTime(), DateUtils.FUTURE.getTime());
		CurrentPersonData cp = new CurrentPersonData(createValidPerson(), null, null, null, null, null, umyndiggoerelse, null, null, null);

		PersonType personType = converter.convert(cp);
		
		PersonRelationType personRelationType = personType.getRegistrering().get(0).getRelationListe().getRetligHandleevneVaergeForPersonen().get(0);
		assertNull(personRelationType.getVirkning().getFraTidspunkt());
		assertNull(personRelationType.getVirkning().getTilTidspunkt());
	}

	@Test
	public void fillsOutRetligHandleevneVaergeForVaerge() {
		List<UmyndiggoerelseVaergeRelation> vaergemaal = new ArrayList<UmyndiggoerelseVaergeRelation>();
		vaergemaal.add(createUmyndiggoerelseVaergeRelation("0102030405", null, at(2005, Calendar.AUGUST, 21), at(2011, Calendar.DECEMBER, 24))); 
		vaergemaal.add(createUmyndiggoerelseVaergeRelation("0102030407", null, at(2005, Calendar.AUGUST, 21), at(2011, Calendar.DECEMBER, 24)));
		
		CurrentPersonData cp = new CurrentPersonData(createValidPerson(), null, null, null, null, null, null, vaergemaal, null, null);
		PersonType personType = converter.convert(cp);
		
		List<PersonFlerRelationType> retligHandleevneVaergemaalsindehaver = personType.getRegistrering().get(0).getRelationListe().getRetligHandleevneVaergemaalsindehaver();
		assertEquals(2, retligHandleevneVaergemaalsindehaver.size());
		PersonFlerRelationType personFlerRelationType = retligHandleevneVaergemaalsindehaver.get(0);
		assertEquals("URN:CPR:0102030405", personFlerRelationType.getReferenceID().getURNIdentifikator());
		assertTidspunktTypeEquals(at(2005, Calendar.AUGUST, 21), personFlerRelationType.getVirkning().getFraTidspunkt());
		assertTidspunktTypeEquals(at(2011, Calendar.DECEMBER, 24), personFlerRelationType.getVirkning().getTilTidspunkt());
		assertEquals("URN:Aktoer:Importer", personFlerRelationType.getVirkning().getAktoerRef().getURNIdentifikator());
		assertEquals("Linje 1\n\n\nLinje 4\n", personFlerRelationType.getCommentText());
	}
	
	@Test
	public void fillsOutBoern() {
		List<BarnRelation> barnRelationer = new ArrayList<BarnRelation>();
		barnRelationer.add(createBarnRelation("1020304050", "1020304051"));
		barnRelationer.add(createBarnRelation("1020304050", "1020304052"));
		CurrentPersonData cp = new CurrentPersonData(createValidPerson(), null, null, null, null, null, null, null, barnRelationer, null);
		PersonType personType = converter.convert(cp);
		List<PersonFlerRelationType> boern = personType.getRegistrering().get(0).getRelationListe().getBoern();
		assertEquals(2, boern.size());
		assertEquals("URN:CPR:1020304051", boern.get(0).getReferenceID().getURNIdentifikator());
		assertEquals("URN:CPR:1020304052", boern.get(1).getReferenceID().getURNIdentifikator());
	}

	@Test
	public void createsCorrectCivilstatusWhenSeparated() {
		Civilstand cs = new Civilstand();
		cs.civilstandskode = "G";
		CurrentPersonData cp = new CurrentPersonData(createValidPerson(), null, null, null, cs, null, null, null, null, null);
		PersonType personType = converter.convert(cp);
		assertEquals(CivilStatusKodeType.GIFT, getCivilStatusKode(personType));

		cs.separation = new Date();
		personType = converter.convert(cp);
		assertEquals(CivilStatusKodeType.SEPARERET, getCivilStatusKode(personType));

		cs.civilstandskode = "P";
		personType = converter.convert(cp);
		assertEquals(CivilStatusKodeType.SEPARERET, getCivilStatusKode(personType));

		cs.civilstandskode = "U";
		personType = converter.convert(cp);
		assertEquals(CivilStatusKodeType.UGIFT, getCivilStatusKode(personType));
	}
	
	@Test
	public void removesWhitespaceFromStreetBuildingIdentifier() {
		String husnummer = "12 C";
		assertEquals("12C", converter.createStreetBuildingIdentifier(husnummer));
	}

	private Person createValidPerson() {
		Person person = new Person();
		person.setCpr("1020304050");
		// C/O-navn ikke i OIO-adresser?!? person.coNavn = "Trifork A/S";
		person.fornavn = "Ole";
		person.mellemnavn = "Friis";
		person.efternavn = "Olesen";
		person.koen = "K";
		try {
			person.foedselsdato = DateUtils.yyyy_MM_dd.parse("1981-06-16");
		} catch (ParseException e) {
			throw new RuntimeException();
		}
		person.lokalitet = "Scandinavian Congress Center";
		person.vejnavn = "Margrethepladsen";
		person.husnummer = "4";
		person.etage = "3";
		person.sideDoerNummer = "th.";
		person.postnummer = BigInteger.valueOf(8000);
		person.bynavn = "Centrum af Århus";
		person.postdistrikt = "Århus C";
		person.status = "01";
		return person;
	}

	private UmyndiggoerelseVaergeRelation createUmyndiggoerelseVaergeRelation(String umyndiggjortCpr, String vaergeCpr, Date from, Date to) {
		UmyndiggoerelseVaergeRelation umyndiggoerelse = new UmyndiggoerelseVaergeRelation();
		umyndiggoerelse.setValidFrom(from);
		umyndiggoerelse.setValidTo(to);
		umyndiggoerelse.setCpr(umyndiggjortCpr);
		umyndiggoerelse.relationCpr = vaergeCpr;
		umyndiggoerelse.RelationsTekst1 = "Linje 1";
		umyndiggoerelse.RelationsTekst2 = "";
		umyndiggoerelse.RelationsTekst4 = "Linje 4";
		return umyndiggoerelse;
	}
	
	private BarnRelation createBarnRelation(String cpr, String barnCpr) {
		BarnRelation result = new BarnRelation();
		result.setCpr(cpr);
		result.barnCPR = barnCpr;
		return result;
	}

	private CivilStatusKodeType getCivilStatusKode(PersonType personType) {
		return personType.getRegistrering().get(0).getTilstandListe().getCivilStatus().getCivilStatusKode();
	}
	
	private Date at(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		return calendar.getTime();
	}
	
	private void assertTidspunktTypeEquals(Date date, TidspunktType tidspunkt) {
		assertEquals(date, tidspunkt.getTidsstempelDatoTid().toGregorianCalendar().getTime());
	}
}
