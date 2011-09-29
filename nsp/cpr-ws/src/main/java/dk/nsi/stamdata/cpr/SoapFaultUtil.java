package dk.nsi.stamdata.cpr;

import static com.trifork.stamdata.Preconditions.checkNotNull;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;

import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.Holder;
import javax.xml.ws.soap.SOAPFaultException;

import dk.nsi.stamdata.cpr.medcom.FaultMessages;
import dk.nsi.stamdata.cpr.ws.DGWSFault;
import dk.nsi.stamdata.cpr.ws.Header;
import dk.nsi.stamdata.cpr.ws.Linking;
import dk.nsi.stamdata.cpr.ws.Security;
import dk.nsi.stamdata.cpr.ws.Timestamp;
import dk.sosi.seal.model.constants.FlowStatusValues;

public final class SoapFaultUtil
{
	private SoapFaultUtil()
	{
	}

	public static DGWSFault newDGWSFault(Holder<Security> securityHeaderHolder, Holder<Header> medcomHeaderHolder, String status, String errorMsg)
	{
		checkNotNull(securityHeaderHolder, "securityHeaderHolder");
		checkNotNull(medcomHeaderHolder, "medcomHeaderHolder");
		checkNotNull(status, "status");
		checkNotNull(errorMsg, "errorMsg");
		
		// The DGWS Specification says that this exact string must be returned.
		
		final String DGWS_ERROR_MSG = "DGWS error";
		final String DGWS_TIMEZONE = "UTC";
		
		Security securityHeader = new Security();
		
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(DGWS_TIMEZONE));
		cal.set(Calendar.MILLISECOND, 0);
		
		Timestamp timestamp = new Timestamp();
		timestamp.setCreated(cal);
		securityHeader.setTimestamp(timestamp);
		securityHeaderHolder.value = securityHeader;
		
		Header medcom = new Header();
		medcom.setFlowStatus(FlowStatusValues.FLOW_FINALIZED_SUCCESFULLY);
		
		Linking linking = new Linking();
		linking.setFlowID(medcomHeaderHolder.value.getLinking().getFlowID());
		linking.setInResponseToMessageID(medcomHeaderHolder.value.getLinking().getMessageID());
		linking.setMessageID(UUID.randomUUID().toString());
		medcom.setLinking(linking);
		
		medcomHeaderHolder.value = medcom;
		medcomHeaderHolder.value.setFlowStatus(status);

		return new DGWSFault(errorMsg, DGWS_ERROR_MSG);
	}

	public static SOAPFaultException newSOAPSenderFault(String message)
	{
		checkNotNull(message, "message");

		SOAPFault fault;

		try
		{
			// We have to make sure to use the same protocol version
			// as defined in the WSDL.

			SOAPFactory factory = SOAPFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);

			fault = factory.createFault();
			fault.setFaultCode(SOAPConstants.SOAP_SENDER_FAULT);

			// TODO: For some reason the xml:lang att. is always "en"
			// even when the locale is set in this next call.

			fault.setFaultString(message);
		}
		catch (Exception e)
		{
			throw newServerErrorFault(e);
		}

		return new SOAPFaultException(fault);
	}

	public static RuntimeException newServerErrorFault(Exception e)
	{
		checkNotNull(e, "e");

		return new RuntimeException(FaultMessages.INTERNAL_SERVER_ERROR, e);
	}
}