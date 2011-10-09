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

package com.trifork.stamdata.replication.security.dgws;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import dk.nsi.dgws.ClientVocesCvr;


/**
 * A servlet that handles authorization requests with DGWS.
 * 
 * @author Thomas Børlum <thb@trifork.com>
 */
@Singleton
public class AuthorizationServlet extends HttpServlet
{
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationServlet.class);

    private final Provider<String> requestVocesCvrProvider;


    @Inject
    AuthorizationServlet(@ClientVocesCvr Provider<String> requestVocesCvrProvider)
    {
        this.requestVocesCvrProvider = requestVocesCvrProvider;
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        try
        {
            SOAPFactory.newInstance();
        }
        catch (SOAPException e)
        {
            e.printStackTrace();
        }
    }


    private static final long serialVersionUID = 1L;
}
