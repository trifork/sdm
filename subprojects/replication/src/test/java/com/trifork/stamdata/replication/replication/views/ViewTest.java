// Stamdata - Copyright (C) 2011 National Board of e-Health (NSI)
// 
// All source code and information supplied as part of Stamdata is
// copyright to National Board of e-Health.
// 
// The source code has been released under a dual license - meaning you can
// use either licensed version of the library with your code.
// 
// It is released under the Common Public License 1.0, a copy of which can
// be found at the link below.
// http://www.opensource.org/licenses/cpl1.0.php
// 
// It is released under the LGPL (GNU Lesser General Public License), either
// version 2.1 of the License, or (at your option) any later version. A copy
// of which can be found at the link below.
// http://www.gnu.org/copyleft/lesser.html

package com.trifork.stamdata.replication.replication.views;

import java.util.Set;

import org.hibernate.Session;
import org.hibernate.exception.SQLGrammarException;
import org.junit.Test;

import com.trifork.stamdata.replication.DatabaseHelper;


public class ViewTest {
	
	@Test
	public void Should_be_able_to_query_all_views() throws Exception {
		Set<Class<?>> views = Views.findAllViews();
		DatabaseHelper db = new DatabaseHelper(views.toArray(new Class<?>[0]));
		for(Class<?> viewClass : views) {
			System.out.println(viewClass.getCanonicalName());
			Session session = db.openSession();
			session.beginTransaction();
			try {
				session.createCriteria(viewClass).list();
			}
			catch(Exception e) {
				throw new RuntimeException("Could not show view " + viewClass.getCanonicalName(), e);
			}
			finally {
				session.getTransaction().rollback();
				session.close();
			}
		}
	}
}
