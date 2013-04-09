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
package dk.nsi.stamdata.replication.webservice;

import static java.lang.String.format;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.transform.TransformerException;
import javax.xml.ws.Holder;

import dk.nsi.stamdata.replication.dynamic.DynamicViewMapper;
import dk.nsi.stamdata.replication.dynamic.DynamicRow;
import dk.nsi.stamdata.replication.dynamic.DynamicRowFetcher;
import dk.nsi.stamdata.replication.vo.ViewMapVO;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.w3c.dom.Document;

import com.google.inject.Inject;
import com.sun.xml.ws.developer.SchemaValidation;
import com.trifork.stamdata.jaxws.GuiceInstanceResolver.GuiceWebservice;

import dk.nsi.stamdata.jaxws.generated.Header;
import dk.nsi.stamdata.jaxws.generated.ObjectFactory;
import dk.nsi.stamdata.jaxws.generated.ReplicationFault;
import dk.nsi.stamdata.jaxws.generated.ReplicationRequestType;
import dk.nsi.stamdata.jaxws.generated.ReplicationResponseType;
import dk.nsi.stamdata.jaxws.generated.Security;
import dk.nsi.stamdata.jaxws.generated.StamdataReplication;
import dk.nsi.stamdata.jaxws.generated.Timestamp;
import dk.nsi.stamdata.replication.models.Client;
import dk.nsi.stamdata.replication.models.ClientDao;
import dk.nsi.stamdata.security.ClientVocesCvr;

@WebService(endpointInterface="dk.nsi.stamdata.jaxws.generated.StamdataReplication")
@HandlerChain(file="handler-chain.xml")
@GuiceWebservice
@SchemaValidation
public class StamdataReplicationImpl implements StamdataReplication {

    private static final Logger logger = Logger.getLogger(StamdataReplicationImpl.class);
    
    private static final int MAX_RECORD_LIMIT = 2000;
    
    private final String cvr;
    private final ClientDao clients;

    @Inject
    private DynamicViewMapper dynamicViewMapper;

    @Inject
    private DynamicRowFetcher dynamicRowFetcher;

    @Inject
    private DynamicViewXmlGenerator dynamicViewXmlGenerator;

    @Inject
    StamdataReplicationImpl(@ClientVocesCvr String cvr, ClientDao clientDao)
    {
        this.cvr = cvr;
        this.clients = clientDao;
    }
    

    @Override
    public ReplicationResponseType replicate(Holder<Security> wsseHeader, Holder<Header> medcomHeader, ReplicationRequestType parameters) throws ReplicationFault
    {
    	
    	// Replace the client request securityheader
    	Security security = new Security();
    	Timestamp ts = new Timestamp();
    	ts.setCreated(Calendar.getInstance());
    	security.setTimestamp(ts);
    	wsseHeader.value = security;
    	
        try
        {
            return handleRequestUsingDynamicViews(wsseHeader, medcomHeader, parameters);
        }
        catch (ReplicationFault e)
        {
            // Log an throw is normally an anti-pattern, but since
            // exceptions are part of JAX-WS's flow it is "OK" here.
            //
            logger.warn("The request could not be handled. This is likely the clients mistake.", e);
            
            throw e;
        }
        catch (Exception e)
        {
            throw new ReplicationFault("An unhandled error occurred.", FaultCodes.INTERNAL_ERROR, e);
        }
    }

    private ReplicationResponseType handleRequestUsingDynamicViews(Holder<Security> wsseHeader, Holder<Header> medcomHeader,
                                                                   ReplicationRequestType parameters) throws ReplicationFault {
        try {
            String viewPath = getViewPath(parameters);

            MDC.put("view", String.valueOf(viewPath));
            MDC.put("cvr", String.valueOf(cvr));

            Client client = clients.findByCvr(cvr);
            if (client == null || !client.isAuthorizedFor(viewPath))
            {
                throw new ReplicationFault("The provided cvr is not authorized to fetch this datatype.", FaultCodes.UNAUTHORIZED);
            }

            HistoryOffset offset = getOffset(parameters);
            int limit = getRecordLimit(parameters);

            MDC.put("offset", String.valueOf(offset));
            MDC.put("limit", String.valueOf(limit));

            ////////////////////
            // Do actual work here
            ViewMapVO view = dynamicViewMapper
                    .getViewMapForView(parameters.getRegister(), parameters.getDatatype(), parameters.getVersion());

            Document feedDocument = createFeed(view, parameters, offset, limit);

            // Construct the output container.
            ReplicationResponseType response = new ObjectFactory().createReplicationResponseType();

            response.setAny(feedDocument.getFirstChild());

            // Log that the client successfully accessed the data.
            // Simply for audit purposes.
            //
            // The client details are included in the MDC.
            //
            logger.info("Records fetched, sending response.");

            return response;

        } catch (ReplicationFault e) {
            throw e;
        } catch (Exception e) {
            throw new ReplicationFault("Could not complete the request do to an error.", FaultCodes.INTERNAL_ERROR, e);
        } finally {
            MDC.remove("view");
            MDC.remove("cvr");
            MDC.remove("offset");
            MDC.remove("limit");
        }
    }

    private int getRecordLimit(ReplicationRequestType parameters)
    {
        if (parameters.getMaxRecords() == null) {
            return MAX_RECORD_LIMIT;
        } else {
            return Math.min(parameters.getMaxRecords().intValue(), MAX_RECORD_LIMIT);
        }
    }

    private Document createFeed(ViewMapVO view, ReplicationRequestType parameters, HistoryOffset offset, int limit) {
        long pid = Long.parseLong(offset.getRecordID());
        Instant modifiedDate = new Instant(offset.getModifiedDate());

        try {
            List<DynamicRow> rows = dynamicRowFetcher.fetchRows(view, pid, modifiedDate, limit);
            return dynamicViewXmlGenerator.generateXml(view, rows,
                    parameters.getRegister(), parameters.getDatatype(), DateTime.now());
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        } catch (TransformerException e) {
            throw new RuntimeException("Transformer error", e);
        }
    }


    private String getViewPath(ReplicationRequestType parameters) 
    {
        return format("%s/%s/v%d", parameters.getRegister(), parameters.getDatatype(), parameters.getVersion());
    }

    private HistoryOffset getOffset(ReplicationRequestType parameters) throws ReplicationFault
    {
        try {
            return new HistoryOffset(parameters.getOffset());
        } catch (Exception e) {
            throw new ReplicationFault("Invalid offset in the request.", FaultCodes.INVALID_OFFSET, e);
        }
    }
}
