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
package dk.nsi.stamdata.security;

import static dk.sosi.seal.model.constants.SubjectIdentifierTypeValues.CVR_NUMBER;

import javax.servlet.http.HttpServletRequest;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.servlet.RequestScoped;

import dk.sosi.seal.model.SystemIDCard;
import dk.sosi.seal.model.constants.SubjectIdentifierTypeValues;


public class DenGodeWebServiceModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        // Nothing to do
    }


    @Provides
    @RequestScoped
    public SystemIDCard provideSystemIDCard(HttpServletRequest request)
    {
        return (SystemIDCard) request.getAttribute(DenGodeWebServiceFilter.IDCARD_REQUEST_ATTRIBUTE_KEY);
    }
    
    @Provides
    @RequestScoped
    @ClientVocesCvr
    public String providesClientVocesCVR(SystemIDCard idCard)
    {
        if (!idCard.getSystemInfo().getCareProvider().getType().equals(SubjectIdentifierTypeValues.CVR_NUMBER))
        {
            throw new IllegalStateException("You cannot inject a client's VOCES CVR if the suplied id card is not using a CVR care provider.");
        }
        
        return idCard.getSystemInfo().getCareProvider().getID();
    }
}
