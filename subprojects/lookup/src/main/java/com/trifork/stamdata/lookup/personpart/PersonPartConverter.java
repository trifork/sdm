package com.trifork.stamdata.lookup.personpart;

import oio.sagdok.person._1_0.AdresseType;
import oio.sagdok.person._1_0.AttributListeType;
import oio.sagdok.person._1_0.CprBorgerType;
import oio.sagdok.person._1_0.DanskAdresseType;
import oio.sagdok.person._1_0.PersonType;
import oio.sagdok.person._1_0.RegisterOplysningType;
import oio.sagdok.person._1_0.RegistreringType;

import com.trifork.stamdata.lookup.dao.CurrentPersonData;

import dk.oio.rep.ebxml.xml.schemas.dkcc._2003._02._13.CountryIdentificationCodeType;
import dk.oio.rep.ebxml.xml.schemas.dkcc._2003._02._13.CountryIdentificationSchemeType;
import dk.oio.rep.xkom_dk.xml.schemas._2006._01._06.AddressCompleteType;
import dk.oio.rep.xkom_dk.xml.schemas._2006._01._06.AddressPostalType;

public class PersonPartConverter {

	public PersonType convert(CurrentPersonData person) {
		PersonType result = new PersonType();
		result.getRegistrering().add(createRegistreringType(person));
		return result;
	}

	private RegistreringType createRegistreringType(CurrentPersonData person) {
		RegistreringType result = new RegistreringType();
		result.setAttributListe(createAttributListeType(person));
		return result;
	}

	private AttributListeType createAttributListeType(CurrentPersonData person) {
		AttributListeType result = new AttributListeType();
		result.getRegisterOplysning().add(createRegisterOplysningType(person));
		return result;
	}

	private RegisterOplysningType createRegisterOplysningType(CurrentPersonData person) {
		RegisterOplysningType result = new RegisterOplysningType();
		result.setCprBorger(createCprBorgerType(person));
		return result;
	}

	private CprBorgerType createCprBorgerType(CurrentPersonData person) {
		CprBorgerType result = new CprBorgerType();
		result.setPersonCivilRegistrationIdentifier(person.getCprNumber());
		result.setPersonNationalityCode(createCountryIdentificationCodeType(person));
		result.setFolkeregisterAdresse(createAdresseType(person));
		
		// TODO: Bare de mest gængse værdier p.t. Skal selvfølgelig hentes rigtigt.
		result.setPersonNummerGyldighedStatusIndikator(true);
		result.setNavneAdresseBeskyttelseIndikator(false);
		result.setTelefonNummerBeskyttelseIndikator(false);
		result.setForskerBeskyttelseIndikator(false);
		result.setFolkekirkeMedlemIndikator(true);
		return result;
	}

	private AdresseType createAdresseType(CurrentPersonData person) {
		AdresseType result = new AdresseType();
		result.setDanskAdresse(createDanskAdresseType(person));
		return result;
	}

	private DanskAdresseType createDanskAdresseType(CurrentPersonData person) {
		DanskAdresseType result = new DanskAdresseType();
		result.setAddressComplete(createAddressCompleteType(person));
		return result;
	}

	private AddressCompleteType createAddressCompleteType(CurrentPersonData person) {
		AddressCompleteType result = new AddressCompleteType();
		result.setAddressPostal(createAddressPostalType(person));
		return result;
	}

	private AddressPostalType createAddressPostalType(CurrentPersonData person) {
		AddressPostalType result = new AddressPostalType();
		result.setMailDeliverySublocationIdentifier(contentsOrNull(person.getLokalitet()));
		result.setStreetName(person.getVejnavn());
		result.setStreetNameForAddressingName(contentsOrNull(person.getVejnavn()));
		result.setStreetBuildingIdentifier(createStreetBuildingIdentifier(person));
		result.setFloorIdentifier(contentsOrNull(person.getEtage()));
		result.setSuiteIdentifier(contentsOrNull(person.getSidedoernummer()));
		result.setDistrictSubdivisionIdentifier(contentsOrNull(person.getBynavn()));
		result.setPostCodeIdentifier("" + person.getPostnummer());
		result.setDistrictName(person.getPostdistrikt());
		result.setCountryIdentificationCode(createCountryIdentificationCodeType(person));
		return result;
	}

	private String createStreetBuildingIdentifier(CurrentPersonData person) {
		if (person.getBygningsnummer() != null && !person.getBygningsnummer().isEmpty()) {
			return person.getHusnummer() + ", " + person.getBygningsnummer();
		}
		return person.getHusnummer();
	}

	private CountryIdentificationCodeType createCountryIdentificationCodeType(CurrentPersonData person) {
		CountryIdentificationCodeType result = new CountryIdentificationCodeType();

		// TODO: Bare de mest gængse værdier p.t. Skal selvfølgelig hentes rigtigt.
		result.setScheme(CountryIdentificationSchemeType.ISO_3166_ALPHA_2);
		result.setValue("DK");
		return result;
	}
	
	private String contentsOrNull(String s) {
		return s == null || s.isEmpty() ? null : s;
	}
}
