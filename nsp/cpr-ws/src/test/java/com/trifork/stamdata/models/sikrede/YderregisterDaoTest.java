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

import org.junit.Test;
import org.junit.runner.RunWith;

import com.trifork.stamdata.persistence.Transactional;

import dk.nsi.stamdata.cpr.Factories;
import dk.nsi.stamdata.cpr.models.Yderregister;
import dk.nsi.stamdata.guice.GuiceTestRunner;

@RunWith(GuiceTestRunner.class)
public class YderregisterDaoTest extends AbstractDaoTest
{
    @Test
    @Transactional
    public void verifyMapping() throws SQLException
    {
        Yderregister yderregister = Factories.createYderregister();
        insertInTable(yderregister);

        Yderregister record = fetcher.fetch(Yderregister.class, yderregister.getNummer());
        assertEquals(record.getNummer(), yderregister.getNummer());
    }
}
