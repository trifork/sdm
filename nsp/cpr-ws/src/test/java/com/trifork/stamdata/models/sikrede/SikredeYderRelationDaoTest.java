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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;

import org.junit.runner.RunWith;

import com.trifork.stamdata.persistence.Transactional;

import dk.nsi.stamdata.cpr.Factories;
import dk.nsi.stamdata.cpr.models.SikredeYderRelation;
import dk.nsi.stamdata.guice.GuiceTestRunner;


@RunWith(GuiceTestRunner.class)
public class SikredeYderRelationDaoTest extends AbstractDaoTest
{
    @Override
    @Transactional
    public void verifyMapping() throws SQLException
    {
        SikredeYderRelation relation = Factories.createSikredeYderRelation();
        insertInTable(relation);

        SikredeYderRelation record = fetcher.fetch(SikredeYderRelation.class, relation.getId());
        assertThat(record.getId(), is(relation.getId()));
    }
}
