package dk.nsi.stamdata.cpr;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;

import javax.xml.ws.Holder;

import dk.nsi.stamdata.cpr.ws.Header;
import dk.nsi.stamdata.cpr.ws.Linking;
import dk.nsi.stamdata.cpr.ws.Security;
import dk.nsi.stamdata.cpr.ws.Timestamp;
import dk.sosi.seal.model.constants.FlowStatusValues;

public class DgwsHeadersUtils {
    
    private static final String TIMEZONE = "UTC";

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
