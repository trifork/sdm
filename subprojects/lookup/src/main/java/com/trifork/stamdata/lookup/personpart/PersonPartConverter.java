package com.trifork.stamdata.lookup.personpart;

import oio.sagdok.person._1_0.AttributListeType;
import oio.sagdok.person._1_0.CprBorgerType;
import oio.sagdok.person._1_0.PersonType;
import oio.sagdok.person._1_0.RegisterOplysningType;
import oio.sagdok.person._1_0.RegistreringType;

import com.trifork.stamdata.lookup.dao.CurrentPersonData;

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
		return result;
	}
}
