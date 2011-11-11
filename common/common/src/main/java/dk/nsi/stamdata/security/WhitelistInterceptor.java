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

import javax.inject.Inject;
import javax.inject.Provider;

import com.google.inject.servlet.RequestScoped;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.type.StandardBasicTypes;

import java.util.List;


@RequestScoped
class WhitelistInterceptor implements MethodInterceptor
{
    @Inject
    private Provider<Session> sessionProvider;

    @Inject @ClientVocesCvr
    private Provider<String> clientCvrProvider;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable
    {
        Object result;

        Whitelisted whitelisted = invocation.getMethod().getAnnotation(Whitelisted.class);
        String componentName = whitelisted.value();
        String clientCvr = clientCvrProvider.get();

        Session session = sessionProvider.get();
        SQLQuery query = session.createSQLQuery("SELECT cvr FROM whitelist_config WHERE component_name=? AND cvr=?");
        query.setString(0, componentName);
        query.setString(1, clientCvr);
        query.addScalar("cvr", StandardBasicTypes.LONG);
        List cvrs = query.list();

        if (cvrs.size() == 1) {
            System.err.println("=================>>>>>> " + clientCvr + " FOUND in whitelist for component " + componentName);
            result = invocation.proceed();
        } else {
            System.err.println("----------------->>>>>> " + clientCvr + " not in whitelist for component " + componentName);
            result = null;
            //Throw some generic service error containing the component name
        }

/*        if (!session.getTransaction().isActive())
        {
                System.err.println("------------> Before calling @Whitelisted method --- CVR: " + clientCvrProvider.get() + " --- " + componentName );
                result = invocation.proceed();
                System.err.println("------------> After calling @Whitelisted method");
                //session.getTransaction().commit();
        }
        else
        {
            System.err.println("------------> Before calling @Whitelisted method --- CVR: " + clientCvrProvider.get() + " --- " + componentName );
            result = invocation.proceed();
            System.err.println("------------> After calling @Whitelisted method");
        }
*/
        return result;
    }
}


