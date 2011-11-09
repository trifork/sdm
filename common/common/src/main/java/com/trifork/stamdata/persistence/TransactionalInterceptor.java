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
package com.trifork.stamdata.persistence;

import javax.inject.Inject;
import javax.inject.Provider;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.hibernate.Session;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;


class TransactionalInterceptor implements MethodInterceptor
{
    @Inject
    private Provider<Session> sessionProvider;


    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable
    {
        Object result;

        Session session = sessionProvider.get();

        if (!session.getTransaction().isActive())
        {
            try
            {
                session.beginTransaction();
                result = invocation.proceed();
                session.getTransaction().commit();
            }
            catch (Exception e)
            {
                try
                {
                    if (session.getTransaction().isActive())
                    {
                        session.getTransaction().rollback();
                    }
                }
                catch (Exception ex)
                {

                }
                
                throw e;
            }
        }
        else
        {
            result = invocation.proceed();
        }

        return result;
    }
}


class TransactionalModule extends AbstractModule
{
    public void configure()
    {
        TransactionalInterceptor transactionalInterceptor = new TransactionalInterceptor();
        requestInjection(transactionalInterceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Transactional.class), transactionalInterceptor);
    }
}
