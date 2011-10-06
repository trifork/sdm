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
package com.trifork.stamdata.models.sikrede;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.Date;

import org.joda.time.DateTime;
import org.junit.Before;

import dk.nsi.stamdata.cpr.models.SikredeYderRelation;

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
