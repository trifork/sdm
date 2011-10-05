package dk.nsi.stamdata.cpr.pvit;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.jws.WebService;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.sun.xml.ws.developer.SchemaValidation;
import com.trifork.stamdata.Fetcher;
import com.trifork.stamdata.models.cpr.Person;

import dk.nsi.stamdata.cpr.PersonMapper;
import dk.nsi.stamdata.cpr.PersonMapper.ServiceProtectionLevel;
import dk.nsi.stamdata.cpr.SoapUtils;
import dk.nsi.stamdata.cpr.jaxws.GuiceInstanceResolver.GuiceWebservice;
import dk.nsi.stamdata.cpr.medcom.FaultMessages;
import dk.nsi.stamdata.cpr.ws.CivilRegistrationNumberListPersonQueryType;
import dk.nsi.stamdata.cpr.ws.DGWSFault;
import dk.nsi.stamdata.cpr.ws.Header;
import dk.nsi.stamdata.cpr.ws.NamePersonQueryType;
import dk.nsi.stamdata.cpr.ws.PersonInformationStructureType;
import dk.nsi.stamdata.cpr.ws.PersonLookupRequestType;
import dk.nsi.stamdata.cpr.ws.PersonLookupResponseType;
import dk.nsi.stamdata.cpr.ws.Security;
import dk.nsi.stamdata.cpr.ws.StamdataPersonLookup;
import dk.sosi.seal.model.SystemIDCard;
import dk.sosi.seal.model.constants.FaultCodeValues;


@SchemaValidation
@GuiceWebservice
@WebService(serviceName = "StamdataPersonLookup", endpointInterface = "dk.nsi.stamdata.cpr.ws.StamdataPersonLookup")
public class StamdataPersonLookupImpl implements StamdataPersonLookup
{
	private final static Logger logger = LoggerFactory.getLogger(StamdataPersonLookupImpl.class);

	private final PersonMapper personMapper;
	private final Fetcher fetcher;

	private final String clientCVR;


	@Inject
	StamdataPersonLookupImpl(SystemIDCard idCard, Fetcher fetcher, PersonMapper personMapper)
	{
		this.clientCVR = idCard.getSystemInfo().getCareProvider().getID();
		this.personMapper = personMapper;
		this.fetcher = fetcher;
	}


	@Override
	public PersonLookupResponseType getPersonDetails(Holder<Security> wsseHeader, Holder<Header> medcomHeader, PersonLookupRequestType request) throws DGWSFault
	{
		verifyExactlyOneQueryParameterIsNonNull(wsseHeader, medcomHeader, request);

		// TODO: This should be done in the filter
		// This has to be done according to the DGWS specifications
		SoapUtils.setHeadersToOutgoing(wsseHeader, medcomHeader);

		try
		{
			if (request.getCivilRegistrationNumberPersonQuery() != null)
			{
				return answerCprRequest(request.getCivilRegistrationNumberPersonQuery());
			}

			if (request.getCivilRegistrationNumberListPersonQuery() != null)
			{
				return answerCivilRegistrationNumberListPersonRequest(request.getCivilRegistrationNumberListPersonQuery());
			}

			if (request.getBirthDatePersonQuery() != null)
			{
				return answerBirthDatePersonRequest(request.getBirthDatePersonQuery());
			}

			if (request.getNamePersonQuery() != null)
			{
				return answerNamePersonRequest(request.getNamePersonQuery());
			}
		}
		catch (SQLException e)
		{
			logger.error(e.getMessage(), e); // TODO: Log medcom flow id
			throw SoapUtils.newDGWSFault(wsseHeader, medcomHeader, FaultMessages.INTERNAL_SERVER_ERROR, FaultCodeValues.PROCESSING_PROBLEM);
		}
		catch (DatatypeConfigurationException e)
		{
			logger.error(e.getMessage(), e); // TODO: Log medcom flow id
			throw SoapUtils.newDGWSFault(wsseHeader, medcomHeader, FaultMessages.INTERNAL_SERVER_ERROR, FaultCodeValues.PROCESSING_PROBLEM);
		}

		throw new AssertionError("Unreachable point: exactly one of the previous clauses is true");
	}


