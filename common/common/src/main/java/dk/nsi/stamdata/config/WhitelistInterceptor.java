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
package dk.nsi.stamdata.config;

import javax.inject.Inject;
import javax.inject.Provider;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;


class WhitelistInterceptor implements MethodInterceptor
{
    @Inject
    private Provider<Session> sessionProvider;


    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable
    {
        Object result;

        Session session = sessionProvider.get();
//        SQLQuery query = session.createSQLQuery("SELECT cvr FROM WHITELIST WHERE component=?");

        if (!session.getTransaction().isActive())
        {
                System.err.println("------------> Before calling @Whitelisted method");
                result = invocation.proceed();
                System.err.println("------------> After calling @Whitelisted method");
                //session.getTransaction().commit();
        }
        else
        {
            System.err.println("------------> Before calling @Whitelisted method");
            result = invocation.proceed();
            System.err.println("------------> After calling @Whitelisted method");
        }

        return result;
    }
}


