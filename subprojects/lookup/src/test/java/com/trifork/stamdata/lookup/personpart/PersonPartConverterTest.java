package com.trifork.stamdata.lookup.personpart;

import static org.junit.Assert.*;
import oio.sagdok.person._1_0.PersonType;

import org.junit.Before;
import org.junit.Test;

import com.trifork.stamdata.lookup.dao.CurrentPersonData;
import com.trifork.stamdata.views.cpr.Person;


public class PersonPartConverterTest {
	private PersonPartConverter converter;

	@Before
	public void before() {
		converter = new PersonPartConverter();
	}
	
	@Test
	public void fillsOutCprNumber() {
		Person person = new Person();
		person.cpr = "1020304050";
		CurrentPersonData currentPerson = new CurrentPersonData(person);
		
		PersonType personType = converter.convert(currentPerson);
		assertEquals("1020304050", personType.getRegistrering().get(0).getAttributListe().getRegisterOplysning().get(0).getCprBorger().getPersonCivilRegistrationIdentifier());
	}
}
