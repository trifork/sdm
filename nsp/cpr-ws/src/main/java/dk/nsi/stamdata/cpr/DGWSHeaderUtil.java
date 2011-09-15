/**
 * The MIT License
 *
 * Original work sponsored and donated by National Board of e-Health (NSI), Denmark
 * (http://www.nsi.dk)
 *
 * Copyright (C) 2011 National Board of e-Health (NSI), Denmark (http://www.nsi.dk)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dk.nsi.stamdata.cpr;

import dk.nsi.stamdata.cpr.ws.Header;
import dk.nsi.stamdata.cpr.ws.Linking;
import dk.nsi.stamdata.cpr.ws.Security;
import dk.nsi.stamdata.cpr.ws.Timestamp;
import dk.sosi.seal.model.constants.FlowStatusValues;

import javax.xml.ws.Holder;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;

public class DGWSHeaderUtil {

	private static final String TIMEZONE = "UTC";

	private DGWSHeaderUtil() {
	}

	public static void setHeadersToOutgoing(Holder<Security> wsseHeader, Holder<Header> medcomHeader) {
		setSecurityHeaderToOutgoing(wsseHeader);
		setMedcomHeaderToOutgoing(medcomHeader);
	}

	private static void setSecurityHeaderToOutgoing(Holder<Security> wsseHeader) {
		Security sec = new Security();
		Timestamp timestamp = new Timestamp();
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(TIMEZONE));
		cal.set(Calendar.MILLISECOND, 0);
		timestamp.setCreated(cal);
		sec.setTimestamp(timestamp);
		wsseHeader.value = sec;
	}

	private static void setMedcomHeaderToOutgoing(Holder<Header> medcomHeader) {
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
