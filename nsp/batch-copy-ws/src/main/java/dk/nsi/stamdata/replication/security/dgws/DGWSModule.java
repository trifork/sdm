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

package dk.nsi.stamdata.replication.security.dgws;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.google.inject.Provides;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.servlet.ServletModule;
import com.trifork.stamdata.persistence.Persistent;

import dk.nsi.stamdata.replication.models.Client;
import dk.nsi.stamdata.replication.security.SecurityManager;


public class DGWSModule extends ServletModule
{
    private JAXBContext jaxbContext;


    @Override
    protected void configureServlets()
    {
        Multibinder<Object> persistent = Multibinder.newSetBinder(binder(), Object.class, Persistent.class);
        persistent.addBinding().to(Authorization.class);
        persistent.addBinding().to(Client.class);

        // BIND THE SECURITY MANAGER
        //
        // The binding is required by the replication module.

        bind(SecurityManager.class).to(DGWSSecurityManager.class);

        // SERVE THE SOAP AUTHENTICATION SERVICE
        //
        // This servlet takes DGWS requests, authenticates and authorizes
        // them, and returns a replication token if authorized.

        serve("/authenticate").with(AuthorizationServlet.class);

        // XML MARSHALLING
        //
        // The marshallers are used to marshal the SOAP
        // bodies to and from XML.
        //
        // @see AuthorizationRequestStructure
        // @see AuthorizationResponseStructure

        try
        {
            jaxbContext = JAXBContext.newInstance(AuthorizationRequestStructure.class, AuthorizationResponseStructure.class);
        }
        catch (JAXBException e)
        {
            addError(e);
        }
    }


    @Provides
    protected Marshaller provideMarshaller() throws JAXBException
    {
        return jaxbContext.createMarshaller();
    }


    @Provides
    protected Unmarshaller provideUnmarshaller() throws JAXBException
    {
        return jaxbContext.createUnmarshaller();
    }
}
