package com.trifork.stamdata.models.sikrede;

import org.junit.Before;

import java.sql.SQLException;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * User: frj
 * Date: 9/20/11
 * Time: 2:12 PM
 *
 * @Author frj
 */
public class SikredeYderRelationDaoTest extends AbstractDaoTest {

    @Before
    private void init() {
        purgeTable("SikredeYderRelation");
    }

    @Override
    public void verifyMapping() throws SQLException {
        SikredeYderRelation syr = new SikredeYderRelation();
        syr.setGruppeKodeIkraftDato(new Date());
        syr.setSikringsgruppeKode('1');
        syr.setYdernummer(1234);
        syr.setCpr("0101010101");

        insertInTable(syr);

        SikredeYderRelation sikredeYderRelation = fetcher.fetch(SikredeYderRelation.class, "0101010101");
        assertEquals("0101010101", sikredeYderRelation.getCpr());
        assertEquals(1234, sikredeYderRelation.getYdernummer());

    }
}
