package com.trifork.stamdata.lookup.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oio.sagdok.person._1_0.PersonType;

import com.trifork.stamdata.lookup.dao.CurrentPersonData;
import com.trifork.stamdata.lookup.dao.PersonDao;
import com.trifork.stamdata.lookup.personpart.PersonPartConverter;
import com.trifork.stamdata.replication.logging.UsageLogger;
import com.trifork.stamdata.ssl.annotations.AuthenticatedSSN;

@Path("person")
public class PersonResource {
	private static final Logger logger = LoggerFactory.getLogger(PersonResource.class);
	private final PersonDao personDao;
	private final PersonPartConverter personPartConverter;
	private final UsageLogger usageLogger;
	private final String clientSsn;

	@Inject
	public PersonResource(PersonDao personDao,
			PersonPartConverter personPartConverter, UsageLogger usageLogger,
			@AuthenticatedSSN String clientSsn) {
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
			logger.info("Opslag på ikke-eksisterende cpr-nummer. cpr={}, subject-serialnumber='{}'", cpr, clientSsn);
            Response.ResponseBuilder builder = Response.status(Response.Status.NOT_FOUND);
            builder.type(MediaType.TEXT_HTML);
            builder.entity("<h3>Person med CPR " + cpr + " findes ikke i systemet</h3>");
            return builder.build();
		}
		PersonType personPart = personPartConverter.convert(person);
		usageLogger.log(clientSsn, "lookup.cpr.person.ok", 1);
		logger.info("Opslag på cpr={}, subject-serialnumber='{}'", cpr, clientSsn);
		return Response.ok(new oio.sagdok.person._1_0.ObjectFactory().createPerson(personPart)).build();
	}
}
