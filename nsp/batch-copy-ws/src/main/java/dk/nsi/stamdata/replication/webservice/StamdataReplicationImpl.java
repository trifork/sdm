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

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;
import javax.xml.ws.Holder;

import dk.nsi.stamdata.security.ClientVocesCvr;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.w3c.dom.Document;

import com.google.inject.Inject;
import com.sun.xml.ws.developer.SchemaValidation;
import com.trifork.stamdata.jaxws.GuiceInstanceResolver.GuiceWebservice;
import com.trifork.stamdata.persistence.Record;
import com.trifork.stamdata.persistence.RecordPersister;
import com.trifork.stamdata.persistence.RecordSpecification;

import dk.nsi.stamdata.jaxws.generated.Header;
import dk.nsi.stamdata.jaxws.generated.ObjectFactory;
import dk.nsi.stamdata.jaxws.generated.ReplicationFault;
import dk.nsi.stamdata.jaxws.generated.ReplicationRequestType;
import dk.nsi.stamdata.jaxws.generated.ReplicationResponseType;
import dk.nsi.stamdata.jaxws.generated.Security;
import dk.nsi.stamdata.jaxws.generated.StamdataReplication;
import dk.nsi.stamdata.replication.models.Client;
import dk.nsi.stamdata.replication.models.ClientDao;
import dk.nsi.stamdata.views.View;
import dk.nsi.stamdata.views.Views;

@WebService(endpointInterface="dk.nsi.stamdata.jaxws.generated.StamdataReplication")
@GuiceWebservice
@SchemaValidation
public class StamdataReplicationImpl implements StamdataReplication {

    private static final Logger logger = LoggerFactory.getLogger(StamdataReplicationImpl.class);
    
    private static final int MAX_RECORD_LIMIT = 2000;
    
    private final String cvr;
    private final RecordDao dao;
    private final Map<String, Class<? extends View>> viewClasses;
    private final ClientDao clients;

    private final AtomFeedWriter outputWriter;

    // FIXME: Session is only needed to create a RecordPersister and it's pretty hacky at that, as we just need a connection
    // The RecordPersister should be injected instead!
    private Session session;


    @Inject
    StamdataReplicationImpl(@ClientVocesCvr String cvr, RecordDao dao, ClientDao clients, Map<String, Class<? extends View>> viewClasses, AtomFeedWriter outputWriter, Session session)
    {
        this.cvr = cvr;
        this.dao = dao;
        this.clients = clients;
        this.viewClasses = viewClasses;
        this.outputWriter = outputWriter;
        this.session = session;
    }
    

    @Override
    public ReplicationResponseType replicate(Holder<Security> wsseHeader, Holder<Header> medcomHeader, ReplicationRequestType parameters) throws ReplicationFault
    {
        try
        {
            if(isSikredeRegister(parameters))
            {
                return handleRequestUsingRecords(wsseHeader, medcomHeader, parameters);
            }
            else
            {
                return handleRequestUsingHibernateView(wsseHeader, medcomHeader, parameters);
            }
        }
        catch (ReplicationFault e)
        {
            // Log an throw is normally an anti-pattern, but since
            // exceptions are part of JAX-WS's flow it is "OK" here.
            logger.warn("The request could not be handled. This is likely the clients mistake.", e);
            
            throw e;
        }
        catch (RuntimeException e)
        {
            logger.error("An unhandled error occured.", e);
            
            throw e;
        } 
        catch (SQLException e) 
        {
            logger.error("A database error occured");

            throw new RuntimeException(e);
        }
    }

    private boolean isSikredeRegister(ReplicationRequestType parameters) {
        return "sikrede".equals(parameters.getRegister()) && "sikrede".equals(parameters.getDatatype()) && (parameters.getVersion() == 1);
    }

    private ReplicationResponseType handleRequestUsingRecords(Holder<Security> wsseHeader, Holder<Header> medcomHeader,
            ReplicationRequestType parameters) throws ReplicationFault, SQLException 
    {
        try 
        {
            String viewPath = getViewPath(parameters);

            MDC.put("view", viewPath);
            MDC.put("cvr", cvr);
            
            // Validate authentication.
            //
            Client client = clients.findByCvr(cvr);
            if (client == null || !client.isAuthorizedFor(viewPath))
            {
                throw new ReplicationFault("The provided cvr is not authorized to fetch this datatype.", FaultCodes.UNAUTHORIZED);
            }
            
            // Validate the input parameters.
            //
            HistoryOffset offset = getOffset(parameters);
            int limit = getRecordLimit(parameters);
    
            MDC.put("offset", String.valueOf(offset));
            MDC.put("limit", String.valueOf(limit));
    
            // Fetch the records from the database and
            // fill the output structure.
            //
            Document feedDocument = createSikredeFeed(offset, limit);
    
            // Construct the output container.
            //
            ReplicationResponseType response = new ObjectFactory().createReplicationResponseType();
            
            System.out.println("Feed document: " + feedDocument.getFirstChild().getLocalName());
            
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
        } catch (SQLException e) {
            throw e;
        }
        finally
        {
            MDC.remove("view");
            MDC.remove("cvr");
            MDC.remove("offset");
            MDC.remove("limit");
        }
    }

