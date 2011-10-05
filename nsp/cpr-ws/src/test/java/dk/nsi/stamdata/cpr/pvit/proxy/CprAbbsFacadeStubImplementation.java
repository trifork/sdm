package dk.nsi.stamdata.cpr.pvit.proxy;

import dk.nsi.dgws.DgwsIdcardFilter;
import dk.nsi.stamdata.cpr.ws.*;
import dk.sosi.seal.model.SystemIDCard;
import org.joda.time.DateTime;

import javax.annotation.Resource;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.servlet.ServletRequest;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import java.util.Calendar;

@WebService(endpointInterface = "dk.nsi.stamdata.cpr.ws.CprAbbsFacade")
public class CprAbbsFacadeStubImplementation implements CprAbbsFacade {
	public static final DateTime SINCE_VALUE_TRIGGERING_CPR_WITH_ALL_ONES = new DateTime(2011, 04, 14, 12, 00, 00);

	@Resource
	WebServiceContext wsContext;

	@Override
	/**
	 * Returns:
	 *  - if since not set and security headers contains a cvr CVR starting with 1: cpr CVR00
	 *  - if since not set: CPR 0000000000
	 *  - if since set to SINCE_VALUE_TRIGGERING_CPR_WITH_ALL_ONES: cpr 1111111111
	 *  - if since set to any other value: cpr 2222222222
	 */
	public CprAbbsResponse getChangedCprs(@WebParam(name = "Security", targetNamespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", header = true, mode = WebParam.Mode.INOUT, partName = "wsseHeader") Holder<Security> wsseHeader,
	                                      @WebParam(name = "Header", targetNamespace = "http://www.medcom.dk/dgws/2006/04/dgws-1.0.xsd", header = true, mode = WebParam.Mode.INOUT, partName = "medcomHeader") Holder<Header> medcomHeader,
	                                      @WebParam(name = "CprAbbsRequest", targetNamespace = "http://nsi.dk/cprabbs/2011/10", partName = "cprAbbsRequest") CprAbbsRequest cprAbbsRequest) throws DGWSFault {
		CprAbbsResponse response = new CprAbbsResponse();
		Calendar since = cprAbbsRequest.getSince();
		String cpr;
		if (since != null) {
			DateTime sinceAsJoda = new DateTime(since.getTime());
			if (SINCE_VALUE_TRIGGERING_CPR_WITH_ALL_ONES.equals(sinceAsJoda)) {
				cpr = "1111111111";
			} else {
				cpr = "2222222222";
			}
		} else {
			// since == null
			String cvr = findIdCardInRequest().getSystemInfo().getCareProvider().getID();
			if (cvr != null && cvr.startsWith("1")) {
				cpr = cvr + "00";
			} else {
				cpr = "0000000000";
			}
		}

		response.getChangedCprs().add(cpr);
		return response;
	}

	public SystemIDCard findIdCardInRequest() {
		MessageContext context = wsContext.getMessageContext();
		ServletRequest request = (ServletRequest) context.get(MessageContext.SERVLET_REQUEST);
		return (SystemIDCard) request.getAttribute(DgwsIdcardFilter.IDCARD_REQUEST_ATTRIBUTE_KEY);

	}
}
