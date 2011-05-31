package com.trifork.stamdata.replication;

import java.math.BigInteger;
import java.util.Date;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.trifork.stamdata.views.cpr.Person;


public class GenerateCPRTestData {

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

		config.addAnnotatedClass(Person.class);

		SessionFactory sessionFactory = config.buildSessionFactory();
		
		Session session = sessionFactory.openSession();
		
		
		
		long cpr = 9000000000L;
		
		for (int j = 0; j < 1000; j++) {
		
		Transaction t = session.beginTransaction();
		
		for (int i = 0; i < 1000; i++) {
			
			Person person = new Person(Long.toString(cpr + i), "M", "Thomas", null, "Borlum",null, "Århus", "Vestre Ringgade", "224", null, "2", "TV", "Århus", new BigInteger("8000"), "Århus C", null, "1234567890", new Date(), "Datalog", new BigInteger("123"), new BigInteger("123"), new Date(), new Date(), new Date(), new Date(), new Date(), "TEST", "TEST");
			session.save(person);
		}

		System.out.println("DONE!");

		t.commit();
		session.flush();
		
		System.gc();
		}
		
		System.out.println("Really DONE!");
	}
}
