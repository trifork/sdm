package com.trifork.stamdata.lookup.rest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;

import oio.sagdok.person._1_0.PersonType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.Provider;
import com.trifork.stamdata.lookup.dao.CurrentPersonData;
import com.trifork.stamdata.lookup.dao.PersonDao;
import com.trifork.stamdata.lookup.personpart.PersonPartConverter;
import com.trifork.stamdata.lookup.validation.PersonValidator;
import com.trifork.stamdata.replication.logging.UsageLogger;
import com.trifork.stamdata.ssl.SubjectSerialNumber;
import com.trifork.stamdata.ssl.SubjectSerialNumber.Kind;
import com.trifork.stamdata.ssl.UncheckedProvider;
import com.trifork.stamdata.views.cpr.Person;

@RunWith(MockitoJUnitRunner.class)
public class PersonResourceTest {
	@Mock PersonDao personDao;
	@Mock PersonPartConverter personPartConverter;
	@Mock UsageLogger usageLogger;
	@Mock Provider<PersonValidator> personValidator;
	@Mock UncheckedProvider<SubjectSerialNumber> ssnProvider;
	SubjectSerialNumber authenticatedSsn = new SubjectSerialNumber(Kind.FOCES, "12345678", "1234");
	
	private PersonResource resource;

	@Before
	public void before() {
		when(ssnProvider.get()).thenReturn(authenticatedSsn);
		resource = new PersonResource(personDao, personPartConverter, usageLogger, ssnProvider, personValidator);
	}

	@Test
	public void usageLogsSuccessfulLookup() {
		setupSuccessfulLookup();
		resource.getPerson("1020304050");
		verify(usageLogger).log(authenticatedSsn.toString(), "lookup.cpr.person.ok", 1);
	}

	@Test
	public void usageLogsNotfound() {
		resource.getPerson("1020304050");
		verify(usageLogger).log(authenticatedSsn.toString(), "lookup.cpr.person.notfound", 1);
	}
	
	@Test
	public void looksUpPersonFromCpr() {
		PersonType personPart = setupSuccessfulLookup();

		JAXBElement<PersonType> result = (JAXBElement<PersonType>)resource.getPerson("1020304050").getEntity();
		assertEquals(personPart, result.getValue());
	}

	private PersonType setupSuccessfulLookup() {
		CurrentPersonData person = new CurrentPersonData(new Person(), null, null, null, null, null, null, null, null, null, null, null, null, null);
		PersonType personPart = new PersonType();
		when(personDao.get("1020304050")).thenReturn(person);
		when(personPartConverter.convert(person)).thenReturn(personPart);
		return personPart;
	}

	@Test
	public void givesCorrectReturnCodeWhenPersonDoesNotExist() {
		when(personDao.get("1020304050")).thenReturn(null);

		Response response = resource.getPerson("1020304050");

		assertEquals(404, response.getStatus());
	}
}
