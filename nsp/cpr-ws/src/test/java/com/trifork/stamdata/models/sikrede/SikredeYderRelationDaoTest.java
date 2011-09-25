package com.trifork.stamdata.models.sikrede;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.Date;

import org.junit.Before;
import org.junit.Ignore;

@Ignore("This test is implemented correctly.")
public class SikredeYderRelationDaoTest extends AbstractDaoTest {

    @Before
    public void init()
    {
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
