package com.trifork.stamdata.models.sikrede;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import com.trifork.stamdata.ConfigurationLoader;
import com.trifork.stamdata.Fetcher;
import com.trifork.stamdata.models.BaseTemporalEntity;
import dk.nsi.stamdata.cpr.ApplicationController;
import dk.nsi.stamdata.cpr.SessionProvider;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;

/**
 * User: frj
 * Date: 9/20/11
 * Time: 2:15 PM
 *
 * @Author frj
 */
public abstract class AbstractDaoTest {
    protected static Session session;
    protected Fetcher fetcher;

    @Before
    public void setupSession() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                Properties properties = ConfigurationLoader.loadForName(ApplicationController.COMPONENT_NAME);
                bind(Session.class).toProvider(SessionProvider.class);
                Names.bindProperties(binder(), properties);
            }
        });

        session = injector.getInstance(Session.class);
        fetcher = injector.getInstance(Fetcher.class);
    }

    protected void purgeTable(String table) {
        assertNotNull(table);
        session.createSQLQuery("TRUNCATE " + table).executeUpdate();
    }

    protected void insertInTable(BaseTemporalEntity entity) {
        session.getTransaction().begin();
        session.save(entity);
        session.flush();
        session.getTransaction().commit();
    }

    @Test
    public abstract void verifyMapping() throws SQLException;

}
