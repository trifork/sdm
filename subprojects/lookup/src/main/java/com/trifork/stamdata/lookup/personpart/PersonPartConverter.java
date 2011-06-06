package com.trifork.stamdata.lookup.personpart;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.xml.datatype.XMLGregorianCalendar;

import oio.sagdok._2_0.PersonFlerRelationType;
import oio.sagdok._2_0.TidspunktType;
import oio.sagdok._2_0.TilstandVirkningType;
import oio.sagdok._2_0.UnikIdType;
import oio.sagdok._2_0.VirkningType;
import oio.sagdok.person._1_0.AdresseType;
import oio.sagdok.person._1_0.AttributListeType;
import oio.sagdok.person._1_0.CivilStatusKodeType;
import oio.sagdok.person._1_0.CivilStatusType;
import oio.sagdok.person._1_0.CprBorgerType;
import oio.sagdok.person._1_0.DanskAdresseType;
import oio.sagdok.person._1_0.EgenskabType;
import oio.sagdok.person._1_0.GroenlandAdresseType;
import oio.sagdok.person._1_0.LivStatusKodeType;
import oio.sagdok.person._1_0.LivStatusType;
import oio.sagdok.person._1_0.NavnStrukturType;
import oio.sagdok.person._1_0.PersonRelationType;
import oio.sagdok.person._1_0.PersonType;
import oio.sagdok.person._1_0.RegisterOplysningType;
import oio.sagdok.person._1_0.RegistreringType;
import oio.sagdok.person._1_0.RelationListeType;
import oio.sagdok.person._1_0.TilstandListeType;
import oio.sagdok.person._1_0.VerdenAdresseType;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trifork.stamdata.lookup.dao.CurrentPersonData;
import com.trifork.stamdata.util.DateUtils;
import com.trifork.stamdata.views.cpr.ForaeldremyndighedsRelation;
import com.trifork.stamdata.views.cpr.MorOgFaroplysninger;
import com.trifork.stamdata.views.cpr.Udrejseoplysninger;
import com.trifork.stamdata.views.cpr.UmyndiggoerelseVaergeRelation;

import dk.oio.rep.cpr_dk.xml.schemas._2008._05._01.AddressCompleteGreenlandType;
import dk.oio.rep.cpr_dk.xml.schemas._2008._05._01.ForeignAddressStructureType;
import dk.oio.rep.ebxml.xml.schemas.dkcc._2003._02._13.CountryIdentificationCodeType;
import dk.oio.rep.ebxml.xml.schemas.dkcc._2003._02._13.CountryIdentificationSchemeType;
import dk.oio.rep.ebxml.xml.schemas.dkcc._2006._01._23.PersonGenderCodeType;
import dk.oio.rep.itst_dk.xml.schemas._2006._01._17.PersonNameStructureType;
import dk.oio.rep.xkom_dk.xml.schemas._2005._03._15.AddressAccessType;
import dk.oio.rep.xkom_dk.xml.schemas._2006._01._06.AddressCompleteType;
import dk.oio.rep.xkom_dk.xml.schemas._2006._01._06.AddressPostalType;

public class PersonPartConverter {
	private static final String URN_NAMESPACE_CPR = "CPR";
	private static final Logger logger = LoggerFactory.getLogger(PersonPartConverter.class);

	public PersonType convert(CurrentPersonData person) {
		PersonType result = new PersonType();
		result.setUUID(cprToUuid(person.getCprNumber()).toString());
		result.getRegistrering().add(createRegistreringType(person));
		return result;
	}

	private RegistreringType createRegistreringType(CurrentPersonData person) {
		RegistreringType result = new RegistreringType();
		result.setAttributListe(createAttributListeType(person));
		result.setTilstandListe(createTilstandListe(person));
		result.setRelationListe(createRelationListeType(person));
		return result;
	}

	private AttributListeType createAttributListeType(CurrentPersonData person) {
		AttributListeType result = new AttributListeType();
		result.getRegisterOplysning().add(createRegisterOplysningType(person));
		result.getEgenskab().add(createEgenskabType(person));
		return result;
	}