	private void verifyExactlyOneQueryParameterIsNonNull(Holder<Security> securityHeaderHolder, Holder<Header> medcomHeaderHolder, PersonLookupRequestType request) throws DGWSFault
	{
		// FIXME: This is actually handled by the @SchemaValidation annotation.
		
		Object[] queryParameters = new Object[4];

		queryParameters[0] = request.getBirthDatePersonQuery();
		queryParameters[1] = request.getCivilRegistrationNumberListPersonQuery();
		queryParameters[2] = request.getCivilRegistrationNumberPersonQuery();
		queryParameters[3] = request.getNamePersonQuery();

		int nonNullParameters = 0;
		for (Object parameter : queryParameters)
		{
			if (parameter != null)
			{
				nonNullParameters += 1;
			}
		}

		if (nonNullParameters != 1)
		{
			// TODO: This way of throwing faults was taken from DGCPROpslag and
			// does not contain any meaningful information for the caller.

			throw SoapUtils.newDGWSFault(securityHeaderHolder, medcomHeaderHolder, FaultMessages.INTERNAL_SERVER_ERROR, FaultCodeValues.PROCESSING_PROBLEM);
		}
	}


	private PersonLookupResponseType answerCprRequest(String cpr) throws SQLException, DatatypeConfigurationException
	{

		PersonLookupResponseType response = new PersonLookupResponseType();
		List<PersonInformationStructureType> personInformationStructure = response.getPersonInformationStructure();

		Person person = fetcher.fetch(Person.class, cpr);
		boolean wasFound = (person != null);
		
		logger.info("type=auditlog, client_cvr={}, requested_cpr={}, record_was_returned={}", new Object[] {clientCVR, cpr, wasFound});
		
		if (wasFound)
		{
			personInformationStructure.add(personMapper.map(person, ServiceProtectionLevel.CensorProtectedDataForNonAuthorities));
		}
		
		return response;
	}


	private PersonLookupResponseType answerCivilRegistrationNumberListPersonRequest(CivilRegistrationNumberListPersonQueryType civilRegistrationNumberList) throws SQLException, DatatypeConfigurationException
	{
		PersonLookupResponseType response = new PersonLookupResponseType();
		List<PersonInformationStructureType> personInformationStructure = response.getPersonInformationStructure();
		
		for (String cpr : civilRegistrationNumberList.getCivilRegistrationNumber())
		{
			Person person = fetcher.fetch(Person.class, cpr);
			boolean wasFound = (person != null);
			
			logger.info("type=auditlog, client_cvr={}, requested_cpr={}, record_was_returned={}", new Object[] {clientCVR, cpr, wasFound});
			
			if (wasFound)
			{
				personInformationStructure.add(personMapper.map(person, ServiceProtectionLevel.CensorProtectedDataForNonAuthorities));
			}
		}

		return response;
	}


	private PersonLookupResponseType answerBirthDatePersonRequest(XMLGregorianCalendar birthDate) throws SQLException, DatatypeConfigurationException
	{
		PersonLookupResponseType response = new PersonLookupResponseType();
		List<PersonInformationStructureType> personInformationStructure = response.getPersonInformationStructure();

		List<Person> persons = fetcher.fetch(Person.class, "Foedselsdato", birthDate.toGregorianCalendar().getTime());

		logger.info("type=auditlog, client_cvr={}, search_birthday_param={}", clientCVR, birthDate.toGregorianCalendar());
		
		for (Person person : persons)
		{
			logger.info("type=auditlog, client_cvr={}, cpr_of_returned_person={}", clientCVR, person.getCpr());
			
			personInformationStructure.add(personMapper.map(person, ServiceProtectionLevel.CensorProtectedDataForNonAuthorities));
		}

		return response;
	}


	private PersonLookupResponseType answerNamePersonRequest(NamePersonQueryType namePerson) throws SQLException, DatatypeConfigurationException
	{
		PersonLookupResponseType response = new PersonLookupResponseType();
		List<PersonInformationStructureType> personInformationStructure = response.getPersonInformationStructure();

		Map<String, Object> columnValuePairs = Maps.newHashMap();
		
		columnValuePairs.put("Fornavn", namePerson.getPersonGivenName());
		columnValuePairs.put("Efternavn", namePerson.getPersonSurnameName());
		
		if (!StringUtils.isBlank(namePerson.getPersonMiddleName()))
		{
			columnValuePairs.put("Mellemnavn", namePerson.getPersonMiddleName());
		}
		
		logger.info("type=auditlog, client_cvr={}, requested_name={} {} {}", new Object[] { clientCVR, namePerson.getPersonGivenName(), namePerson.getPersonMiddleName(), namePerson.getPersonSurnameName() });

		for (Person person : fetcher.fetch(Person.class, columnValuePairs))
		{
			logger.info("type=auditlog, client_cvr={}, cvr_of_returned_person={}", clientCVR, person.getCpr());
			
			personInformationStructure.add(personMapper.map(person, ServiceProtectionLevel.CensorProtectedDataForNonAuthorities));
		}

		return response;
	}
}
