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

import java.math.BigInteger;

import org.junit.Test;

import com.trifork.stamdata.persistence.Record;
import com.trifork.stamdata.persistence.RecordBuilder;
import com.trifork.stamdata.specs.YderregisterRecordSpecs;

import dk.nsi.stamdata.jaxws.generated.AssociatedGeneralPractitionerStructureType;

public class YderregisterRecordToAssociatedGeneralPractitionerMapperTest {

    @Test
    public void testCompleteMapping() 
    {
        testSimpleMappingContainingAllFieldsHelper("123456", "Kvaksalverne A/S", "Galten", "kvak@salver.dk", "8464", "Hovedgaden 10", "86941234");
    }

    private void testSimpleMappingContainingAllFieldsHelper(
            String ydernrYder, String prakBetegn, String postdistYder, String emailYder, String postnrYder, String adrYder, String hvdTlf)
    {
        RecordBuilder builder = new RecordBuilder(YderregisterRecordSpecs.YDER_RECORD_TYPE)
                                        .field("YdernrYder", ydernrYder)
                                        .field("PrakBetegn", prakBetegn)
                                        .field("PostdistYder", postdistYder)
                                        .field("EmailYder", emailYder)
                                        .field("PostnrYder", postnrYder)
                                        .field("AdrYder", adrYder)
                                        .field("HvdTlf", hvdTlf);
        
        Record yderRecord = builder.addDummyFieldsAndBuild();
        
        YderregisterRecordToAssociatedGeneralPractitionerMapper mapper = new YderregisterRecordToAssociatedGeneralPractitionerMapper();
        AssociatedGeneralPractitionerStructureType xmlStructurer = mapper.map(yderRecord);
        
        assertEquals(new BigInteger(ydernrYder), xmlStructurer.getAssociatedGeneralPractitionerIdentifier());
        assertEquals(prakBetegn, xmlStructurer.getAssociatedGeneralPractitionerOrganisationName());
        assertEquals(postdistYder, xmlStructurer.getDistrictName());
        assertEquals(emailYder, xmlStructurer.getEmailAddressIdentifier());
        assertEquals(postnrYder, xmlStructurer.getPostCodeIdentifier());
        assertEquals(adrYder, xmlStructurer.getStandardAddressIdentifier());
        assertEquals(hvdTlf, xmlStructurer.getTelephoneSubscriberIdentifier());
    }    
}