	private EgenskabType createEgenskabType(CurrentPersonData person) {
		EgenskabType result = new EgenskabType();
		result.setPersonGenderCode(createPersonGenderCodeType(person.getKoen()));
		result.setBirthDate(DateUtils.toXmlGregorianCalendar(person.getFoedselsdato()));
		result.setNavnStruktur(createNavnStrukturType(person));
		return result;
	}

	private NavnStrukturType createNavnStrukturType(CurrentPersonData person) {
		NavnStrukturType navnStrukturType = new NavnStrukturType();
		navnStrukturType.setPersonNameStructure(createPersonNameStructure(person));
		return navnStrukturType;
	}

	private PersonNameStructureType createPersonNameStructure(CurrentPersonData person) {
		PersonNameStructureType result = new PersonNameStructureType();
		result.setPersonGivenName(person.getFornavn());
		result.setPersonMiddleName(person.getMellemnavn());
		result.setPersonSurnameName(person.getEfternavn());
		return result;
	}

	private PersonGenderCodeType createPersonGenderCodeType(String koen) {
		if("M".equals(koen)) {
			return PersonGenderCodeType.MALE;
		}
		else if ("K".equals(koen)) {
			return PersonGenderCodeType.FEMALE;
		}
		else {
			return PersonGenderCodeType.UNSPECIFIED;
		}
	}

	private TilstandListeType createTilstandListe(CurrentPersonData person) {
		TilstandListeType result = new TilstandListeType();
		result.setCivilStatus(createCivilStatusType(person));
		result.setLivStatus(createLivStatusType(person));
		return result;
	}

	private RelationListeType createRelationListeType(CurrentPersonData person) {
		RelationListeType result = new RelationListeType();
		if (person.getVaerge() != null) {
			result.getRetligHandleevneVaergeForPersonen().add(createPersonRelationTypeForVaerge(person.getVaerge()));
		}
		for (UmyndiggoerelseVaergeRelation vaergemaal : person.getVaergemaal()) {
			result.getRetligHandleevneVaergemaalsindehaver().add(createPersonFlerRelationTypeForVaergemaal(vaergemaal));
		}
		for(String boernCpr : person.getBoernCpr()) {
			result.getBoern().add(createPersonFlerRelationTypeForCpr(boernCpr));
		}
		addParentRelations(result, person);
		addPartnerRelation(result, person);
		addForaeldremyndighedsIndehavere(result, person);
		addforaeldremyndighedBoern(result, person);
		return result;
	}
	
	private void addforaeldremyndighedBoern(RelationListeType result,
			CurrentPersonData person) {
		if(person.getForaeldreMyndighedBoern() == null) {
			return;
		}
		for(ForaeldremyndighedsRelation relation : person.getForaeldreMyndighedBoern()) {
			result.getForaeldremyndighedsboern().add(createPersonFlerRelationTypeForCpr(relation.getCpr()));
		}
	}

	private void addForaeldremyndighedsIndehavere(RelationListeType result,
			CurrentPersonData person) {
		if(person.getForaeldreMyndighedsIndehavere() == null) {
			return;
		}
		for(ForaeldremyndighedsRelation relation : person.getForaeldreMyndighedsIndehavere()) {
			if(relation.typeKode.equals("0003")) { 
				// mor
				if(person.getMorOplysninger() == null) {
					logger.error("There was a ForaeldremyndighedsRelation with type 0003 (mother), but there is no MorOgFaroplysninger, cpr={}", person.getCprNumber());
					continue;
				}
				if(person.getMorOplysninger().foraeldercpr == null) {
					logger.error("There was a ForaeldremyndighedsRelation with type 0003 (mother), but the MorOgFaroplysninger record had no foraeldercpr, cpr={}", person.getCprNumber());
					continue;
				}
				result.getForaeldremyndighedsindehaver().add(createPersonRelationTypeForCpr(person.getMorOplysninger().foraeldercpr, null, null));
			}
			else if(relation.typeKode.equals("0004")) {
				// far
				if(person.getFarOplysninger() == null) {
					logger.error("There was a ForaeldremyndighedsRelation with type 0004 (father), but there is no MorOgFaroplysninger, cpr={}", person.getCprNumber());
					continue;
				}
				if(person.getFarOplysninger().foraeldercpr == null) {
					logger.error("There was a ForaeldremyndighedsRelation with type 0004 (father), but the MorOgFaroplysninger record had no foraeldercpr, cpr={}", person.getCprNumber());
					continue;
				}
				result.getForaeldremyndighedsindehaver().add(createPersonRelationTypeForCpr(person.getFarOplysninger().foraeldercpr, null, null));
			}
			else if(relation.typeKode.equals("0005") || relation.typeKode.equals("0006")) {
				// andre foraeldremyndighedsindehavere
				if(relation.relationCpr == null) {
					logger.error("ForaeldremyndighedsRelation had type {} but did not have relationCpr, cpr={}", relation.typeKode, person.getCprNumber());
					continue;
				}
				result.getForaeldremyndighedsindehaver().add(createPersonRelationTypeForCpr(relation.relationCpr, null, null));
			}
			else {
				logger.error("Ukendt foraeldremyndigheds-typekode {}, cpr={}", relation.typeKode, person.getCprNumber());
			}
		}
	}

