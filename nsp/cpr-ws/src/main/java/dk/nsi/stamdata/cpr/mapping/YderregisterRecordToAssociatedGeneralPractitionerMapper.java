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

import java.math.BigInteger;

import com.trifork.stamdata.persistence.Record;

import dk.oio.rep.medcom_sundcom_dk.xml.schemas._2007._02._01.AssociatedGeneralPractitionerStructureType;

public class YderregisterRecordToAssociatedGeneralPractitionerMapper {

    public AssociatedGeneralPractitionerStructureType map(Record record)
    {
        AssociatedGeneralPractitionerStructureType associatedGeneralPractitioner = new AssociatedGeneralPractitionerStructureType();
        
        associatedGeneralPractitioner.setAssociatedGeneralPractitionerIdentifier(new BigInteger((String) record.get("YdernrYder")));
        associatedGeneralPractitioner.setAssociatedGeneralPractitionerOrganisationName((String) record.get("PrakBetegn"));
        associatedGeneralPractitioner.setDistrictName((String) record.get("PostdistYder"));
        associatedGeneralPractitioner.setEmailAddressIdentifier((String) record.get("EmailYder"));
        associatedGeneralPractitioner.setPostCodeIdentifier((String) record.get("PostnrYder"));
        associatedGeneralPractitioner.setStandardAddressIdentifier((String) record.get("AdrYder"));
        associatedGeneralPractitioner.setTelephoneSubscriberIdentifier((String) record.get("HvdTlf"));
        
        return associatedGeneralPractitioner;
    }
    
}
