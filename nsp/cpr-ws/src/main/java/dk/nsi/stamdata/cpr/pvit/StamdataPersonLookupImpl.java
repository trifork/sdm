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

import com.sun.xml.ws.developer.SchemaValidation;
import com.trifork.stamdata.jaxws.GuiceInstanceResolver.GuiceWebservice;
import com.trifork.stamdata.persistence.Transactional;

import dk.nsi.stamdata.cpr.SoapUtils;
import dk.nsi.stamdata.cpr.medcom.FaultMessages;
import dk.nsi.stamdata.cpr.ws.*;
import dk.sosi.seal.model.SystemIDCard;
import dk.sosi.seal.model.constants.FaultCodeValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.jws.WebService;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.ws.Holder;
import java.sql.SQLException;


@SchemaValidation
@GuiceWebservice
@WebService(serviceName = "StamdataPersonLookup", endpointInterface = "dk.nsi.stamdata.cpr.ws.StamdataPersonLookup")
public class StamdataPersonLookupImpl implements StamdataPersonLookup
{
    private final static Logger logger = LoggerFactory.getLogger(StamdataPersonLookupImpl.class);

    private final String clientCVR;
    private final StamdataPersonResponseFinder stamdataPersonResponseFinder;


    @Inject
    /**
     * Constructor for this implementation as Guice request scoped bean
     */
    StamdataPersonLookupImpl(SystemIDCard idCard, StamdataPersonResponseFinder stamdataPersonResponseFinder)
    {
        this.clientCVR = idCard.getSystemInfo().getCareProvider().getID();
        this.stamdataPersonResponseFinder = stamdataPersonResponseFinder;
    }


    @Override
    @Transactional
    public PersonLookupResponseType getPersonDetails(Holder<Security> wsseHeader, Holder<Header> medcomHeader, PersonLookupRequestType request) throws DGWSFault
    {
        verifyExactlyOneQueryParameterIsNonNull(wsseHeader, medcomHeader, request);

        // TODO: This should be done in the filter
        // This has to be done according to the DGWS specifications
        SoapUtils.setHeadersToOutgoing(wsseHeader, medcomHeader);

        try
        {
            if (request.getCivilRegistrationNumberPersonQuery() != null)
            {
                return stamdataPersonResponseFinder.answerCprRequest(clientCVR, request.getCivilRegistrationNumberPersonQuery());
            }

            if (request.getCivilRegistrationNumberListPersonQuery() != null)
            {
                return stamdataPersonResponseFinder.answerCivilRegistrationNumberListPersonRequest(clientCVR, request.getCivilRegistrationNumberListPersonQuery().getCivilRegistrationNumber());
            }

            if (request.getBirthDatePersonQuery() != null)
            {
                return stamdataPersonResponseFinder.answerBirthDatePersonRequest(clientCVR, request.getBirthDatePersonQuery());
            }

            if (request.getNamePersonQuery() != null)
            {
                return stamdataPersonResponseFinder.answerNamePersonRequest(clientCVR, request.getNamePersonQuery());
            }
        }
        catch (SQLException e)
        {
            logger.error(e.getMessage(), e); // TODO: Log medcom flow id
            throw SoapUtils.newDGWSFault(wsseHeader, medcomHeader, FaultMessages.INTERNAL_SERVER_ERROR, FaultCodeValues.PROCESSING_PROBLEM);
        }
        catch (DatatypeConfigurationException e)
        {
            logger.error(e.getMessage(), e); // TODO: Log medcom flow id
            throw SoapUtils.newDGWSFault(wsseHeader, medcomHeader, FaultMessages.INTERNAL_SERVER_ERROR, FaultCodeValues.PROCESSING_PROBLEM);
        }

        throw new AssertionError("Unreachable point: exactly one of the previous clauses is true");
    }


    private void verifyExactlyOneQueryParameterIsNonNull(Holder<Security> securityHeaderHolder, Holder<Header> medcomHeaderHolder, PersonLookupRequestType request) throws DGWSFault
    {
        // FIXME: This is actually handled by the @SchemaValidation annotation.

        Object[] queryParameters = new Object[4];

        queryParameters[0] = request.getBirthDatePersonQuery();
        queryParameters[1] = request.getCivilRegistrationNumberListPersonQuery();
        queryParameters[2] = request.getCivilRegistrationNumberPersonQuery();
        queryParameters[3] = request.getNamePersonQuery();

        int nonNullParameters = 0;
        for (Object parameter : queryParameters)
        {
            if (parameter != null)
            {
                nonNullParameters += 1;
            }
        }

        if (nonNullParameters != 1)
        {
            // TODO: This way of throwing faults was taken from DGCPROpslag and
            // does not contain any meaningful information for the caller.

            throw SoapUtils.newDGWSFault(securityHeaderHolder, medcomHeaderHolder, FaultMessages.INTERNAL_SERVER_ERROR, FaultCodeValues.PROCESSING_PROBLEM);
        }
    }
}
