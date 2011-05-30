package com.trifork.stamdata.lookup.personpart;

import java.util.HashMap;
import java.util.Map;

import oio.sagdok.person._1_0.AdresseType;
import oio.sagdok.person._1_0.AttributListeType;
import oio.sagdok.person._1_0.CivilStatusKodeType;
import oio.sagdok.person._1_0.CivilStatusType;
import oio.sagdok.person._1_0.CprBorgerType;
import oio.sagdok.person._1_0.DanskAdresseType;
import oio.sagdok.person._1_0.PersonType;
import oio.sagdok.person._1_0.RegisterOplysningType;
import oio.sagdok.person._1_0.RegistreringType;
import oio.sagdok.person._1_0.TilstandListeType;
import oio.sagdok.person._1_0.VerdenAdresseType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trifork.stamdata.lookup.dao.CurrentPersonData;
import com.trifork.stamdata.views.cpr.Udrejseoplysninger;

import dk.oio.rep.cpr_dk.xml.schemas._2008._05._01.ForeignAddressStructureType;
import dk.oio.rep.ebxml.xml.schemas.dkcc._2003._02._13.CountryIdentificationCodeType;
import dk.oio.rep.ebxml.xml.schemas.dkcc._2003._02._13.CountryIdentificationSchemeType;
import dk.oio.rep.xkom_dk.xml.schemas._2006._01._06.AddressCompleteType;
import dk.oio.rep.xkom_dk.xml.schemas._2006._01._06.AddressPostalType;

public class PersonPartConverter {
	Logger logger = LoggerFactory.getLogger(PersonPartConverter.class);
	public PersonType convert(CurrentPersonData person) {
		PersonType result = new PersonType();
		result.getRegistrering().add(createRegistreringType(person));
		return result;
	}

	private RegistreringType createRegistreringType(CurrentPersonData person) {
		RegistreringType result = new RegistreringType();
		result.setAttributListe(createAttributListeType(person));
		result.setTilstandListe(createTilstandListe(person));
		return result;
	}

	private TilstandListeType createTilstandListe(CurrentPersonData person) {
		TilstandListeType result = new TilstandListeType();
		result.setCivilStatus(createCivilStatusType(person));
		return result;
	}
	
	@SuppressWarnings("serial")
	private static final Map<String, CivilStatusKodeType> civilStandMap = new HashMap<String, CivilStatusKodeType>() {{
		put("U", CivilStatusKodeType.UGIFT);
		put("G", CivilStatusKodeType.GIFT);
		put("F", CivilStatusKodeType.SKILT);
		put("E", CivilStatusKodeType.ENKE);
		put("P", CivilStatusKodeType.REGISTRERET_PARTNER);
		put("O", CivilStatusKodeType.OPHAEVET_PARTNERSKAB);
		put("L", CivilStatusKodeType.LAENGSTLEVENDE);
	}};

	/*package*/ CivilStatusType createCivilStatusType(CurrentPersonData person) {
		String civilstandskode = person.getCivilstandskode();
		if(civilstandskode == null || civilstandskode.isEmpty()) {
			return null;
		}
		CivilStatusKodeType kode = civilStandMap.get(civilstandskode);
		if(kode == null) {
			logger.error("Ukendt civilstandskode: {}", civilstandskode);
			return null;
		}
		// Der er ikke nogen civilstatuskode for separerede i CPR
		// vi udfylder feltet hvis der er en separationsdato i civilstandsrecorden og personen enten er gift eller separeret
		// Spørgsmålet er om dette er korrekt i alle tilfælde, eksempelvis hvis en separation annuleres?
		if(person.getSeparationsdato() != null && (civilstandskode.equals("G")|| civilstandskode.equals("P"))) {
			kode = CivilStatusKodeType.SEPARERET;
		}
		else {
			logger.warn("Separationsdato angivet, men personen er ikke gift eller registreret partner. cpr={}", person.getCprNumber());
		}
		CivilStatusType result = new CivilStatusType();
		result.setCivilStatusKode(kode);
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
		result.setPersonNationalityCode(createCountryIdentificationCodeType(CountryIdentificationSchemeType.IMK, person.getStatsborgerskab()));
		result.setFolkeregisterAdresse(createAdresseType(person));
		
		result.setFolkekirkeMedlemIndikator(person.getMedlemAfFolkekirken());

		// TODO: Bare de mest gængse værdier p.t. Skal selvfølgelig hentes rigtigt.
		result.setPersonNummerGyldighedStatusIndikator(true);
		result.setNavneAdresseBeskyttelseIndikator(false);
		result.setTelefonNummerBeskyttelseIndikator(false);
		result.setForskerBeskyttelseIndikator(false);
		return result;
	}

	private AdresseType createAdresseType(CurrentPersonData person) {
		AdresseType result = new AdresseType();
		if(person.getUdrejseoplysninger() == null) {
			result.setDanskAdresse(createDanskAdresseType(person));
		}
		else {
			result.setVerdenAdresse(createVerdenAdresse(person.getUdrejseoplysninger()));
		}
		return result;
	}

	private VerdenAdresseType createVerdenAdresse(
			Udrejseoplysninger udrejseoplysninger) {
		VerdenAdresseType result = new VerdenAdresseType();
		ForeignAddressStructureType address = new ForeignAddressStructureType();
		result.setForeignAddressStructure(address);
		address.setCountryIdentificationCode(createCountryIdentificationCodeType(CountryIdentificationSchemeType.IMK, udrejseoplysninger.udrejseLandekode));
		address.setPostalAddressFirstLineText(udrejseoplysninger.udlandsadresse1);
		address.setPostalAddressSecondLineText(udrejseoplysninger.udlandsadresse2);
		address.setPostalAddressThirdLineText(udrejseoplysninger.udlandsadresse3);
		address.setPostalAddressFourthLineText(udrejseoplysninger.udlandsadresse4);
		address.setPostalAddressFifthLineText(udrejseoplysninger.udlandsadresse5);
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
		// FIXME udfyld korrekt land
		result.setCountryIdentificationCode(createCountryIdentificationCodeType(CountryIdentificationSchemeType.ISO_3166_ALPHA_2, "DK"));
		return result;
	}

	private String createStreetBuildingIdentifier(CurrentPersonData person) {
		if (person.getBygningsnummer() != null && !person.getBygningsnummer().isEmpty()) {
			return person.getHusnummer() + ", " + person.getBygningsnummer();
		}
		return person.getHusnummer();
	}

	private CountryIdentificationCodeType createCountryIdentificationCodeType(CountryIdentificationSchemeType scheme, String code) {
		CountryIdentificationCodeType result = new CountryIdentificationCodeType();

		// TODO: Bare de mest gængse værdier p.t. Skal selvfølgelig hentes rigtigt.
		result.setScheme(scheme);
		result.setValue(code);
		return result;
	}
	
	private String contentsOrNull(String s) {
		return s == null || s.isEmpty() ? null : s;
	}
}
