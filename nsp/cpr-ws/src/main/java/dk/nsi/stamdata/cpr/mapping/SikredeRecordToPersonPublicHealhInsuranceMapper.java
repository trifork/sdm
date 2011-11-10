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

import java.util.Date;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;

import com.trifork.stamdata.persistence.SikredeRecord;

import dk.nsi.stamdata.jaxws.generated.ObjectFactory;
import dk.nsi.stamdata.jaxws.generated.PersonPublicHealthInsuranceType;
import dk.nsi.stamdata.jaxws.generated.PublicHealthInsuranceGroupIdentifierType;

public class SikredeRecordToPersonPublicHealhInsuranceMapper 
{
    private static final String SYGESIKRINGSGRUPPE_1_FIELD_VALUE = "1";
    private static final String SYGESIKRINGSGRUPPE_2_FIELD_VALUE = "2";

    public PersonPublicHealthInsuranceType map(SikredeRecord record)
    {
        // TODO: If SikredeFields was also available, it would be possible to verify content before mapping
        PersonPublicHealthInsuranceType healthInsuranceType = new ObjectFactory().createPersonPublicHealthInsuranceType();

        // The key SSikrGrpKode is taken from "NSI - NOTUS Sikrede - def"
        Object sSikrGrpKodeFieldValue = record.get("SSikrGrpKode");
        if(SYGESIKRINGSGRUPPE_1_FIELD_VALUE.equals(sSikrGrpKodeFieldValue))
        {
            healthInsuranceType.setPublicHealthInsuranceGroupIdentifier(PublicHealthInsuranceGroupIdentifierType.SYGESIKRINGSGRUPPE_1);
        }
        else if(SYGESIKRINGSGRUPPE_2_FIELD_VALUE.equals(sSikrGrpKodeFieldValue))
        {
            healthInsuranceType.setPublicHealthInsuranceGroupIdentifier(PublicHealthInsuranceGroupIdentifierType.SYGESIKRINGSGRUPPE_2);
        }
        else
        {
            throw new IllegalStateException("Unknown value for SSikrGrpKode: " + sSikrGrpKodeFieldValue);
        }
        
        // The key SIkraftDatoGrp is taken from "NSI - NOTUS Sikrede - def"
        String sIkraftDatoGrp = (String) record.get("SIkraftDatoGrp");
        int year = Integer.parseInt(sIkraftDatoGrp.substring(0, 4));
        int month = Integer.parseInt(sIkraftDatoGrp.substring(4, 6));
        int day = Integer.parseInt(sIkraftDatoGrp.substring(6, 8));
        healthInsuranceType.setPublicHealthInsuranceGroupStartDate(newXMLGregorianCalendar(year, month, day));
        
        return healthInsuranceType;
    }
    
    static XMLGregorianCalendar newXMLGregorianCalendar(int year, int month, int day)
    {
        DatatypeFactory factory = null;
        try {
            factory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
        return factory.newXMLGregorianCalendar(new DateTime(year, month, day, 0, 0).toGregorianCalendar());
    }

}
