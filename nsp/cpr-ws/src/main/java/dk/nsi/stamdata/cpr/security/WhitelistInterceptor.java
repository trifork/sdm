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
package dk.nsi.stamdata.cpr.security;

import com.google.inject.servlet.RequestScoped;
import dk.nsi.stamdata.security.ClientVocesCvr;
import dk.nsi.stamdata.security.WhitelistService;
import dk.nsi.stamdata.security.Whitelisted;
import dk.oio.rep.medcom_sundcom_dk.xml.wsdl._2007._06._28.DGWSFault;
import dk.sosi.seal.model.constants.FaultCodeValues;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Provider;


@RequestScoped
class WhitelistInterceptor implements MethodInterceptor
{
    private static Logger logger = Logger.getLogger(WhitelistInterceptor.class);
    @Inject @ClientVocesCvr
    private Provider<String> clientCvrProvider;

    @Inject
    private Provider<WhitelistService> whitelistServiceProvider;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable
    {
        Whitelisted whitelistedAnnotation = invocation.getMethod().getAnnotation(Whitelisted.class);
        String serviceName = whitelistedAnnotation.value();

        String clientCvr = clientCvrProvider.get();
        WhitelistService whitelistService = whitelistServiceProvider.get();

        Object result;
        if (whitelistService.isCvrWhitelisted(clientCvr, serviceName)) {
            result = invocation.proceed();
        } else {
            logger.error("CVR '" + clientCvr + "' is not in whitelist for component name " + serviceName);
            result = null;

            // TODO!!! Get back here and throw appropriate fault
            throw new DGWSFault("The request CVR number is not authorized to use this service.", FaultCodeValues.NOT_AUTHORIZED);
        }

        return result;

    }
}


