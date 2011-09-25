package dk.nsi.stamdata.cpr;

import static com.trifork.stamdata.Preconditions.checkNotNull;

import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.Holder;
import javax.xml.ws.soap.SOAPFaultException;

import dk.nsi.stamdata.cpr.ws.DGWSFault;
import dk.nsi.stamdata.cpr.ws.Header;
import dk.nsi.stamdata.cpr.ws.Security;

public final class DGWSFaultUtil
{
	private DGWSFaultUtil()
	{
	}

	static DGWSFault newDGWSFault(Holder<Security> securityHolder, Holder<Header> medcomHeaderHolder, String status, String errorMsg) throws DGWSFault
	{
		DGWSHeaderUtil.setHeadersToOutgoing(securityHolder, medcomHeaderHolder);
		medcomHeaderHolder.value.setFlowStatus(status);

		return new DGWSFault(errorMsg, DGWSHeaderUtil.DGWS_ERROR_MSG);
	}

	static SOAPFaultException newSOAPSenderFault(String message)
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

	static RuntimeException newServerErrorFault(Exception e)
	{
		checkNotNull(e, "e");

		return new RuntimeException(DetGodeCPROpslagFaultMessages.INTERNAL_SERVER_ERROR, e);
	}
}