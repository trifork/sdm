package dk.nsi.stamdata.cpr.medcom;

import com.google.inject.Inject;
import com.trifork.stamdata.Fetcher;
import com.trifork.stamdata.Nullable;
import com.trifork.stamdata.models.cpr.Person;
import com.trifork.stamdata.models.sikrede.Sikrede;
import com.trifork.stamdata.models.sikrede.SikredeYderRelation;
import com.trifork.stamdata.models.sikrede.Yderregister;
import dk.nsi.stamdata.cpr.SoapFaultUtil;
import dk.nsi.stamdata.cpr.WhitelistProvider.Whitelist;
import dk.nsi.stamdata.cpr.jaxws.GuiceInstanceResolver.GuiceWebservice;
import dk.nsi.stamdata.cpr.ws.*;
import dk.sosi.seal.model.SystemIDCard;
import dk.sosi.seal.model.constants.FaultCodeValues;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.ws.Holder;
import java.math.BigInteger;
import java.util.Set;

import static com.trifork.stamdata.Preconditions.checkNotNull;

@GuiceWebservice
@WebService(serviceName = "DetGodeCprOpslag", endpointInterface = "dk.nsi.stamdata.cpr.ws.DetGodeCPROpslag")
public class DetGodeCPROpslagImpl implements DetGodeCPROpslag {
    private static Logger logger = LoggerFactory.getLogger(DetGodeCPROpslagImpl.class);

    private static final String NS_TNS = "http://rep.oio.dk/medcom.sundcom.dk/xml/wsdl/2007/06/28/";
    private static final String NS_DGWS_1_0 = "http://www.medcom.dk/dgws/2006/04/dgws-1.0.xsd"; // TODO: Shouldn't this be 1.0.1?
    private static final String NS_WS_SECURITY = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";

    private final Set<String> whitelist;
    private final Fetcher fetcher;
    private final PersonMapper personMapper;
    private final PersonWithHealthCareMapper personWithHealthCareMapper;

    private final SystemIDCard idCard;

    @Inject
    DetGodeCPROpslagImpl(@Whitelist Set<String> whitelist, Fetcher fetcher, PersonMapper personMapper, PersonWithHealthCareMapper personWithHealthCareMapper, SystemIDCard idCard) {
        this.whitelist = whitelist;
        this.fetcher = fetcher;
        this.personMapper = personMapper;
        this.personWithHealthCareMapper = personWithHealthCareMapper;
        this.idCard = idCard;
    }

    @Override
    public GetPersonInformationOut getPersonInformation(@WebParam(name = "Security", targetNamespace = NS_WS_SECURITY, mode = WebParam.Mode.INOUT, partName = "wsseHeader") Holder<Security> wsseHeader, @WebParam(name = "Header", targetNamespace = NS_DGWS_1_0, mode = WebParam.Mode.INOUT, partName = "medcomHeader") Holder<Header> medcomHeader, @WebParam(name = "getPersonInformationIn", targetNamespace = NS_TNS, partName = "parameters") GetPersonInformationIn input) throws DGWSFault {
        // 1. Check the white list to see if the client is authorized.

        String pnr = input.getPersonCivilRegistrationIdentifier();

        checkClientAuthorization(pnr, wsseHeader, medcomHeader);

        // 2. Validate the input parameters.

        checkInputParameters(pnr);

        // 3. Fetch the person from the database.

        Person person = fetchPersonWithPnr(pnr);

        // We now have the requested person. Use it to fill in
        // the response.

        GetPersonInformationOut output = new GetPersonInformationOut();
        PersonInformationStructureType personInformation;

        try {
            personInformation = personMapper.map(person, false); // FIXME: Protect data when we need to.
        } catch (DatatypeConfigurationException e) {
            throw SoapFaultUtil.newServerErrorFault(e);
        }

        output.setPersonInformationStructure(personInformation);

        return output;
    }


