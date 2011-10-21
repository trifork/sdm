package dk.nsi.stamdata.replication.webservice;

import javax.jws.WebService;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.ws.Holder;

import org.w3c.dom.Document;

import com.sun.xml.ws.developer.SchemaValidation;

import dk.nsi.stamdata.replication.jaxws.Header;
import dk.nsi.stamdata.replication.jaxws.ReplicationRequestType;
import dk.nsi.stamdata.replication.jaxws.ReplicationResponseType;
import dk.nsi.stamdata.replication.jaxws.Security;
import dk.nsi.stamdata.replication.jaxws.StamdataReplication;

@WebService(serviceName = "StamdataReplication", endpointInterface = "dk.nsi.stamdata.replication.jaxws.StamdataReplication")
@SchemaValidation
public class StamdataReplicationImpl implements StamdataReplication {

    @Override
    public ReplicationResponseType getPersonDetails(
            Holder<Security> wsseHeader, Holder<Header> medcomHeader,
            ReplicationRequestType parameters) {
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document document;
        
        try {
            document = factory.newDocumentBuilder().newDocument();
            document.appendChild(document.createElement("thomas"));
            
            ReplicationResponseType response = new ReplicationResponseType();
            
            response.setAny(document.getFirstChild());
            
            return response;
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

}
