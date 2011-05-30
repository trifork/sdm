package com.trifork.stamdata.lookup.rest;

import javax.xml.bind.JAXBElement;

import oio.sagdok.person._1_0.PersonType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.trifork.stamdata.lookup.dao.CurrentPersonData;
import com.trifork.stamdata.lookup.dao.PersonDao;
import com.trifork.stamdata.lookup.personpart.PersonPartConverter;
import com.trifork.stamdata.views.cpr.Person;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PersonResourceTest {
	@Mock PersonDao personDao;
	@Mock PersonPartConverter personPartConverter;
	
	private PersonResource resource;

	@Before
	public void before() {
		resource = new PersonResource(personDao, personPartConverter);
	}
	
	@Test
	public void looksUpPersonFromCpr() {
		CurrentPersonData person = new CurrentPersonData(new Person(), null, null, null);
		PersonType personPart = new PersonType();
		when(personDao.get("1020304050")).thenReturn(person);
		when(personPartConverter.convert(person)).thenReturn(personPart);
		
		JAXBElement<PersonType> result = resource.getPerson("1020304050");
		assertEquals(personPart, result.getValue());
	}
}
