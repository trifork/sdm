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
import java.util.Map;

import javax.inject.Provider;
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
import com.trifork.stamdata.persistence.RecordFetcher;
import com.trifork.stamdata.persistence.RecordSpecification;
import com.trifork.stamdata.specs.BemyndigelseRecordSpecs;
import com.trifork.stamdata.specs.SikredeRecordSpecs;
import com.trifork.stamdata.specs.TilskudsblanketRecordSpecs;
import com.trifork.stamdata.specs.VaccinationRecordSpecs;
import com.trifork.stamdata.specs.VitaminRecordSpecs;
import com.trifork.stamdata.specs.YderregisterRecordSpecs;

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
import dk.nsi.stamdata.views.View;

@WebService(endpointInterface="dk.nsi.stamdata.jaxws.generated.StamdataReplication")
@HandlerChain(file="handler-chain.xml")
@GuiceWebservice
@SchemaValidation
public class StamdataReplicationImpl implements StamdataReplication {

    private static final Logger logger = Logger.getLogger(StamdataReplicationImpl.class);
    
    private static final int MAX_RECORD_LIMIT = 2000;
    
    private final String cvr;
    private final Map<String, Class<? extends View>> viewClasses;
    private final ClientDao clients;

    private final Provider<RecordFetcher> fetchers;

    @Inject
    private DynamicViewMapper dynamicViewMapper;

    @Inject
    private DynamicRowFetcher dynamicRowFetcher;

    @Inject DynamicViewXmlGenerator dynamicViewXmlGenerator;

