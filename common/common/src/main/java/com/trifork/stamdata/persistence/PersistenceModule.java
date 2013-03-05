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

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.cfg.Configuration;

import javax.inject.Named;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Set;

import static com.google.inject.Key.get;
import static com.google.inject.name.Names.named;

public class PersistenceModule extends AbstractModule
{
    private static final TypeLiteral<Set<Object>> A_SET_OF_OBJECTS = new TypeLiteral<Set<Object>>() {};

    public static final String JNDI_DATASOURCE_NAME = "sdm.JNDIName";

    public static final String JDBC_URL_PROP = "db.connection.jdbcURL";
    public static final String DB_USERNAME_PROP = "db.connection.username";
    public static final String DB_PASSWORD_PROP = "db.connection.password";

    @Override
    protected void configure()
    {
        requireBinding(get(A_SET_OF_OBJECTS, Persistent.class));
        requireBinding(get(String.class, named(JNDI_DATASOURCE_NAME)));

        install(new TransactionalModule());
    }


    @Provides
    @Singleton
    protected SessionFactory provideSessionFactory(@Persistent Set<Object> entities,
                                                   @Named(JNDI_DATASOURCE_NAME) String jndiDatasource,
                                                   @Named(JDBC_URL_PROP) String jdbcUrl,
                                                   @Named(DB_USERNAME_PROP) String jdbcUsername,
                                                   @Named(DB_PASSWORD_PROP) String jdbcPassword)
    {
        Configuration config = new Configuration();
        // In tests jndiDatasource is empty and we use jdbc directly
        if (jndiDatasource == null || jndiDatasource.length() == 0) {
            config.setProperty("hibernate.connection.url", jdbcUrl);
            config.setProperty("hibernate.connection.username", jdbcUsername);
            config.setProperty("hibernate.connection.password", jdbcPassword);
        } else {
            config.setProperty("hibernate.connection.datasource", jndiDatasource);
            config.setProperty("hibernate.transaction.manager_lookup_class",
                    "org.hibernate.transaction.JBossTransactionManagerLookup");
        }
        config.setProperty("hibernate.transaction.factory_class", "org.hibernate.transaction.JDBCTransactionFactory");

        config.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
        config.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLInnoDBDialect");
        config.setProperty("hibernate.connection.characterEncoding", "utf8");

        // Set the default behavior of dates fetched from the database to be
        // converted to null. The alternative 00-00-0000 or similar is strange.
        config.setProperty("hibernate.connection.zeroDateTimeBehavior", "convertToNull");
        config.setProperty("hibernate.current_session_context_class", "thread");
        config.setProperty("hibernate.cache.provider_class", "org.hibernate.cache.NoCacheProvider");

        // Since we are not using the exact same version as provided
        // on JBoss we need to disable search and validator registration.
        //
        // If we don't do this Hibernate will use the provided version
        // of search and validator which do not match the ones in the war.

        config.setProperty("hibernate.validator.autoregister_listeners", "false");
        config.setProperty("hibernate.search.autoregister_listeners", "false");

        // Configure Hibernate JTA bindings


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

    @Provides
    protected Connection provideConnection(@Named(JNDI_DATASOURCE_NAME) String jndiDatasource,
                                           @Named(JDBC_URL_PROP) String jdbcUrl,
                                           @Named(DB_USERNAME_PROP) String jdbcUsername,
                                           @Named(DB_PASSWORD_PROP) String jdbcPassword)
    {
        try {
            // In tests jndiDatasource is empty and we use jdbc directly
            if (jndiDatasource == null || jndiDatasource.length() == 0) {
                Properties connectionProps = new Properties();
                connectionProps.put("user", jdbcUsername);
                connectionProps.put("password", jdbcPassword);
                return DriverManager.getConnection(jdbcUrl, connectionProps);
            } else {
                Context initialContext = new InitialContext();
                DataSource dataSource = (DataSource) initialContext.lookup(jndiDatasource);
                return dataSource.getConnection();
            }
        } catch (NamingException e) {
            throw  new RuntimeException("Cannot get connection", e);
        } catch (SQLException e) {
            throw  new RuntimeException("Cannot get connection", e);
        }
    }
}
