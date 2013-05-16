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
package dk.nsi.stamdata.cpr.medcom;

import com.google.inject.Inject;
import com.sun.xml.ws.developer.SchemaValidation;
import com.trifork.stamdata.Fetcher;
import com.trifork.stamdata.jaxws.GuiceInstanceResolver.GuiceWebservice;
import com.trifork.stamdata.persistence.RecordFetcher;
import com.trifork.stamdata.persistence.Transactional;
import dk.nsi.stamdata.cpr.SoapUtils;
import dk.nsi.stamdata.cpr.mapping.PersonMapper100;
import dk.nsi.stamdata.cpr.models.Person;
import dk.nsi.stamdata.jaxws.generated.*;
import dk.sdsd.nsp.slalog.api.SLALogItem;
import dk.sosi.seal.model.SystemIDCard;
import oio.medcom.cprservice._1_0.*;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.Holder;

@WebService(endpointInterface="oio.medcom.cprservice._1_0.DetGodeCPROpslag102")
@GuiceWebservice
@SchemaValidation
public class DetGodeCPROpslagImpl102 extends DetGodCPROpslagBase implements DetGodeCPROpslag102
{
    private static final String NS_DET_GODE_CPR_OPSLAG_102 = "urn:oio:medcom:cprservice:1.0.2";
    private static final String NS_DGWS_1_0 = "http://www.medcom.dk/dgws/2006/04/dgws-1.0.xsd";
    private static final String NS_WS_SECURITY = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";

    private final PersonMapper100 personMapper;
    private final SLALogItem slaLogItem;

    @Inject
    DetGodeCPROpslagImpl102(Fetcher fetcher, RecordFetcher recordFetcher, PersonMapper100 personMapper, SystemIDCard card, SLALogItem slaLogItem)
    {
        super(fetcher, recordFetcher, card.getSystemInfo().getCareProvider().getID());
        this.personMapper = personMapper;
        this.slaLogItem = slaLogItem;
    }

    @Override
    @Transactional
    public GetPersonWithHealthCareInformationOut getPersonWithHealthCareInformation(
            @WebParam(name = "Security", targetNamespace = NS_WS_SECURITY, mode = WebParam.Mode.INOUT, partName = "wsseHeader") Holder<Security> wsseHeader,
            @WebParam(name = "Header", targetNamespace = NS_DGWS_1_0, mode = WebParam.Mode.INOUT, partName = "medcomHeader") Holder<Header> medcomHeader,
            @WebParam(name = "getPersonWithHealthCareInformationIn", targetNamespace = NS_DET_GODE_CPR_OPSLAG_102, partName = "parameters") GetPersonWithHealthCareInformationIn input)
            throws DGWSFault
    {
        return null;
    }

    @Override
    public GetPersonInformationOut getPersonInformation(
            @WebParam(name = "Security", targetNamespace = NS_WS_SECURITY, header = true, mode = WebParam.Mode.INOUT, partName = "wsseHeader") Holder<Security> wsseHeader,
            @WebParam(name = "Header", targetNamespace = NS_DGWS_1_0, header = true, mode = WebParam.Mode.INOUT, partName = "medcomHeader") Holder<Header> medcomHeader,
            @WebParam(name = "getPersonInformationIn", targetNamespace = NS_DET_GODE_CPR_OPSLAG_102, partName = "parameters") GetPersonInformationIn input) throws DGWSFault {

        SoapUtils.updateSlaLog(medcomHeader, "getPersonInformation", slaLogItem);
        SoapUtils.setHeadersToOutgoing(wsseHeader, medcomHeader);

        // 1. Check the white list to see if the client is authorized.

        String pnr = input.getPersonCivilRegistrationIdentifier();

        logAccess(pnr);

        // 2. Validate the input parameters.

        checkInputParameters(pnr);

        // 3. Fetch the person from the database.

        Person person = fetchPersonWithPnr(pnr);

        // We now have the requested person. Use it to fill in
        // the response.

        GetPersonInformationOut output = new GetPersonInformationOut();
        PersonInformationStructureType personInformation = null;

//        personInformation = personMapper.map(person, PersonMapper.ServiceProtectionLevel.AlwaysCensorProtectedData,
//                PersonMapper.CPRProtectionLevel.DoNotCensorCPR);

        output.setPersonInformationStructure(personInformation);

        return output;
    }
}