    @Inject
    StamdataReplicationImpl(@ClientVocesCvr String cvr, ClientDao clientDao, Map<String, Class<? extends View>> viewClasses, Provider<RecordFetcher> fetchers)
    {
        this.cvr = cvr;
        this.clients = clientDao;
        this.viewClasses = viewClasses;
        this.fetchers = fetchers;
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
            // During the transition to the new architecture we will have
            // to handle some registers differently.
            //
            /*if (isRecordRegister(parameters)) {
                return handleRequestUsingRecords(wsseHeader, medcomHeader, parameters);
            } else if (isHibernateView(parameters)) {
                return handleRequestUsingHibernateView(wsseHeader, medcomHeader, parameters);
            } else {*/
                return handleRequestUsingDynamicViews(wsseHeader, medcomHeader, parameters);
            //}
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

    private boolean isRecordRegister(ReplicationRequestType parameters)
    {
        return (//"sikrede".equals(parameters.getRegister())&& "sikrede".equals(parameters.getDatatype()) && parameters.getVersion() == 1)
                //|| ("yderregister".equals(parameters.getRegister()) && "yder".equals(parameters.getDatatype()) && parameters.getVersion() == 1)
                //|| ("yderregister".equals(parameters.getRegister()) && "person".equals(parameters.getDatatype()) && parameters.getVersion() == 1)
                //"bemyndigelsesservice".equals(parameters.getRegister()) && "bemyndigelse".equals(parameters.getDatatype()) && parameters.getVersion() == 1)
                "vitamin".equals(parameters.getRegister()) && parameters.getVersion() == 1)
                || ("tilskudsblanket".equals(parameters.getRegister()) && parameters.getVersion() == 1)
                || ("ddv".equals(parameters.getRegister()) && parameters.getVersion() == 1);
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

    private ReplicationResponseType handleRequestUsingRecords(Holder<Security> wsseHeader, Holder<Header> medcomHeader, ReplicationRequestType parameters) throws ReplicationFault
    {
        try {
            String viewPath = getViewPath(parameters);

            MDC.put("view", String.valueOf(viewPath));
            MDC.put("cvr", String.valueOf(cvr));
            
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
            RecordSpecification recordSpecification = null;
            if("sikrede".equals(parameters.getRegister())) {
                recordSpecification = SikredeRecordSpecs.ENTRY_RECORD_SPEC;
            } else if ("yderregister".equals(parameters.getRegister())) {
                if("yder".equals(parameters.getDatatype())) {
                    recordSpecification = YderregisterRecordSpecs.YDER_RECORD_TYPE;
                } else if("person".equals(parameters.getDatatype())) {
                    recordSpecification = YderregisterRecordSpecs.PERSON_RECORD_TYPE;
                } else {
                    throw new IllegalStateException("Datatype: '"+parameters.getDatatype()+"' not known on register '"+parameters.getRegister()+"'");
                }
            } else if ("bemyndigelsesservice".equals(parameters.getRegister())) {
                if("bemyndigelse".equals(parameters.getDatatype())) {
                    recordSpecification = BemyndigelseRecordSpecs.ENTRY_RECORD_SPEC;
                } else {
                    throw new IllegalStateException("Datatype: '"+parameters.getDatatype()+"' not known on register '"+parameters.getRegister()+"'");
                }
            }
            else if ("vitamin".equals(parameters.getRegister())) {
                if("grunddata".equals(parameters.getDatatype())) {
                    recordSpecification = VitaminRecordSpecs.GRUNDDATA_RECORD_SPEC;
                } else if("firmadata".equals(parameters.getDatatype())) {
                    recordSpecification = VitaminRecordSpecs.FIRMADATA_RECORD_SPEC;
                } else if("udgaaedenavne".equals(parameters.getDatatype())) {
                    recordSpecification = VitaminRecordSpecs.UDGAAEDENAVNE_RECORD_SPEC;
                } else if("indholdsstoffer".equals(parameters.getDatatype())) {
                    recordSpecification = VitaminRecordSpecs.INDHOLDSSTOFFER_RECORD_SPEC;
                } else {
                    throw new IllegalStateException("Datatype: '"+parameters.getDatatype()+"' not known on register '"+parameters.getRegister()+"'");
                }
            } else if ("ddv".equals(parameters.getRegister())) {
                if("diseases".equals(parameters.getDatatype())) {
                    recordSpecification = VaccinationRecordSpecs.DISEASES_RECORD_SPEC;
                } else if("diseases_vaccines".equals(parameters.getDatatype())) {
                    recordSpecification = VaccinationRecordSpecs.DISEASESVACCINES_RECORD_SPEC;
                } else if("dosageoptions".equals(parameters.getDatatype())) {
                    recordSpecification = VaccinationRecordSpecs.DOSAGEOPTIONS_RECORD_SPEC;
                } else if("ssidrugs".equals(parameters.getDatatype())) {
                    recordSpecification = VaccinationRecordSpecs.SSIDRUGS_RECORD_SPEC;
                } else if("vaccinationplanitems".equals(parameters.getDatatype())) {
                    recordSpecification = VaccinationRecordSpecs.VACCINATIONPLANITEMS_RECORD_SPEC;
                } else if("vaccinationplan".equals(parameters.getDatatype())) {
                    recordSpecification = VaccinationRecordSpecs.VACCINATIONPLANS_RECORD_SPEC;
                } else if("vaccines".equals(parameters.getDatatype())) {
                    recordSpecification = VaccinationRecordSpecs.VACCINES_RECORD_SPEC;
                } else if("vaccines_drugs".equals(parameters.getDatatype())) {
                    recordSpecification = VaccinationRecordSpecs.VACCINESDRUGS_RECORD_SPEC;
                } else {
                    throw new IllegalStateException("Datatype: '"+parameters.getDatatype()+"' not known on register '"+parameters.getRegister()+"'");
                }
            } else if ("tilskudsblanket".equals(parameters.getRegister())) {
                if("forhoejettakst".equals(parameters.getDatatype())) {
                    recordSpecification = TilskudsblanketRecordSpecs.FORHOEJETTAKST_RECORD_SPEC;
                } else if("blanket".equals(parameters.getDatatype())) {
                    recordSpecification = TilskudsblanketRecordSpecs.BLANKET_RECORD_SPEC;
                } else if("blanketterminal".equals(parameters.getDatatype())) {
                    recordSpecification = TilskudsblanketRecordSpecs.BLANKET_TERMINALTILSKUD_RECORD_SPEC;
                } else if("blanketforhoejet".equals(parameters.getDatatype())) {
                    recordSpecification = TilskudsblanketRecordSpecs.BLANKET_FORHOJETTILSKUD_RECORD_SPEC;
                } else if("blanketkroniker".equals(parameters.getDatatype())) {
                    recordSpecification = TilskudsblanketRecordSpecs.BLANKET_KRONIKERTILSKUD_RECORD_SPEC;
                } else if("blanketenkelt".equals(parameters.getDatatype())) {
                    recordSpecification = TilskudsblanketRecordSpecs.BLANKET_ENKELTTILSKUD_RECORD_SPEC;
                } else {
                    throw new IllegalStateException("Datatype: '"+parameters.getDatatype()+"' not known on register '"+parameters.getRegister()+"'");
                }
            } else {
                throw new IllegalStateException("Datatype: '"+parameters.getDatatype()+"' not known on register '"+parameters.getRegister()+"'");
            }
//            Document feedDocument = createFeed(recordSpecification, parameters, offset, limit);
    
            // Construct the output container.
            //
            ReplicationResponseType response = new ObjectFactory().createReplicationResponseType();
            
//            response.setAny(feedDocument.getFirstChild());

            // Log that the client successfully accessed the data.
            // Simply for audit purposes.
            //
            // The client details are included in the MDC.
            //
            logger.info("Records fetched, sending response.");
    
            return response;
        }
        catch (ReplicationFault e)
        {
            // We don't want to wrap replication faults further,
            // so catch and rethrow them here.
            //
            throw e;
        }
        catch (Exception e)
        {
            throw new ReplicationFault("Could not complete the request do to an error.", FaultCodes.INTERNAL_ERROR, e);
        }
        finally
        {
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


    private Class<? extends View> getViewClass(ReplicationRequestType parameters) throws ReplicationFault
    {
        String viewPath = getViewPath(parameters);
        
        Class<? extends View> requestedView = viewClasses.get(viewPath);
        
        if (requestedView == null) {
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
        try {
            return new HistoryOffset(parameters.getOffset());
        } catch (Exception e) {
            throw new ReplicationFault("Invalid offset in the request.", FaultCodes.INVALID_OFFSET, e);
        }
    }
}
