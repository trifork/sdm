package com.trifork.stamdata.lookup.personpart;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;

import oio.sagdok.person._1_0.AdresseType;
import oio.sagdok.person._1_0.CivilStatusKodeType;
import oio.sagdok.person._1_0.CprBorgerType;
import oio.sagdok.person._1_0.DanskAdresseType;
import oio.sagdok.person._1_0.EgenskabType;
import oio.sagdok.person._1_0.PersonType;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.trifork.stamdata.lookup.dao.CurrentPersonData;
import com.trifork.stamdata.views.cpr.Civilstand;
import com.trifork.stamdata.views.cpr.Foedselsregistreringsoplysninger;
import com.trifork.stamdata.views.cpr.Folkekirkeoplysninger;
import com.trifork.stamdata.views.cpr.Person;
import com.trifork.stamdata.views.cpr.Statsborgerskab;

import dk.oio.rep.ebxml.xml.schemas.dkcc._2003._02._13.CountryIdentificationCodeType;
import dk.oio.rep.ebxml.xml.schemas.dkcc._2003._02._13.CountryIdentificationSchemeType;
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
		person.cpr = "1020304050";
		Folkekirkeoplysninger folkekirkeoplysninger  = new Folkekirkeoplysninger();
		folkekirkeoplysninger.forholdsKode = "M";
		Statsborgerskab sb = new Statsborgerskab();
		sb.landekode = "1234";
		CurrentPersonData currentPerson = new CurrentPersonData(person, folkekirkeoplysninger, sb, null, null);
		
		PersonType personType = converter.convert(currentPerson);
		
		CprBorgerType cprBorger = personType.getRegistrering().get(0).getAttributListe().getRegisterOplysning().get(0).getCprBorger();
		assertEquals("1020304050", cprBorger.getPersonCivilRegistrationIdentifier());
		assertTrue(cprBorger.isPersonNummerGyldighedStatusIndikator());
		assertFalse(cprBorger.isNavneAdresseBeskyttelseIndikator());
		assertFalse(cprBorger.isTelefonNummerBeskyttelseIndikator());
		assertFalse(cprBorger.isForskerBeskyttelseIndikator());
		assertTrue(cprBorger.isFolkekirkeMedlemIndikator());
	}
	
	@Test
	public void fillsOutDanishAddress() {
		Person person = createValidPerson();
		CurrentPersonData currentPerson = new CurrentPersonData(person, null, null, null, null);
		
		PersonType personType = converter.convert(currentPerson);
		
		CprBorgerType cprBorger = personType.getRegistrering().get(0).getAttributListe().getRegisterOplysning().get(0).getCprBorger();
		AdresseType adresseType = cprBorger.getFolkeregisterAdresse();
		DanskAdresseType danskAdresse = adresseType.getDanskAdresse();
		AddressPostalType postalAddress = danskAdresse.getAddressComplete().getAddressPostal();
		assertEquals("Scandinavian Congress Center", postalAddress.getMailDeliverySublocationIdentifier());
		assertEquals("Margrethepladsen", postalAddress.getStreetName()); // The best we can do
		assertEquals("Margrethepladsen", postalAddress.getStreetNameForAddressingName());
		assertEquals("4, 1", postalAddress.getStreetBuildingIdentifier());
		assertEquals("3", postalAddress.getFloorIdentifier());
		assertEquals("th.", postalAddress.getSuiteIdentifier());
		assertEquals("Centrum af Århus", postalAddress.getDistrictSubdivisionIdentifier());
		assertEquals("8000", postalAddress.getPostCodeIdentifier());
		assertEquals("Århus C", postalAddress.getDistrictName());
		assertEquals(CountryIdentificationSchemeType.ISO_3166_ALPHA_2, postalAddress.getCountryIdentificationCode().getScheme());
		assertEquals("DK", postalAddress.getCountryIdentificationCode().getValue());
	}

	private Person createValidPerson() {
		Person person = new Person();
		person.cpr = "1020304050";
		// C/O-navn ikke i OIO-adresser?!? person.coNavn = "Trifork A/S";
		person.lokalitet = "Scandinavian Congress Center";
		person.vejnavn = "Margrethepladsen";
		person.husnummer = "4";
		person.bygningsnummer = "1";
		person.etage = "3";
		person.sideDoerNummer = "th.";
		person.postnummer = BigInteger.valueOf(8000);
		person.bynavn = "Centrum af Århus";
		person.postdistrikt = "Århus C";
		return person;
	}
	
	
	@Test
	public void fillsOutMemberOfChurch() {
		// check not member
		Folkekirkeoplysninger fo = new Folkekirkeoplysninger();
		fo.forholdsKode = "U"; // uden for folkekirken
		CurrentPersonData currentPerson = new CurrentPersonData(createValidPerson(), fo, null, null, null);
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
		CurrentPersonData cp = new CurrentPersonData(createValidPerson(), null, sb, null, null);
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
		CurrentPersonData cp = new CurrentPersonData(createValidPerson(), null, null, fr, null);
		PersonType personType = converter.convert(cp);
		EgenskabType egenskabType = personType.getRegistrering().get(0).getAttributListe().getEgenskab().get(0);
	}
	
	@Test
	public void fillsOutCivilstand() {
		Civilstand cs = new Civilstand();
		cs.civilstandskode = "U";
		CurrentPersonData cp = new CurrentPersonData(createValidPerson(), null, null, null, cs);
		PersonType personType = converter.convert(cp);
		CivilStatusKodeType civilStatusKode = personType.getRegistrering().get(0).getTilstandListe().getCivilStatus().getCivilStatusKode();
		assertEquals(CivilStatusKodeType.UGIFT, civilStatusKode);
	}
}
