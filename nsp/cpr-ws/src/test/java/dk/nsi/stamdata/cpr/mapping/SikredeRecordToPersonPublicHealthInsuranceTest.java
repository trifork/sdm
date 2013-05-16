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
package dk.nsi.stamdata.cpr.mapping;

import static org.junit.Assert.assertEquals;

import dk.oio.rep.medcom_sundcom_dk.xml.schemas._2007._02._01.PersonPublicHealthInsuranceType;
import dk.oio.rep.medcom_sundcom_dk.xml.schemas._2007._02._01.PublicHealthInsuranceGroupIdentifierType;
import org.junit.Test;

import com.trifork.stamdata.persistence.Record;

public class SikredeRecordToPersonPublicHealthInsuranceTest {

    @Test
    public void testSimpleMappingContainingAllFieldsInCategory1() {
        testSimpleMappingContainingAllFieldsHelper("1", PublicHealthInsuranceGroupIdentifierType.SYGESIKRINGSGRUPPE_1, "20110628", 2011, 6, 28);
    }

    @Test
    public void testSimpleMappingContainingAllFieldsInCategory2() {
        testSimpleMappingContainingAllFieldsHelper("2", PublicHealthInsuranceGroupIdentifierType.SYGESIKRINGSGRUPPE_2, "20110628", 2011, 6, 28);
    }

    @Test(expected = IllegalStateException.class)
    public void testSimpleMappingWithIlleagalSygesikringsgruppe() {
        testSimpleMappingContainingAllFieldsHelper("3", null, "20110628", 0, 0, 0);
    }

    @Test(expected = NumberFormatException.class)
    public void testSimpleMappingWithIlleagalSygesikringsGruppeStartDato() {
        testSimpleMappingContainingAllFieldsHelper("1", null, "A0110628", 0, 0, 0);
    }

    private void testSimpleMappingContainingAllFieldsHelper(
            String sygesikringsGruppeKategori, PublicHealthInsuranceGroupIdentifierType expectedCategory,
            String sygesikringsGruppeStartDato, int expectedYear, int expectedMonth, int expectedDay) {
        // These record names are taken from the document "NSI - NOTUS Sikrede -def.pdf"
        Record record = new Record()
                .put("SSikrGrpKode", sygesikringsGruppeKategori)
                .put("SIkraftDatoGrp", sygesikringsGruppeStartDato);

        SikredeRecordToPersonPublicHealhInsuranceMapper mapper = new SikredeRecordToPersonPublicHealhInsuranceMapper();
        PersonPublicHealthInsuranceType xmlStructurer = mapper.map(record);

        assertEquals(expectedCategory, xmlStructurer.getPublicHealthInsuranceGroupIdentifier());
        assertEquals(expectedYear, xmlStructurer.getPublicHealthInsuranceGroupStartDate().getYear());
        assertEquals(expectedMonth, xmlStructurer.getPublicHealthInsuranceGroupStartDate().getMonth());
        assertEquals(expectedDay, xmlStructurer.getPublicHealthInsuranceGroupStartDate().getDay());
    }
}
