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
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.Holder;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;


@SchemaValidation
@GuiceWebservice
@WebService(serviceName = "StamdataPersonLookupWithSubscription", endpointInterface = "dk.nsi.stamdata.cpr.ws.StamdataPersonLookupWithSubscription")
public class StamdataPersonLookupWithSubscriptionImpl implements StamdataPersonLookupWithSubscription {
	private final static Logger logger = LoggerFactory.getLogger(StamdataPersonLookupWithSubscriptionImpl.class);
	private final String clientCVR;
	private StamdataPersonResponseFinder stamdataPersonResponseFinder;
	private CprAbbsClient abbsClient;

	@Inject
	/**
	 * Constructor for this implementation as Guice request scoped bean
	 */
	StamdataPersonLookupWithSubscriptionImpl(SystemIDCard idCard, StamdataPersonResponseFinder stamdataPersonResponseFinder, CprAbbsClient abbsClient) {
		this.abbsClient = abbsClient;
		this.clientCVR = idCard.getSystemInfo().getCareProvider().getID();
		this.stamdataPersonResponseFinder = stamdataPersonResponseFinder;
	}

	@Override
	public PersonLookupResponseType getSubscribedPersonDetails(
			@WebParam(name = "Security", targetNamespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", header = true, mode = WebParam.Mode.INOUT, partName = "wsseHeader") Holder<Security> wsseHeader,
			@WebParam(name = "Header", targetNamespace = "http://www.medcom.dk/dgws/2006/04/dgws-1.0.xsd", header = true, mode = WebParam.Mode.INOUT, partName = "medcomHeader") Holder<Header> medcomHeader,
			@WebParam(name = "CprAbbsRequest", targetNamespace = "http://nsi.dk/cprabbs/2011/10", partName = "parameters") CprAbbsRequest request) throws DGWSFault {
		PersonLookupResponseType response;
		try {
			List<String> changedCprs = abbsClient.getChangedCprs(wsseHeader, medcomHeader, new DateTime(request.getSince()));

			response = stamdataPersonResponseFinder.answerCivilRegistrationNumberListPersonRequest(clientCVR, changedCprs);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e); // TODO: Log medcom flow id
			throw SoapUtils.newDGWSFault(wsseHeader, medcomHeader, FaultMessages.INTERNAL_SERVER_ERROR, FaultCodeValues.PROCESSING_PROBLEM);
		} catch (CprAbbsException e) {
			logger.error(e.getMessage(), e); // TODO: Log medcom flow id
			throw SoapUtils.newDGWSFault(wsseHeader, medcomHeader, FaultMessages.INTERNAL_SERVER_ERROR, FaultCodeValues.PROCESSING_PROBLEM);
		}

		return response;
	}
}
