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
package dk.nsi.stamdata.cpr.pvit.proxy;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.servlet.ServletRequest;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import dk.nsi.cprabbs._2011._10.CprAbbsFacade;
import dk.nsi.cprabbs._2011._10.CprAbbsRequestType;
import dk.nsi.cprabbs._2011._10.CprAbbsResponse;
import dk.nsi.cprabbs._2011._10.DGWSFault;
import dk.nsi.stamdata.security.DenGodeWebServiceFilter;
import org.joda.time.DateTime;

import com.sun.xml.ws.developer.SchemaValidation;

import dk.nsi.stamdata.jaxws.generated.Header;
import dk.nsi.stamdata.jaxws.generated.Security;
import dk.sosi.seal.model.SystemIDCard;

@WebService(endpointInterface="dk.nsi.cprabbs._2011._10.CprAbbsFacade")
@SchemaValidation
public class CprAbbsFacadeStubImplementation implements CprAbbsFacade {
	public static final DateTime SINCE_VALUE_TRIGGERING_CPR_WITH_ALL_ONES = new DateTime(2011, 4, 14, 12, 0, 0);
	public static Map<String, List<String>> cprsToReturnForCvrs = null;

	@Resource
	WebServiceContext wsContext;

	@Override
	/**
	 * Returns:
	 *  - if since not set and security headers contains a cvr CVR starting with 1: cpr CVR00
	 *  - if since not set: CPR 0000000000
	 *  - if since set to SINCE_VALUE_TRIGGERING_CPR_WITH_ALL_ONES: cpr 1111111111
	 *  - if since set to any other value: cpr 2222222222
	 *
	 *  this may all be overridden by changing the contents of cprsToReturnForCvrs
	 */
	public CprAbbsResponse getChangedCprs(@WebParam(name = "Security", targetNamespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", header = true, mode = WebParam.Mode.INOUT, partName = "wsseHeader") Holder<Security> wsseHeader,
	                                      @WebParam(name = "Header", targetNamespace = "http://www.medcom.dk/dgws/2006/04/dgws-1.0.xsd", header = true, mode = WebParam.Mode.INOUT, partName = "medcomHeader") Holder<Header> medcomHeader,
	                                      @WebParam(name = "CprAbbsRequest", targetNamespace = "http://nsi.dk/cprabbs/2011/10", partName = "cprAbbsRequest") CprAbbsRequestType cprAbbsRequest) throws DGWSFault {
		try {
			String cvr = findIdCardInRequest().getSystemInfo().getCareProvider().getID();
			CprAbbsResponse response = new CprAbbsResponse();

			if (cprsToReturnForCvrs == null) {
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

					if (cvr != null && cvr.startsWith("1")) {
						cpr = cvr + "00";
					} else {
						cpr = "0000000000";
					}
				}
				response.getChangedCprs().add(cpr);
			} else {
				response.getChangedCprs().addAll(cprsToReturnForCvrs.get(cvr));
			}

			return response;
		} finally {
			cprsToReturnForCvrs = null;
		}
	}

	public SystemIDCard findIdCardInRequest() {
		MessageContext context = wsContext.getMessageContext();
		ServletRequest request = (ServletRequest) context.get(MessageContext.SERVLET_REQUEST);
		return (SystemIDCard) request.getAttribute(DenGodeWebServiceFilter.IDCARD_REQUEST_ATTRIBUTE_KEY);
	}
}
