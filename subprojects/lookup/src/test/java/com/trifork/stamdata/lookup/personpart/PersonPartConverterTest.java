package com.trifork.stamdata.lookup.personpart;

import static org.junit.Assert.*;

import java.math.BigInteger;

import oio.sagdok.person._1_0.AdresseType;
import oio.sagdok.person._1_0.CprBorgerType;
import oio.sagdok.person._1_0.DanskAdresseType;
import oio.sagdok.person._1_0.PersonType;

import org.junit.Before;
import org.junit.Test;

import com.trifork.stamdata.lookup.dao.CurrentPersonData;
import com.trifork.stamdata.views.cpr.Person;

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
		CurrentPersonData currentPerson = new CurrentPersonData(person);
		
		PersonType personType = converter.convert(currentPerson);
		
		CprBorgerType cprBorger = personType.getRegistrering().get(0).getAttributListe().getRegisterOplysning().get(0).getCprBorger();
		assertEquals("1020304050", cprBorger.getPersonCivilRegistrationIdentifier());
		assertTrue(cprBorger.isPersonNummerGyldighedStatusIndikator());
		assertFalse(cprBorger.isNavneAdresseBeskyttelseIndikator());
		assertFalse(cprBorger.isTelefonNummerBeskyttelseIndikator());
		assertFalse(cprBorger.isForskerBeskyttelseIndikator());
		assertTrue(cprBorger.isFolkekirkeMedlemIndikator());
		
		CountryIdentificationCodeType nationalityCode = cprBorger.getPersonNationalityCode();
		assertEquals(CountryIdentificationSchemeType.ISO_3166_ALPHA_2, nationalityCode.getScheme());
		assertEquals("DK", nationalityCode.getValue());
	}
	
	@Test
	public void fillsOutDanishAddress() {
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
		CurrentPersonData currentPerson = new CurrentPersonData(person);
		
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
}