	private void addParentRelations(RelationListeType result,
			CurrentPersonData person) {
		addFatherRelation(result, person.getFarOplysninger());
		addMotherRelation(result, person.getMorOplysninger());
	}

	private void addMotherRelation(RelationListeType result,
			MorOgFaroplysninger morOplysninger) {
		if(morOplysninger == null) {
			return;
		}
		if(morOplysninger.foraeldercpr == null) {
			return;
		}
		result.getModer().add(createPersonRelationTypeForCpr(morOplysninger.foraeldercpr, morOplysninger.getValidFrom(), null));
	}

	private void addFatherRelation(RelationListeType result,
			MorOgFaroplysninger farOplysninger) {
		if(farOplysninger == null) {
			return;
		}
		if(farOplysninger.foraeldercpr == null) {
			return;
		}
		result.getFader().add(createPersonRelationTypeForCpr(farOplysninger.foraeldercpr, farOplysninger.getValidFrom(), null));
	}

	private void addPartnerRelation(RelationListeType result, CurrentPersonData person) {
		if(person.getCivilstandskode() == null) {
			return;
		}
		if(!person.getCivilstandskode().equals("G") && !person.getCivilstandskode().equals("P")) {
			return;
		}
		PersonRelationType partnerRelation = createPersonRelationTypeForCpr(person.getAegtefaelleCpr(), person.getCivilstand().getValidFrom(), person.getCivilstand().getValidTo());
		if(person.getCivilstandskode().equals("G")) {
			result.getAegtefaelle().add(partnerRelation);
		}
		else {
			result.getRegistreretPartner().add(partnerRelation);
		}
	}

	private PersonRelationType createPersonRelationTypeForCpr(
			String cpr, Date validFrom, Date validTo) {
		PersonRelationType result = new PersonRelationType();
		result.setReferenceID(createUnikIdType(URN_NAMESPACE_CPR, cpr));
		result.setVirkning(createVirkningType(validFrom, validTo));
		return result;
	}

	private PersonFlerRelationType createPersonFlerRelationTypeForCpr(
			String cpr) {
		PersonFlerRelationType result = new PersonFlerRelationType();
		result.setReferenceID(createUnikIdType(URN_NAMESPACE_CPR, cpr));
		return result;
	}

	private PersonRelationType createPersonRelationTypeForVaerge(UmyndiggoerelseVaergeRelation vaerge) {
		PersonRelationType result = new PersonRelationType();
		result.setReferenceID(createUnikIdType(URN_NAMESPACE_CPR, vaerge.relationCpr));
		result.setVirkning(createVirkningType(vaerge.getValidFrom(), vaerge.getValidTo()));
		result.setCommentText(joinLines(vaerge.RelationsTekst1, vaerge.RelationsTekst2, vaerge.RelationsTekst3, vaerge.RelationsTekst4, vaerge.RelationsTekst5));
		return result;
	}
	
