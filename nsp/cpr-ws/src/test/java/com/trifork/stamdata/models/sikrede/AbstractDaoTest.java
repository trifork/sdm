package com.trifork.stamdata.models.sikrede;

import static org.junit.Assert.assertNotNull;

import java.sql.SQLException;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.trifork.stamdata.Fetcher;
import com.trifork.stamdata.models.BaseTemporalEntity;

import dk.nsi.stamdata.cpr.ApplicationController;

public abstract class AbstractDaoTest
{
	protected static Session session;
	protected Fetcher fetcher;

	@Before
	public void setupSession()
	{
		Injector injector = Guice.createInjector(Stage.DEVELOPMENT, new ApplicationController.ComponentModule());

		session = injector.getInstance(Session.class);
		fetcher = injector.getInstance(Fetcher.class);
	}

	protected void purgeTable(String table)
	{
		assertNotNull(table);
		session.createSQLQuery("TRUNCATE " + table).executeUpdate();
	}

	protected void insertInTable(BaseTemporalEntity entity)
	{
		session.getTransaction().begin();
		session.save(entity);
		session.flush();
		session.getTransaction().commit();
	}

	@Test
	public abstract void verifyMapping() throws SQLException;

}
