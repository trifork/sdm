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
import java.util.Date;

import static com.trifork.stamdata.Preconditions.checkNotNull;

abstract public class DetGodeCPROpslagCommon {

    protected final Fetcher fetcher;
    protected final RecordFetcher recordFetcher;

    protected final String clientCVR;

    private static final Logger logger = Logger.getLogger(DetGodeCPROpslagCommon.class);

    public DetGodeCPROpslagCommon(Fetcher fetcher, RecordFetcher recordFetcher, String clientCVR) {
        this.fetcher = fetcher;
        this.recordFetcher = recordFetcher;
        this.clientCVR = clientCVR;
    }

    @Transactional
    protected Record getSikredeRecord(String pnr, Date currentDate) throws SQLException {
        return recordFetcher.fetchCurrent(pnr, SikredeRecordSpecs.ENTRY_RECORD_SPEC, "CPRnr", currentDate);
    }

    @Transactional
    protected Record getYderRecord(String ydernummer, Date currentDate) throws SQLException {
        return recordFetcher.fetchCurrent(ydernummer, YderregisterRecordSpecs.YDER_RECORD_TYPE, "YdernrYder", currentDate);
    }

    // HELPERS
    protected Person fetchPersonWithPnr(String pnr) {
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
    protected void checkInputParameters(@Nullable String pnr) {
        if (StringUtils.isBlank(pnr)) {
            // This service should match functionality from a service that was
            // not DGWS protected.
            // Callers expect to be met with a SOAP sender fault.
            // Callers to the PVIT service should expect DGWS faults instead.
            throw SoapUtils.newSOAPSenderFault("PersonCivilRegistrationIdentifier was not set in request, but is required.");
        }
    }

    protected void logAccess(String requestedCPR) {
        logger.info("type=auditlog, service=stamdata-cpr, client_cvr="+clientCVR+", requested_cpr="+requestedCPR);
    }

}
