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
package dk.nsi.stamdata.cpr.pvit.proxy;

import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.ws.Holder;

import org.joda.time.DateTime;

import com.trifork.stamdata.persistence.Transactional;

import dk.nsi.stamdata.jaxws.generated.Header;
import dk.nsi.stamdata.jaxws.generated.Security;


public class CprSubscriptionClient
{
    private static final String MARSHALLER_PROP = "com.sun.xml.bind.namespacePrefixMapper";

    public static final String HOST_PROPERTY_NAME = "cprabbs.service.endpoint.host";
    public static final String PORT_PROPERTY_NAME = "cprabbs.service.endpoint.port";
    public static final String PATH_PROPERTY_NAME = "cprabbs.service.endpoint.path";
    
    private String host;
    private int port;
    private String path;

    @Inject
    CprSubscriptionClient(@Named(HOST_PROPERTY_NAME) String host, @Named(PORT_PROPERTY_NAME) String port, @Named(PATH_PROPERTY_NAME) String path) throws MalformedURLException
    {
        this.host = host;
        this.port = Integer.parseInt(port);
        this.path = path;
    }
    
    @Transactional
    public List<String> getChangedCprs(Holder<Security> wsseHeader, Holder<Header> medcomHeader, DateTime since) throws CprAbbsException
    {
        String header = createDgwsHeaderString(wsseHeader, medcomHeader);
        
        String body = createCprAbbsRequestBody(since);
        
        SimpleSoapBuilder simpleSoapBuilder = new SimpleSoapBuilder();
        String soapCallMessage = simpleSoapBuilder.createSoapMessage(header, body);
        
        PostXml postXml = new PostXml();
        String result;
        try {
            result = postXml.postXml(host, port, path, soapCallMessage);
            if(!result.contains("200 OK")) {
                throw new CprAbbsException(result);
            }
        } catch (IOException e) {
            throw new CprAbbsException(e);
        }
        
        CprAbbsResponseParser responseParser = new CprAbbsResponseParser();
        return responseParser.extractCprNumbers(result);
    }

    private String createDgwsHeaderString(Holder<Security> wsseHeader, Holder<Header> medcomHeader) throws CprAbbsException {
        String wsseHeaderAsString;
        String medcomHeaderAsString;
        try {
            wsseHeaderAsString = marshalJaxbToString(Security.class, wsseHeader.value);
            medcomHeaderAsString = marshalJaxbToString(Header.class, medcomHeader.value);
        } catch (JAXBException e) {
            throw new CprAbbsException(e);
        }
        String header = wsseHeaderAsString + medcomHeaderAsString;
        return header;
    }

    private String createCprAbbsRequestBody(DateTime since) {
        CprAbbsBodyBuilder cprAbbsBodyBuilder = new CprAbbsBodyBuilder();
        String body = null;
        if(since == null)
        {
            body = cprAbbsBodyBuilder.createCprAbbsSoapBody();
        }
        else
        {
            body = cprAbbsBodyBuilder.createCprAbbsSoapBody(since);
        }
        return body;
    }
    
    private String marshalJaxbToString(Class<?> clazz, Object jaxbObject) throws JAXBException
    {
        // TODO: Marshallers are expensive. Consider pooling them (see Behandlingsrelationsservice)
        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty("jaxb.fragment", true);
        marshaller.setProperty(MARSHALLER_PROP, new SealNamespacePrefixMapper());
        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(jaxbObject, stringWriter);
        return stringWriter.toString();
    }
}
