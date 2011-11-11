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

import org.junit.Test;

import com.trifork.stamdata.persistence.SikredeRecord;

import dk.nsi.stamdata.jaxws.generated.PersonPublicHealthInsuranceType;
import dk.nsi.stamdata.jaxws.generated.PublicHealthInsuranceGroupIdentifierType;

public class SikredeRecordToPersonPublicHealthInsuranceTest {

    @Test
    public void testSimpleMappingContainingAllFields() 
    {
        String sygesikringsGruppeKategori = "1";
        String sygesikringsGruppeStartDato = "20110628";
        
        SikredeRecord record = new SikredeRecord();
        // TODO: rename setField to withField
        // These record names are taken from the document "NSI - NOTUS Sikrede -def.pdf"
        record = record
                .setField("SSikrGrpKode", sygesikringsGruppeKategori)
                .setField("SIkraftDatoGrp", sygesikringsGruppeStartDato);
        
        SikredeRecordToPersonPublicHealhInsuranceMapper mapper = new SikredeRecordToPersonPublicHealhInsuranceMapper();
        
        PersonPublicHealthInsuranceType xmlStructurer = mapper.map(record);
        
        assertEquals(PublicHealthInsuranceGroupIdentifierType.SYGESIKRINGSGRUPPE_1, xmlStructurer.getPublicHealthInsuranceGroupIdentifier());
        assertEquals(2011, xmlStructurer.getPublicHealthInsuranceGroupStartDate().getYear());
        assertEquals(6, xmlStructurer.getPublicHealthInsuranceGroupStartDate().getMonth());
        assertEquals(28, xmlStructurer.getPublicHealthInsuranceGroupStartDate().getDay());
    }

}
