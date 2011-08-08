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

package com.trifork.stamdata.replication;

import java.io.InputStream;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.classic.Session;


public class DatabaseHelper {

	private SessionFactory sessionFactory;

	public DatabaseHelper(Class<?>... entities) throws Exception {

		Properties props = new Properties();

		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties");
		props.load(inputStream);
		inputStream.close();

		org.hibernate.cfg.Configuration config = new org.hibernate.cfg.Configuration();

		config.setProperty("hibernate.connection.driver_class", props.getProperty("db.connection.driverClass"));
		config.setProperty("hibernate.dialect", props.getProperty("db.connection.sqlDialect"));
		config.setProperty("hibernate.connection.url", props.getProperty("db.connection.jdbcURL"));

		config.setProperty("hibernate.connection.username", props.getProperty("db.connection.username"));
		config.setProperty("hibernate.connection.password", props.getProperty("db.connection.password", null));

		config.setProperty("hibernate.connection.zeroDateTimeBehavior", "convertToNull");
		config.setProperty("hibernate.connection.characterEncoding", "utf8");

		config.setProperty("hibernate.current_session_context_class", "thread");
		config.setProperty("hibernate.cache.provider_class", "org.hibernate.cache.NoCacheProvider");

		for (Class<?> entity : entities) {
			config.addAnnotatedClass(entity);
		}

		sessionFactory = config.buildSessionFactory();
	}

	public Session openSession() {
		return sessionFactory.openSession();
	}
	
	public StatelessSession openStatelessSession() {
		return sessionFactory.openStatelessSession();
	}
}
