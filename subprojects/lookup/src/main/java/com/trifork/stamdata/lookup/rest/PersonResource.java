package com.trifork.stamdata.lookup.rest;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;

import oio.sagdok.person._1_0.PersonType;

import com.trifork.stamdata.lookup.dao.CurrentPersonData;
import com.trifork.stamdata.lookup.dao.PersonDao;
import com.trifork.stamdata.lookup.personpart.PersonPartConverter;

@Path("person")
public class PersonResource {
	private final PersonDao personDao;
	private final PersonPartConverter personPartConverter;

	@Inject
	public PersonResource(PersonDao personDao, PersonPartConverter personPartConverter) {
		this.personDao = personDao;
		this.personPartConverter = personPartConverter;
	}
	
	@GET
	@Path("{cpr}")
	@Produces("text/xml")
	public Response getPerson(@PathParam("cpr") String cpr) {
        CurrentPersonData person = personDao.get(cpr);
        if (person == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
		PersonType personPart = personPartConverter.convert(person);
		return Response.ok(new oio.sagdok.person._1_0.ObjectFactory().createPerson(personPart)).build() ;
	}

}
