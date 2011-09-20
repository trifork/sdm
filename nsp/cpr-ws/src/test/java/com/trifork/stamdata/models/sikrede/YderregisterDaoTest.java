package com.trifork.stamdata.models.sikrede;


import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import com.trifork.stamdata.ConfigurationLoader;
import com.trifork.stamdata.Fetcher;
import dk.nsi.stamdata.cpr.ApplicationController;
import dk.nsi.stamdata.cpr.SessionProvider;
import org.hibernate.Session;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: frj
 * Date: 9/20/11
 * Time: 1:17 PM
 *
 * @Author frj
 */

public class YderregisterDaoTest extends AbstractDaoTest {
    @Before
    public void init() {
        purgeTable("Yderregister");
    }

    private void insertInYderregistertable() {
        Yderregister yderregister = new Yderregister();
        yderregister.setNummer(1234);
        yderregister.setBynavn("test");
        yderregister.setModifiedDate(new Date());
        yderregister.setCreatedDate(new Date());
        yderregister.setValidFrom(DateTime.now().minusDays(1).toDate());
        yderregister.setValidTo(DateTime.now().plusDays(1).toDate());

        insertInTable(yderregister);
    }

    @Test
    public void verifyMapping() throws SQLException {
        assertTrue(true);
        insertInYderregistertable();

        Yderregister yderregister1 = fetcher.fetch(Yderregister.class, 1234);
        assertEquals("test", yderregister1.getBynavn());
    }
}