    @Override
    public GetPersonWithHealthCareInformationOut getPersonWithHealthCareInformation(@WebParam(name = "Security", targetNamespace = NS_WS_SECURITY, mode = WebParam.Mode.INOUT, partName = "wsseHeader") Holder<Security> wsseHeader, @WebParam(name = "Header", targetNamespace = NS_DGWS_1_0, mode = WebParam.Mode.INOUT, partName = "medcomHeader") Holder<Header> medcomHeader, @WebParam(name = "getPersonWithHealthCareInformationIn", targetNamespace = NS_TNS, partName = "parameters") GetPersonWithHealthCareInformationIn parameters) throws DGWSFault {
        // 1. Check the white list to see if the client is authorized.

        String pnr = parameters.getPersonCivilRegistrationIdentifier();

        checkClientAuthorization(pnr, wsseHeader, medcomHeader);

        // 2. Validate the input parameters.

        checkInputParameters(pnr);

        // 2. Fetch the person from the database.
        //
        // NOTE: Unfortunately the specification is defined so that we have to
        // return a fault if no person is found. We cannot change this to return nil
        // which would be a nicer protocol.

        Person person = fetchPersonWithPnr(pnr);
        Sikrede sikrede = null; // TODO: Fetch the "sikrede" record for the pnr.
        SikredeYderRelation sikredeYderRelation = fetchSikredeYderRelationWithPnr(pnr+"-C");
        Yderregister yderregister = fetchYderregisterForPnr(sikredeYderRelation.getYdernummer());

        GetPersonWithHealthCareInformationOut output = new GetPersonWithHealthCareInformationOut();
        PersonWithHealthCareInformationStructureType personWithHealthCareInformation = null;
        try {
            personWithHealthCareInformation = personWithHealthCareMapper.map(person, sikredeYderRelation, yderregister);
        } catch (DatatypeConfigurationException e) {
            throw SoapFaultUtil.newServerErrorFault(e);
        }
        output.setPersonWithHealthCareInformationStructure(personWithHealthCareInformation);

        return output;
    }

    // HELPERS

    private Person fetchPersonWithPnr(String pnr) {
        checkNotNull(pnr, "pnr");

        Person person;

        try {
            person = fetcher.fetch(Person.class, pnr);
        } catch (Exception e) {
            throw SoapFaultUtil.newServerErrorFault(e);
        }

        // NOTE: Unfortunately the specification is defined so that we have to return a
        // fault if no person is found. We cannot change this to return nil which would
        // be a nicer protocol.

        if (person == null) {
            throw SoapFaultUtil.newSOAPSenderFault(FaultMessages.NO_DATA_FOUND_FAULT_MSG);
        }

        return person;
    }

    private SikredeYderRelation fetchSikredeYderRelationWithPnr(String pnr) {
        checkNotNull(pnr, "pnr");
        SikredeYderRelation sikredeYderRelation = null;

        try {
            sikredeYderRelation = fetcher.fetch(SikredeYderRelation.class, pnr);
        } catch (Exception e) {
            throw SoapFaultUtil.newServerErrorFault(e);
        }

        return sikredeYderRelation;

    }

    private Yderregister fetchYderregisterForPnr(int ydernummer) {
        Yderregister yderregister = null;
        try {
            yderregister = fetcher.fetch(Yderregister.class, ydernummer);
        } catch (Exception e) {
            throw SoapFaultUtil.newServerErrorFault(e);
        }
        return yderregister;
    }

    @SuppressWarnings("unused")
    private Sikrede fetchSikredeWithPnr(String pnr) {
        checkNotNull(pnr, "pnr");

        Sikrede sikrede = null;

        try {
            //sikrede = fetcher.fetch(Sikrede.class, pnr); //TODO 
        } catch (Exception e) {
            throw SoapFaultUtil.newServerErrorFault(e);
        }

        if (sikrede == null) {
            throw SoapFaultUtil.newSOAPSenderFault(FaultMessages.NO_DATA_FOUND_FAULT_MSG);
        }

        return sikrede;
    }

    private void checkInputParameters(@Nullable String pnr) {
        if (StringUtils.isBlank(pnr)) {
            throw SoapFaultUtil.newSOAPSenderFault("PersonCivilRegistrationIdentifier was not set in request, but is required.");
        }
    }


    private void checkClientAuthorization(String requestedPNR, Holder<Security> wsseHeader, Holder<Header> medcomHeader) throws DGWSFault {
        String clientCVR = idCard.getSystemInfo().getCareProvider().getID();

        if (!whitelist.contains(clientCVR)) {
            logger.warn("type=auditlog, service=stamdata-cpr, access=denied, client_cvr={}, requested_pnr={}", clientCVR, requestedPNR);
            throw SoapFaultUtil.newDGWSFault(wsseHeader, medcomHeader, FaultMessages.CALLER_NOT_AUTHORIZED, FaultCodeValues.NOT_AUTHORIZED);
        } else {
            logger.info("type=auditlog, service=stamdata-cpr, access=granted, client_cvr={}, requested_pnr={}", clientCVR, requestedPNR);
        }
    }
}
