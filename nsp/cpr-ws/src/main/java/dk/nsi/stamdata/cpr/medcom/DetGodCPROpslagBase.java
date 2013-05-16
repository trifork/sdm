package dk.nsi.stamdata.cpr.medcom;

import com.trifork.stamdata.Fetcher;
import com.trifork.stamdata.Nullable;
import com.trifork.stamdata.persistence.Record;
import com.trifork.stamdata.persistence.RecordFetcher;
import com.trifork.stamdata.persistence.Transactional;
import com.trifork.stamdata.specs.SikredeRecordSpecs;
import com.trifork.stamdata.specs.YderregisterRecordSpecs;
import dk.nsi.stamdata.cpr.SoapUtils;
import dk.nsi.stamdata.cpr.models.Person;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.SQLException;

import static com.trifork.stamdata.Preconditions.checkNotNull;

public class DetGodCPROpslagBase {

    protected final Fetcher fetcher;
    protected final RecordFetcher recordFetcher;

    protected final String clientCVR;

    private static final Logger logger = Logger.getLogger(DetGodCPROpslagBase.class);

    public DetGodCPROpslagBase(Fetcher fetcher, RecordFetcher recordFetcher, String clientCVR) {
        this.fetcher = fetcher;
        this.recordFetcher = recordFetcher;
        this.clientCVR = clientCVR;
    }

    @Transactional
    protected Record getSikredeRecord(String pnr) throws SQLException
    {
        return recordFetcher.fetchCurrent(pnr, SikredeRecordSpecs.ENTRY_RECORD_SPEC, "CPRnr");
    }

    @Transactional
    protected Record getYderRecord(String ydernummer) throws SQLException
    {
        return recordFetcher.fetchCurrent(ydernummer, YderregisterRecordSpecs.YDER_RECORD_TYPE, "YdernrYder");
    }

    // HELPERS

    protected Person fetchPersonWithPnr(String pnr)
    {
        checkNotNull(pnr, "pnr");

        Person person;

        try {
            person = fetcher.fetch(Person.class, pnr);
        } catch (Exception e) {
            throw SoapUtils.newServerErrorFault(e);
        }

        // NOTE: Unfortunately the specification is defined so that we have to
        // return a
        // fault if no person is found. We cannot change this to return nil
        // which would
        // be a nicer protocol.

        if (person == null) {
            throw SoapUtils.newSOAPSenderFault(FaultMessages.NO_DATA_FOUND_FAULT_MSG);
        }

        return person;
    }


    // HELPERS

    protected void checkInputParameters(@Nullable String pnr)
    {
        if (StringUtils.isBlank(pnr)) {
            // This service should match functionality from a service that was
            // not DGWS protected.
            // Callers expect to be met with a SOAP sender fault.
            // Callers to the PVIT service should expect DGWS faults instead.
            throw SoapUtils.newSOAPSenderFault("PersonCivilRegistrationIdentifier was not set in request, but is required.");
        }
    }


    protected void logAccess(String requestedCPR)
    {
        logger.info("type=auditlog, service=stamdata-cpr, client_cvr="+clientCVR+", requested_cpr="+requestedCPR);
    }

}
