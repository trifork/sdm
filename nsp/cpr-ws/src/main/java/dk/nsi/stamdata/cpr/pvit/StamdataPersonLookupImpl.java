package dk.nsi.stamdata.cpr.pvit;

import com.sun.xml.ws.developer.SchemaValidation;
import dk.nsi.stamdata.cpr.SoapUtils;
import dk.nsi.stamdata.cpr.jaxws.GuiceInstanceResolver.GuiceWebservice;
import dk.nsi.stamdata.cpr.medcom.FaultMessages;
import dk.nsi.stamdata.cpr.ws.*;
import dk.sosi.seal.model.SystemIDCard;
import dk.sosi.seal.model.constants.FaultCodeValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.jws.WebService;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.ws.Holder;
import java.sql.SQLException;


@SchemaValidation
@GuiceWebservice
@WebService(serviceName = "StamdataPersonLookup", endpointInterface = "dk.nsi.stamdata.cpr.ws.StamdataPersonLookup")
public class StamdataPersonLookupImpl implements StamdataPersonLookup
{
	private final static Logger logger = LoggerFactory.getLogger(StamdataPersonLookupImpl.class);

	private final String clientCVR;
	private final StamdataPersonResponseFinder stamdataPersonResponseFinder;


	@Inject
	/**
	 * Constructor for this implementation as Guice request scoped bean
	 */
	StamdataPersonLookupImpl(SystemIDCard idCard, StamdataPersonResponseFinder stamdataPersonResponseFinder)
	{
		this.clientCVR = idCard.getSystemInfo().getCareProvider().getID();
		this.stamdataPersonResponseFinder = stamdataPersonResponseFinder;
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
				return stamdataPersonResponseFinder.answerCprRequest(clientCVR, request.getCivilRegistrationNumberPersonQuery());
			}

			if (request.getCivilRegistrationNumberListPersonQuery() != null)
			{
				return stamdataPersonResponseFinder.answerCivilRegistrationNumberListPersonRequest(clientCVR, request.getCivilRegistrationNumberListPersonQuery().getCivilRegistrationNumber());
			}

			if (request.getBirthDatePersonQuery() != null)
			{
				return stamdataPersonResponseFinder.answerBirthDatePersonRequest(clientCVR, request.getBirthDatePersonQuery());
			}

			if (request.getNamePersonQuery() != null)
			{
				return stamdataPersonResponseFinder.answerNamePersonRequest(clientCVR, request.getNamePersonQuery());
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
}
