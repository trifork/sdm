
// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
// 
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
// 
// Contributor(s): Contributors are attributed in the source code
// where applicable.
// 
// The Original Code is "Stamdata".
// 
// The Initial Developer of the Original Code is Trifork Public A/S.
// 
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
// 
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

package com.trifork.stamdata.replication.replication.views;

import java.util.Set;

import org.hibernate.Session;
import org.hibernate.exception.SQLGrammarException;
import org.junit.Test;

import com.trifork.stamdata.replication.DatabaseHelper;
import com.trifork.stamdata.views.Views;


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