    private ReplicationResponseType handleRequestUsingHibernateView(Holder<Security> wsseHeader, Holder<Header> medcomHeader,
            ReplicationRequestType parameters) throws RuntimeException, ReplicationFault 
    {
        try
        {
            Class<? extends View> requestedView = getViewClass(parameters);
    
            MDC.put("view", Views.getViewPath(requestedView));
            MDC.put("cvr", cvr);
    
            // Validate authentication.
            //
            Client client = clients.findByCvr(cvr);
            if (client == null || !client.isAuthorizedFor(requestedView))
            {
                throw new ReplicationFault("The provided cvr is not authorized to fetch this datatype.", FaultCodes.UNAUTHORIZED);
            }
    
            // Validate the input parameters.
            //
            HistoryOffset offset = getOffset(parameters);
            int limit = getRecordLimit(parameters);
    
            MDC.put("offset", String.valueOf(offset));
            MDC.put("limit", String.valueOf(limit));
    
            // Fetch the records from the database and
            // fill the output structure.
            //
            Document feedDocument = createFeed(requestedView, offset, limit);
    
            // Construct the output container.
            //
            ReplicationResponseType response = new ObjectFactory().createReplicationResponseType();
            
            response.setAny(feedDocument.getFirstChild());
            
            // Log that the client successfully accessed the data.
            // Simply for audit purposes.
            //
            // The client details are included in the MDC.
            //
            logger.info("Records fetched, sending response.");
    
            return response;
        }
        catch (RuntimeException e)
        {
            throw e;
        } catch (ReplicationFault e) {
            throw e;
        }
        finally
        {
            // Clean up the thread's MDC.
            // TODO: This might fit better in a filter or intercepter.
            MDC.remove("view");
            MDC.remove("cvr");
            MDC.remove("offset");
            MDC.remove("limit");
        }
    }

    private int getRecordLimit(ReplicationRequestType parameters)
    {
        if (parameters.getMaxRecords() == null)
        {
            return MAX_RECORD_LIMIT;
        }
        else
        {
            return Math.min(parameters.getMaxRecords().intValue(), MAX_RECORD_LIMIT);
        }
    }


    private <T extends View> Document createFeed(Class<T> requestedView, HistoryOffset offset, int limit) throws ReplicationFault
    {
        List<T> results = dao.findPage(requestedView, offset.getRecordID(), offset.getModifiedDate(), limit);
        
        Document body;
        
        try {
            body = outputWriter.write(requestedView, results);
        }
        catch (IOException e) {
            
            throw new ReplicationFault("A unexpected error occured. Processing stopped.", FaultCodes.IO_ERROR, e);
        }
        
        return body;
    }
    
    private Document createSikredeFeed(HistoryOffset offset, int limit) throws SQLException
    {
        RecordXmlGenerator sikredeXmlGenerator = new RecordXmlGenerator(RecordSpecification.SIKREDE_FIELDS_SINGLETON);
        RecordPersister persister = new RecordPersister(RecordSpecification.SIKREDE_FIELDS_SINGLETON, session.connection());
        // FIXME: The persister should respect the offset and the limit. Input form THB on how to handle this nicely.
        // A first step would be to introduce modfied date to Record persistense (currently missing).
        List<Record> records = persister.fetchAllActiveRecords();
        return sikredeXmlGenerator.generateXml(records);
    }

    private Class<? extends View> getViewClass(ReplicationRequestType parameters) throws ReplicationFault
    {
        String viewPath = getViewPath(parameters);
        
        Class<? extends View> requestedView = viewClasses.get(viewPath);
        
        if (requestedView == null)
        {
            throw new ReplicationFault(format("No view with identifier register=%s, datatype=%s and version=%d can be found.", parameters.getRegister(), parameters.getDatatype(), parameters.getVersion()), FaultCodes.UNKNOWN_VIEW);
        }
        
        return requestedView;
    }


    private String getViewPath(ReplicationRequestType parameters) 
    {
        return format("%s/%s/v%d", parameters.getRegister(), parameters.getDatatype(), parameters.getVersion());
    }

    private HistoryOffset getOffset(ReplicationRequestType parameters) throws ReplicationFault
    {
        try
        {
            return new HistoryOffset(parameters.getOffset());
        }
        catch (Exception e) {
            
            throw new ReplicationFault("Invalid offset in the request.", FaultCodes.INVALID_OFFSET, e);
        }
    }
}
