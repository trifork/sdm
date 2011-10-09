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

import dk.nsi.stamdata.cpr.jaxws.SealNamespaceResolver;
import dk.nsi.stamdata.cpr.ws.*;
import org.joda.time.DateTime;

import com.trifork.stamdata.persistence.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;


public class CprSubscriptionClient
{
    public static final String ENDPOINT_PROPERTY_NAME = "cprabbs.service.endpoint.url";
    private final URL wsdlLocation;
    private static final QName SERVICE_QNAME = new QName("http://nsi.dk/cprabbs/2011/10", "CprAbbsFacadeService");


    @Inject
    public CprSubscriptionClient(@Named(ENDPOINT_PROPERTY_NAME) String cprabbsServiceUrl) throws MalformedURLException
    {
        this.wsdlLocation = new URL(cprabbsServiceUrl + "?wsdl");
    }


    @Transactional
    public List<String> getChangedCprs(Holder<Security> wsseHeader, Holder<Header> medcomHeader, DateTime since) throws CprAbbsException
    {
        CprAbbsFacadeService serviceCatalog = new CprAbbsFacadeService(wsdlLocation, SERVICE_QNAME);

        serviceCatalog.setHandlerResolver(new SealNamespaceResolver());

        CprAbbsFacade client = serviceCatalog.getCprAbbsSoapBinding();

        CprAbbsRequest request = new CprAbbsRequest();

        if (since != null)
        {
            request.setSince(since.toCalendar(Locale.getDefault()));
        }

        CprAbbsResponse response;

        try
        {
            response = client.getChangedCprs(wsseHeader, medcomHeader, request);
        }
        catch (DGWSFault dgwsFault)
        {
            throw new CprAbbsException(dgwsFault);
        }

        return response.getChangedCprs();
    }
}