	private PersonFlerRelationType createPersonFlerRelationTypeForVaergemaal(UmyndiggoerelseVaergeRelation vaergemaal) {
		PersonFlerRelationType result = new PersonFlerRelationType();
		result.setReferenceID(createUnikIdType(URN_NAMESPACE_CPR, vaergemaal.getCpr()));
		result.setVirkning(createVirkningType(vaergemaal.getValidFrom(), vaergemaal.getValidTo()));
		result.setCommentText(joinLines(vaergemaal.RelationsTekst1, vaergemaal.RelationsTekst2, vaergemaal.RelationsTekst3, vaergemaal.RelationsTekst4, vaergemaal.RelationsTekst5));
		return result;
	}

	private String joinLines(String... lines) {
		return StringUtils.join(lines, "\n");
	}

	private TilstandVirkningType createTilstandVirkningType(Date from) {
		TilstandVirkningType result = new TilstandVirkningType();
		result.setFraTidspunkt(createTidspunktType(from));
		result.setAktoerRef(createAktoerRefId());
		return result;
		
	}
	
	private VirkningType createVirkningType(Date from, Date to) {
		VirkningType result = new VirkningType();
		if (from != null) {
			result.setFraTidspunkt(createTidspunktType(from));
		}
		if (to != null) {
			result.setTilTidspunkt(createTidspunktType(to));
		}
		result.setAktoerRef(createAktoerRefId()); // Field is required
		return result;
	}

	private UnikIdType createAktoerRefId() {
		return createUnikIdType("Aktoer", "Importer");
	}

	private UnikIdType createUnikIdType(String nameSpace, String cpr) {
		UnikIdType result = new UnikIdType();
		result.setURNIdentifikator("URN:" + nameSpace + ":" + cpr);
		return result;
	}

	private TidspunktType createTidspunktType(Date time) {
		XMLGregorianCalendar xmlGregorianCalendar = DateUtils.toXmlGregorianCalendar(time);

		TidspunktType result = new TidspunktType();
		result.setTidsstempelDatoTid(xmlGregorianCalendar);
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

	private LivStatusType createLivStatusType(CurrentPersonData person) {
		LivStatusKodeType livStatus;
		if("70".equals(person.getStatus())) {
			livStatus = LivStatusKodeType.FORSVUNDET;
		}
		else if ("90".equals(person.getStatus())) {
			livStatus = LivStatusKodeType.DOED;
		}
		else if ("80".equals(person.getStatus())) {
			// udrejst - så er det jo ikke til at vide med sikkerhed.
			// men levende er vist bedste bud
			livStatus = LivStatusKodeType.FOEDT;
		}
		else {
			// alle andre mapper til FOEDT - herunder ændrede/annullerede/slettede numre
			livStatus = LivStatusKodeType.FOEDT;
		}
		LivStatusType livStatusType = new LivStatusType();
		livStatusType.setLivStatusKode(livStatus);
		return livStatusType;
	}

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
		Date now = new Date();
		result.setNavneAdresseBeskyttelseIndikator(
				(person.getNavnebeskyttelsesstartdato() != null && person.getNavnebeskyttelsesstartdato().before(now)) &&
				(person.getNavnebeskyttelsesslettedato() == null || person.getNavnebeskyttelsesslettedato().after(now)));

		// TODO: Bare de mest gængse værdier p.t. Skal selvfølgelig hentes rigtigt.
		result.setPersonNummerGyldighedStatusIndikator(true);
		result.setTelefonNummerBeskyttelseIndikator(false);
		result.setForskerBeskyttelseIndikator(false);
		return result;
	}

	private AdresseType createAdresseType(CurrentPersonData person) {
		AdresseType result = new AdresseType();
		// check om personen har status 80, udrejst.
		if(!"80".equals(person.getStatus())) {
			if(person.getPostnummer() == null) {
				return null;
			}
			int postnummer = person.getPostnummer().intValue();
			// check om postnummeret er groenlandsk og lav en groendlandsk adresse hvis det er tilfaeldet
			// postnummer 2412 er julemandens postdistrikt, hvis han skulle gå hen og få et CPR-nummer :)
			if((postnummer >= 3000 && postnummer < 4000) || postnummer == 2412) {
				result.setGroenlandAdresse(createGroendlandAdresseType(person));
			}
			else {
				result.setDanskAdresse(createDanskAdresseType(person));
			}
		}
		else {
			result.setVerdenAdresse(createVerdenAdresse(person.getUdrejseoplysninger()));
		}
		return result;
	}

