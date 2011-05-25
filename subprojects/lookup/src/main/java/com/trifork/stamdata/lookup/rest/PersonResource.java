package com.trifork.stamdata.lookup.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.xml.bind.JAXBElement;

import oio.sagdok.person._1_0.PersonType;

import com.trifork.stamdata.lookup.dao.CurrentPersonData;
import com.trifork.stamdata.lookup.dao.PersonDao;
import com.trifork.stamdata.lookup.personpart.PersonPartConverter;

@Path("person/{cpr}")
public class PersonResource {
	private final PersonDao personDao;
	private final PersonPartConverter personPartConverter;

	@Inject
	public PersonResource(PersonDao personDao, PersonPartConverter personPartConverter) {
		this.personDao = personDao;
		this.personPartConverter = personPartConverter;
	}
	
	@GET
	@Produces("text/xml")
	public JAXBElement<PersonType> getPerson(@PathParam("cpr") String cpr) {
		CurrentPersonData person = personDao.get(cpr);
		PersonType personPart = personPartConverter.convert(person);
		return new oio.sagdok.person._1_0.ObjectFactory().createPerson(personPart);
	}
}
