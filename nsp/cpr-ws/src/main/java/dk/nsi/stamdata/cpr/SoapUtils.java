/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */
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
import dk.nsi.stamdata.jaxws.generated.DGWSFault;
import dk.nsi.stamdata.jaxws.generated.Header;
import dk.nsi.stamdata.jaxws.generated.Linking;
import dk.nsi.stamdata.jaxws.generated.Security;
import dk.nsi.stamdata.jaxws.generated.Timestamp;
import dk.sdsd.nsp.slalog.api.SLALogItem;
import dk.sosi.seal.model.constants.FlowStatusValues;


public final class SoapUtils
{
	private static final TimeZone DGWS_TIMEZONE = TimeZone.getTimeZone("UTC");
	
	private SoapUtils()
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

		Security securityHeader = new Security();

		Calendar cal = Calendar.getInstance(DGWS_TIMEZONE);
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


	public static void setHeadersToOutgoing(Holder<Security> wsseHeader, Holder<Header> medcomHeader)
	{
		setSecurityHeaderToOutgoing(wsseHeader);
		setMedcomHeaderToOutgoing(medcomHeader);
	}


	private static void setSecurityHeaderToOutgoing(Holder<Security> wsseHeader)
	{
		Security sec = new Security();
		Timestamp timestamp = new Timestamp();
		Calendar cal = Calendar.getInstance(DGWS_TIMEZONE);
		cal.set(Calendar.MILLISECOND, 0);
		timestamp.setCreated(cal);
		sec.setTimestamp(timestamp);
		wsseHeader.value = sec;
	}


	private static void setMedcomHeaderToOutgoing(Holder<Header> medcomHeader)
	{
		Header medcom = new Header();
		medcom.setFlowStatus(FlowStatusValues.FLOW_FINALIZED_SUCCESFULLY);

		Linking linking = new Linking();
		linking.setFlowID(medcomHeader.value.getLinking().getFlowID());
		linking.setInResponseToMessageID(medcomHeader.value.getLinking().getMessageID());
		linking.setMessageID(UUID.randomUUID().toString());
		medcom.setLinking(linking);

		medcomHeader.value = medcom;
	}

    public static void updateSlaLog(Holder<Header> medcomHeader, SLALogItem slaLogItem) {
        Header value = medcomHeader.value;
        if (slaLogItem != null && value.getLinking() != null && value.getLinking().getMessageID() != null) {
            String messageID = value.getLinking().getMessageID();
            slaLogItem.setMessageId(messageID);
        }
    }
}
