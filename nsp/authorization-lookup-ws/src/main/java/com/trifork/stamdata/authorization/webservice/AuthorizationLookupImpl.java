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

package com.trifork.stamdata.authorization.webservice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jws.WebService;
import javax.xml.ws.Holder;

import com.google.inject.Inject;
import com.sun.xml.ws.developer.SchemaValidation;
import com.trifork.stamdata.authorization.models.Authorization;
import com.trifork.stamdata.authorization.models.AuthorizationDao;
import com.trifork.stamdata.jaxws.GuiceInstanceResolver.GuiceWebservice;

import dk.nsi.stamdata.security.ClientVocesCvr;
import dk.nsi.stamdata.jaxws.generated.AuthorizationPortType;
import dk.nsi.stamdata.jaxws.generated.AuthorizationRequestType;
import dk.nsi.stamdata.jaxws.generated.AuthorizationResponseType;
import dk.nsi.stamdata.jaxws.generated.AuthorizationType;
import dk.nsi.stamdata.jaxws.generated.DGWSFault;
import dk.nsi.stamdata.jaxws.generated.Header;
import dk.nsi.stamdata.jaxws.generated.ObjectFactory;
import dk.nsi.stamdata.jaxws.generated.Security;
import dk.sosi.seal.model.constants.FaultCodeValues;


@WebService(endpointInterface="dk.nsi.stamdata.jaxws.generated.AuthorizationPortType")
@GuiceWebservice
@SchemaValidation
public class AuthorizationLookupImpl implements AuthorizationPortType
{
	private final Set<String> whitelist;
	private final AuthorizationDao authorizationDao;
    private final String cvr;

	@Inject
	AuthorizationLookupImpl(@ClientVocesCvr String cvr, Set<String> whitelist, AuthorizationDao authorizationDao)
	{
		this.cvr = cvr;
        this.authorizationDao = authorizationDao;
		this.whitelist = whitelist;
	}

	@Override
	public AuthorizationResponseType authorization(Holder<Security> wsseHeader,
	        Holder<Header> medcomHeader, AuthorizationRequestType parameters)
	        throws DGWSFault {

        if (!whitelist.contains(cvr))
        {
            throw new DGWSFault("The request CVR number is not authorized to use this service.", FaultCodeValues.NOT_AUTHORIZED);
        }
        
        List<Authorization> authorizations = authorizationDao.getAuthorizations(parameters.getCpr());

        AuthorizationResponseType response = new ObjectFactory().createAuthorizationResponseType();
        response.setCpr(parameters.getCpr());
        
        if (!authorizations.isEmpty())
        {
            response.setFirstName(authorizations.get(0).firstName);
            response.setLastName(authorizations.get(0).lastName);
        }
        
        for (Authorization authorization : authorizations)
        {
            AuthorizationType authorizationType = new ObjectFactory().createAuthorizationType();
            authorizationType.setAuthorizationCode(authorization.authorizationCode);
            authorizationType.setEducationCode(authorization.educationCode);
            
            // Map the name of the education if we know it.
            
            if (educations.containsKey(authorization.educationCode))
            {
                authorizationType.setEducationName(educations.get(authorization.educationCode));
            }
            
            response.getAuthorization().add(authorizationType);
        }

        return response;
    }
    
    private static Map<String,String> educations = new HashMap<String, String>(); {

        educations.put("4498", "Optiker");
        educations.put("5151", "Fysioterapeut");
        educations.put("5153", "Ergoterapeut");
        educations.put("5155", "Fodterapeut");
        educations.put("5158", "Radiograf");
        educations.put("5159", "Bioanalytiker");
        educations.put("5166", "Sygeplejerske");
        educations.put("5175", "Jordemoder");
        educations.put("5265", "Kiropraktor");
        educations.put("5431", "Tandplejer");
        educations.put("5432", "Klinisk Tandtekniker");
        educations.put("5433", "Tandlæge");
        educations.put("5451", "Klinisk diætist");
        educations.put("7170", "Læge");
        educations.put("9495", "Bandagist");
    }
}
