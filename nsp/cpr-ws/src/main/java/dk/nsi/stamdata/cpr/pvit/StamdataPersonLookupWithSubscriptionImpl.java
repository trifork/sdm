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
package dk.nsi.stamdata.cpr.pvit;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.Holder;

import org.joda.time.DateTime;

import com.sun.xml.ws.developer.SchemaValidation;
import com.trifork.stamdata.persistence.Transactional;

import dk.nsi.stamdata.cpr.SoapUtils;
import dk.nsi.stamdata.cpr.jaxws.GuiceInstanceResolver.GuiceWebservice;
import dk.nsi.stamdata.cpr.medcom.FaultMessages;
import dk.nsi.stamdata.cpr.pvit.proxy.CprAbbsException;
import dk.nsi.stamdata.cpr.pvit.proxy.CprSubscriptionClient;
import dk.nsi.stamdata.cpr.ws.CprAbbsRequest;
import dk.nsi.stamdata.cpr.ws.DGWSFault;
import dk.nsi.stamdata.cpr.ws.Header;
import dk.nsi.stamdata.cpr.ws.PersonLookupResponseType;
import dk.nsi.stamdata.cpr.ws.Security;
import dk.nsi.stamdata.cpr.ws.StamdataPersonLookupWithSubscription;
import dk.sosi.seal.model.SystemIDCard;
import dk.sosi.seal.model.constants.FaultCodeValues;


@SchemaValidation
@GuiceWebservice
@WebService(serviceName = "StamdataPersonLookupWithSubscription", endpointInterface = "dk.nsi.stamdata.cpr.ws.StamdataPersonLookupWithSubscription")
public class StamdataPersonLookupWithSubscriptionImpl implements StamdataPersonLookupWithSubscription
{
    private final String clientCVR;
    private StamdataPersonResponseFinder stamdataPersonResponseFinder;
    private CprSubscriptionClient abbsClient;


    /**
     * Constructor for this implementation as Guice request scoped bean
     */
    @Inject
    StamdataPersonLookupWithSubscriptionImpl(SystemIDCard idCard, StamdataPersonResponseFinder stamdataPersonResponseFinder, CprSubscriptionClient abbsClient)
    {
        this.abbsClient = abbsClient;
        this.clientCVR = idCard.getSystemInfo().getCareProvider().getID();
        this.stamdataPersonResponseFinder = stamdataPersonResponseFinder;
    }


    @Override
    @Transactional
    public PersonLookupResponseType getSubscribedPersonDetails(
            @WebParam(name = "Security", targetNamespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", header = true, mode = WebParam.Mode.INOUT, partName = "wsseHeader") Holder<Security> wsseHeader,
            @WebParam(name = "Header", targetNamespace = "http://www.medcom.dk/dgws/2006/04/dgws-1.0.xsd", header = true, mode = WebParam.Mode.INOUT, partName = "medcomHeader") Holder<Header> medcomHeader,
            @WebParam(name = "CprAbbsRequest", targetNamespace = "http://nsi.dk/cprabbs/2011/10", partName = "parameters") CprAbbsRequest request) throws DGWSFault
    {
        PersonLookupResponseType response;
        
        try
        {
            List<String> changedCprs = abbsClient.getChangedCprs(wsseHeader, medcomHeader, new DateTime(request.getSince()));

            response = stamdataPersonResponseFinder.answerCivilRegistrationNumberListPersonRequest(clientCVR, changedCprs);
        }
        catch (SQLException e)
        {
            throw SoapUtils.newDGWSFault(wsseHeader, medcomHeader, FaultMessages.INTERNAL_SERVER_ERROR, FaultCodeValues.PROCESSING_PROBLEM);
        }
        catch (CprAbbsException e)
        {
            throw SoapUtils.newDGWSFault(wsseHeader, medcomHeader, FaultMessages.INTERNAL_SERVER_ERROR, FaultCodeValues.PROCESSING_PROBLEM);
        }

        return response;
    }
}
