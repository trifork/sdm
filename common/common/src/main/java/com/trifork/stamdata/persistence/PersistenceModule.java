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

import static com.google.inject.Key.get;
import static com.google.inject.name.Names.named;

import java.util.Set;

import javax.inject.Named;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.cfg.Configuration;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.trifork.stamdata.Nullable;


public class PersistenceModule extends AbstractModule
{
    private static final TypeLiteral<Set<Object>> A_SET_OF_OBJECTS = new TypeLiteral<Set<Object>>() {};

    public static final String JDBC_URL_PROP = "db.connection.jdbcURL";
    public static final String DB_USERNAME_PROP = "db.connection.username";
    public static final String DB_PASSWORD_PROP = "db.connection.password";


    @Override
    protected void configure()
    {
        requireBinding(get(A_SET_OF_OBJECTS, Persistent.class));

        requireBinding(get(String.class, named(JDBC_URL_PROP)));
        requireBinding(get(String.class, named(DB_USERNAME_PROP)));
        // We don't require a password since it might not be set.

        install(new TransactionalModule());
    }


    @Provides
    @Singleton
    protected SessionFactory provideSessionFactory(@Persistent Set<Object> entities, @Named(JDBC_URL_PROP) String jdbcURL, @Named(DB_USERNAME_PROP) String username, @Nullable @Named(DB_PASSWORD_PROP) String password)
    {
        Configuration config = new Configuration();

        config.setProperty("hibernate.connection.url", jdbcURL);
        config.setProperty("hibernate.connection.username", username);
        config.setProperty("hibernate.connection.password", password);

        // TODO: These can be configurable to e.g. allow in-memory databases.

        config.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
        config.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLInnoDBDialect");

        config.setProperty("hibernate.connection.characterEncoding", "utf8");

        // Set the default behavior of dates fetched from the database to be
        // converted to null. The alternative 00-00-0000 or similar is strange.

        config.setProperty("hibernate.connection.zeroDateTimeBehavior", "convertToNull");

        config.setProperty("hibernate.transaction.factory_class", "org.hibernate.transaction.JDBCTransactionFactory");
        config.setProperty("hibernate.current_session_context_class", "thread");

        // TODO: Look into caching.

        config.setProperty("hibernate.cache.provider_class", "org.hibernate.cache.NoCacheProvider");

        // Since we are not using the exact same version as provided
        // on JBoss we need to disable search and validator registration.
        //
        // If we don't do this Hibernate will use the provided version
        // of search and validator which do not match the ones in the war.

        config.setProperty("hibernate.validator.autoregister_listeners", "false");
        config.setProperty("hibernate.search.autoregister_listeners", "false");

        // Use a C3P0 connection pool.
        // The default connection pool is not meant for production use.
        //
        // TODO: Make this configurable.

        config.setProperty("hibernate.c3p0.min_size", "5");
        config.setProperty("hibernate.c3p0.max_size", "20");
        config.setProperty("hibernate.c3p0.timeout", "200");

        // Lastly register all the entities.

        for (Object entity : entities)
        {
            config.addAnnotatedClass(entity.getClass());
        }

        return config.buildSessionFactory();
    }


    @Provides
    protected Session provideSession(SessionFactory factory)
    {
        return factory.getCurrentSession();
    }


    @Provides
    protected StatelessSession provideStatelessSession(SessionFactory factory)
    {
        return factory.openStatelessSession();
    }
}
