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
package com.trifork.stamdata.authorization;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;


public class DataGenerator {
	
	public static void main(String[] args) {
		
		// DISCOVER ALL ENTITY CLASSES

		Configuration config = new Configuration();

		config.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/sdm_warehouse");
		config.setProperty("hibernate.connection.username", "root");
		config.setProperty("hibernate.connection.password", "");

		config.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
		config.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLInnoDBDialect");
		config.setProperty("hibernate.connection.zeroDateTimeBehavior", "convertToNull");
		config.setProperty("hibernate.connection.characterEncoding", "utf8");

		// Not sure that the following 3 settings work.
		// They might have to be added to the JDBC url to have any effect.

		config.setProperty("hibernate.connection.useCursorFetch", "true");
		config.setProperty("hibernate.connection.useServerPrepStmts", "true");
		config.setProperty("hibernate.connection.defaultFetchSize", "1000");

		// Do not set "hibernate.c3p0.max_statements" it to anything above 0.
		// This might cause deadlocks. If you do set it set it to a very high
		// number, this will cost memory but give better performence.

		// The following two properties can be used to debug c3p0's connections.›
		// They are commented out since they are quite expensive.

		// config.setProperty("hibernate.c3p0.unreturnedConnectionTimeout", "120");
		// config.setProperty("hibernate.c3p0.debugUnreturnedConnectionStackTraces", "true");

		config.setProperty("hibernate.current_session_context_class", "thread");

		config.addAnnotatedClass(Authorization.class);

		SessionFactory sessionFactory = config.buildSessionFactory();
		
		Session session = sessionFactory.openSession();
		
		Transaction t = session.beginTransaction();
		
		Long cpr = 1000000000L;
		
		for (int i = 0; i < 45000; i++) {
			Authorization a = new Authorization(Long.toString(cpr + i), "Blah", "Blah", "12345", "1234");
			session.save(a);
		}
		
		t.commit();
	}
}
