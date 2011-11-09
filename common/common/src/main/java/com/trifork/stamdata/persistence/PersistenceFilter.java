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

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;

@Singleton
public class PersistenceFilter implements Filter
{
	public static String HIBERNATE_SESSION_KEY = "dk.nsi.stamdata.cpr.session";

	@Inject
	private Provider<Session> sessionProvider;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		// Each request is wrapped in a transaction.

		Session session = null;

		try
		{
			session = sessionProvider.get();
			session.beginTransaction();
			Transaction transaction = session.beginTransaction();
			transaction.setTimeout(5);

			chain.doFilter(request, response);

			session.getTransaction().commit();
		}
		catch (Exception e)
		{
			try
			{
				session.getTransaction().rollback();
			}
			catch (Exception ex)
			{

			}
			
			// Let other filters handle the exception.
			
			throw new ServletException(e);
		}
	}


	@Override
	public void init(FilterConfig config) throws ServletException {}


	@Override
	public void destroy() {}
}
