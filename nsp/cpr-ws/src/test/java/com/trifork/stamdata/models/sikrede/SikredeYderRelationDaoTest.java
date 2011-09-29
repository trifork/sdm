package com.trifork.stamdata.models.sikrede;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.Date;

import org.joda.time.DateTime;
import org.junit.Before;

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
        syr.setGruppekodeRegistreringDato(new Date());
        syr.setYdernummerIkraftDato(new Date());
        syr.setYdernummerRegistreringDato(new Date());
        syr.setCreatedDate(new Date());
        syr.setModifiedDate(new Date());
        syr.setSikringsgruppeKode('1');
        syr.setYdernummer(1234);
        syr.setCpr("0101010101");
        syr.setType("C");
        syr.setId(syr.getCpr() + "-" + syr.getType());
        syr.setValidFrom(DateTime.now().minusDays(1).toDate());
        syr.setValidTo(DateTime.now().plusDays(1).toDate());

        insertInTable(syr);

        SikredeYderRelation sikredeYderRelation = fetcher.fetch(SikredeYderRelation.class, "0101010101-C");
        assertEquals("0101010101", sikredeYderRelation.getCpr());
        assertEquals(1234, sikredeYderRelation.getYdernummer());

    }
}
