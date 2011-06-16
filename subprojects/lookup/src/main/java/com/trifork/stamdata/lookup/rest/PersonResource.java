package com.trifork.stamdata.lookup.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import oio.sagdok.person._1_0.PersonType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.google.inject.Provider;
import com.trifork.stamdata.lookup.dao.CurrentPersonData;
import com.trifork.stamdata.lookup.dao.PersonDao;
import com.trifork.stamdata.lookup.personpart.PersonPartConverter;
import com.trifork.stamdata.lookup.validation.PersonValidator;
import com.trifork.stamdata.replication.logging.UsageLogger;
import com.trifork.stamdata.ssl.UncheckedProvider;
import com.trifork.stamdata.ssl.annotations.AuthenticatedSSN;

@Path("person")
public class PersonResource {
	private static final Logger logger = LoggerFactory.getLogger(PersonResource.class);
	private final PersonDao personDao;
	private final PersonPartConverter personPartConverter;
	private final UsageLogger usageLogger;
	private final Provider<PersonValidator> personValidator;
	private final UncheckedProvider<String> clientSsn;

	@Inject
	public PersonResource(PersonDao personDao,
			PersonPartConverter personPartConverter, UsageLogger usageLogger,
			@AuthenticatedSSN UncheckedProvider<String> clientSsn, Provider<PersonValidator> personValidator) {
		this.personDao = personDao;
		this.personPartConverter = personPartConverter;
		this.usageLogger = usageLogger;
		this.clientSsn = clientSsn;
		this.personValidator = personValidator;
	}

	@GET
	@Path("{cpr}")
	@Produces("text/xml")
	public Response getPerson(@PathParam("cpr") String cpr) {
		CurrentPersonData person = personDao.get(cpr);
		if (person == null) {
			usageLogger.log(clientSsn.get(), "lookup.cpr.person.notfound", 1);
			logger.info("Opslag på ikke-eksisterende cpr-nummer. cpr={}, subject-serialnumber='{}'", cpr, clientSsn);
            return buildNotFoundResponse(cpr);
		}
		PersonType personPart = personPartConverter.convert(person);
		usageLogger.log(clientSsn.get(), "lookup.cpr.person.ok", 1);
		logger.info("Opslag på cpr={}, subject-serialnumber='{}'", cpr, clientSsn);
		return Response.ok(new oio.sagdok.person._1_0.ObjectFactory().createPerson(personPart)).build();
	}

    private Response buildNotFoundResponse(String cpr) {
		return Response
				.status(Response.Status.NOT_FOUND)
				.type(MediaType.TEXT_HTML)
				.entity("<h3>Person med CPR " + cpr	+ " findes ikke i systemet</h3>")
				.build();
    }

    @GET
	@Path("{cpr}/validate")
	@Produces("text/plain; charset=utf-8")
	public String getPersonValidationErrors(@PathParam("cpr") String cpr) throws JAXBException, SAXException  {
		CurrentPersonData person = personDao.get(cpr);
		PersonType personPart = personPartConverter.convert(person);
		final StringBuilder result = new StringBuilder();
		ErrorHandler errorHandler = new ErrorHandler() {
			@Override
			public void error(SAXParseException exception) throws SAXException {
				result.append("ERROR: ").append(exception.getMessage()).append('\n');
			}

			@Override
			public void fatalError(SAXParseException exception)
					throws SAXException {
				result.append("FATAL ERROR: ").append(exception.getMessage()).append('\n');
			}

			@Override
			public void warning(SAXParseException exception)
					throws SAXException {
				result.append("WARNING: ").append(exception.getMessage()).append('\n');
			}

		};
		try {
			personValidator.get().validate(new oio.sagdok.person._1_0.ObjectFactory().createPerson(personPart), errorHandler);
		}
		catch(Exception e) {
			result.append("stopping validation due to fatal error: " + e.getMessage() + "\n");
			logger.info("Validation failed", e);
		}
		if(result.toString().isEmpty()) {
			return "NO ERRORS";
		}
		return result.toString();
	}
}
