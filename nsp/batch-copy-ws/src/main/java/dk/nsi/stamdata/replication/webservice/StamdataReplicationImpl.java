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
import java.util.Map;

import javax.jws.WebService;
import javax.xml.ws.Holder;

import org.hibernate.ScrollableResults;
import org.w3c.dom.Document;

import com.google.inject.Inject;
import com.sun.xml.ws.developer.SchemaValidation;
import com.trifork.stamdata.jaxws.GuiceInstanceResolver.GuiceWebservice;

import dk.nsi.dgws.ClientVocesCvr;
import dk.nsi.stamdata.replication.jaxws.Header;
import dk.nsi.stamdata.replication.jaxws.ObjectFactory;
import dk.nsi.stamdata.replication.jaxws.ReplicationFault;
import dk.nsi.stamdata.replication.jaxws.ReplicationRequestType;
import dk.nsi.stamdata.replication.jaxws.ReplicationResponseType;
import dk.nsi.stamdata.replication.jaxws.Security;
import dk.nsi.stamdata.replication.jaxws.StamdataReplication;
import dk.nsi.stamdata.replication.models.Client;
import dk.nsi.stamdata.replication.models.ClientDao;
import dk.nsi.stamdata.views.View;

@GuiceWebservice
@SchemaValidation
@WebService(serviceName = "StamdataReplication", endpointInterface = "dk.nsi.stamdata.replication.jaxws.StamdataReplication")
public class StamdataReplicationImpl implements StamdataReplication {

    private static final int MAX_RECORD_LIMIT = 2000;
    
    private final String cvr;
    private final RecordDao dao;
    private final Map<String, Class<? extends View>> viewClasses;
    private final ClientDao clients;

    private final AtomFeedWriter outputWriter;


    @Inject
    StamdataReplicationImpl(@ClientVocesCvr String cvr, RecordDao dao, ClientDao clients, Map<String, Class<? extends View>> viewClasses, AtomFeedWriter outputWriter) {
        
        this.cvr = cvr;
        this.dao = dao;
        this.clients = clients;
        this.viewClasses = viewClasses;
        this.outputWriter = outputWriter;
    }
    
    @Override
    public ReplicationResponseType replicate(Holder<Security> wsseHeader, Holder<Header> medcomHeader, ReplicationRequestType parameters) throws ReplicationFault
    {
        Class<? extends View> requestedView = getViewClass(parameters);
        
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
        
        // Fetch the records from the database and
        // fill the output structure.
        //
        ScrollableResults results = dao.findPage(requestedView, offset.getRecordID(), offset.getModifiedDate(), limit);
        Document feedDocument = createFeed(requestedView, results);
        
        // Construct the output container.
        //
        ReplicationResponseType response = new ObjectFactory().createReplicationResponseType();
        response.setAny(feedDocument.getFirstChild());
        
        return response;
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


    private Document createFeed(Class<? extends View> requestedView, ScrollableResults results) throws ReplicationFault
    {
        Document body;
        
        try {
            body = outputWriter.write(requestedView, results);
        }
        catch (IOException e) {
            
            throw new ReplicationFault("A unexpected error occured. Processing stopped.", FaultCodes.IO_ERROR, e);
        }
        
        return body;
    }

    private Class<? extends View> getViewClass(ReplicationRequestType parameters) throws ReplicationFault
    {
        String viewPath = format("%s/%s/v%d", parameters.getRegister(), parameters.getDatatype(), parameters.getVersion());
        
        Class<? extends View> requestedView = viewClasses.get(viewPath);
        
        if (requestedView == null)
        {
            throw new ReplicationFault(format("No view with identifier register=%s, datatype=%s and version=%d can be found.", parameters.getRegister(), parameters.getDatatype(), parameters.getVersion()), FaultCodes.UNKNOWN_VIEW);
        }
        
        return requestedView;
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
