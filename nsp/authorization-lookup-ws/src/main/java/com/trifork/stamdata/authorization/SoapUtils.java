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
package com.trifork.stamdata.authorization;

import dk.nsi.stamdata.jaxws.generated.*;
import dk.sosi.seal.model.constants.FlowStatusValues;

import javax.xml.ws.Holder;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;

public final class SoapUtils
{
	private static final TimeZone DGWS_TIMEZONE = TimeZone.getTimeZone("UTC");
	
	private SoapUtils() {
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
}
