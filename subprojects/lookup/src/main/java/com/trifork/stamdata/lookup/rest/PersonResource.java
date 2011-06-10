package com.trifork.stamdata.lookup.rest;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;

import oio.sagdok.person._1_0.PersonType;

import com.trifork.stamdata.lookup.dao.CurrentPersonData;
import com.trifork.stamdata.lookup.dao.PersonDao;
import com.trifork.stamdata.lookup.personpart.PersonPartConverter;
import com.trifork.stamdata.replication.logging.UsageLogger;
import com.trifork.stamdata.ssl.annotations.AuthenticatedSSN;

@Path("person")
public class PersonResource {
	private final PersonDao personDao;
	private final PersonPartConverter personPartConverter;
	private final UsageLogger usageLogger;
	private final String clientSsn;

	@Inject
	public PersonResource(PersonDao personDao, PersonPartConverter personPartConverter, UsageLogger usageLogger, @AuthenticatedSSN String clientSsn) {
		this.personDao = personDao;
		this.personPartConverter = personPartConverter;
		this.usageLogger = usageLogger;
		this.clientSsn = clientSsn;
	}
	
	@GET
	@Path("{cpr}")
	@Produces("text/xml")
	public Response getPerson(@PathParam("cpr") String cpr) {
        CurrentPersonData person = personDao.get(cpr);
        if (person == null) {
		usageLogger.log(clientSsn, "lookup.cpr.person.notfound", 1);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
		PersonType personPart = personPartConverter.convert(person);
		usageLogger.log(clientSsn, "lookup.cpr.person.ok", 1);
		return Response.ok(new oio.sagdok.person._1_0.ObjectFactory().createPerson(personPart)).build() ;
	}

}