	private GroenlandAdresseType createGroendlandAdresseType(
			CurrentPersonData person) {
		GroenlandAdresseType result = new GroenlandAdresseType();
		result.setAddressCompleteGreenland(createAddressCompleteGreendlandType(person));
		return result;
	}

	private AddressCompleteGreenlandType createAddressCompleteGreendlandType(
			CurrentPersonData person) {
		AddressCompleteGreenlandType result = new AddressCompleteGreenlandType();
		result.setMailDeliverySublocationIdentifier(contentsOrNull(person.getLokalitet()));
		result.setStreetName(person.getVejnavn());
		result.setStreetNameForAddressingName(contentsOrNull(person.getVejnavn()));
		result.setStreetBuildingIdentifier(createStreetBuildingIdentifier(person.getHusnummer()));
		result.setFloorIdentifier(contentsOrNull(person.getEtage()));
		result.setSuiteIdentifier(contentsOrNull(person.getSidedoernummer()));
		result.setDistrictSubdivisionIdentifier(contentsOrNull(person.getBynavn()));
		result.setPostCodeIdentifier("" + person.getPostnummer());
		result.setDistrictName(person.getPostdistrikt());
		result.setGreenlandBuildingIdentifier(person.getBygningsnummer());
		result.setCountryIdentificationCode(createCountryIdentificationCodeType(CountryIdentificationSchemeType.ISO_3166_ALPHA_2, "GL"));
		return result;
	}

	private VerdenAdresseType createVerdenAdresse(
			Udrejseoplysninger udrejseoplysninger) {
		if(udrejseoplysninger == null) {
			return null;
		}
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
		result.setAddressAccess(createAddressAccessType(person));
		result.setAddressPostal(createAddressPostalType(person));
		return result;
	}

	private AddressAccessType createAddressAccessType(CurrentPersonData person) {
		AddressAccessType result = new AddressAccessType();
		result.setMunicipalityCode(person.getKommuneKode());
		result.setStreetCode(person.getVejKode());
		result.setStreetBuildingIdentifier(person.getHusnummer());
		return result;
	}

	private AddressPostalType createAddressPostalType(CurrentPersonData person) {
		AddressPostalType result = new AddressPostalType();
		result.setMailDeliverySublocationIdentifier(contentsOrNull(person.getLokalitet()));
		result.setStreetName(person.getVejnavn());
		result.setStreetNameForAddressingName(contentsOrNull(person.getVejnavn()));
		result.setStreetBuildingIdentifier(createStreetBuildingIdentifier(person.getHusnummer()));
		result.setFloorIdentifier(contentsOrNull(person.getEtage()));
		result.setSuiteIdentifier(contentsOrNull(person.getSidedoernummer()));
		result.setDistrictSubdivisionIdentifier(contentsOrNull(person.getBynavn()));
		result.setPostCodeIdentifier("" + person.getPostnummer());
		result.setDistrictName(person.getPostdistrikt());
		result.setCountryIdentificationCode(createCountryIdentificationCodeType(CountryIdentificationSchemeType.ISO_3166_ALPHA_2, "DK"));
		return result;
	}

	String createStreetBuildingIdentifier(String cprHusnummer) {
		if(cprHusnummer == null) {
			return null;
		}
		// CPR delivers this record with whitespace padded between building number and eventual letter.
		// The OIO standard mandates that this whitespace is removed.
		return cprHusnummer.replaceAll(" ", "");
	}

	private CountryIdentificationCodeType createCountryIdentificationCodeType(CountryIdentificationSchemeType scheme, String code) {
		CountryIdentificationCodeType result = new CountryIdentificationCodeType();
		result.setScheme(scheme);
		result.setValue(code);
		return result;
	}
	
	private String contentsOrNull(String s) {
		return s == null || s.isEmpty() ? null : s;
	}
	
	private UUID cprToUuid(String cpr) {
		return UUID.nameUUIDFromBytes(cpr.getBytes(Charset.forName("ISO-8859-1")));
	}
}
