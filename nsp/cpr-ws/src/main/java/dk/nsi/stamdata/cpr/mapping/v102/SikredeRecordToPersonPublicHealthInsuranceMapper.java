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
package dk.nsi.stamdata.cpr.mapping.v102;

import com.trifork.stamdata.persistence.Record;
import dk.nsi.stamdata.cpr.util.XMLCalendar;
import oio.medcom.cprservice._1_0.ObjectFactory;
import oio.medcom.cprservice._1_0.PersonPublicHealthInsuranceType;
import oio.medcom.cprservice._1_0.PublicHealthInsuranceGroupIdentifierType;

public class SikredeRecordToPersonPublicHealthInsuranceMapper {

    public PersonPublicHealthInsuranceType map(Record record) {
        PersonPublicHealthInsuranceType healthInsuranceType = new ObjectFactory().createPersonPublicHealthInsuranceType();
        // The key SSikrGrpKode is taken from "NSI - NOTUS Sikrede - def"
        String sSikrGrpKodeFieldValue = (String) record.get("SSikrGrpKode");
        if ("1".equals(sSikrGrpKodeFieldValue)) {
            healthInsuranceType.setPublicHealthInsuranceGroupIdentifier(PublicHealthInsuranceGroupIdentifierType.SYGESIKRINGSGRUPPE_1);
        } else if ("2".equals(sSikrGrpKodeFieldValue)) {
            healthInsuranceType.setPublicHealthInsuranceGroupIdentifier(PublicHealthInsuranceGroupIdentifierType.SYGESIKRINGSGRUPPE_2);
        } else if ("4".equals(sSikrGrpKodeFieldValue)) {
            healthInsuranceType.setPublicHealthInsuranceGroupIdentifier(PublicHealthInsuranceGroupIdentifierType.SYGESIKRINGSGRUPPE_4);
        } else if ("5".equals(sSikrGrpKodeFieldValue)) {
            healthInsuranceType.setPublicHealthInsuranceGroupIdentifier(PublicHealthInsuranceGroupIdentifierType.SYGESIKRINGSGRUPPE_5);
        } else if ("6".equals(sSikrGrpKodeFieldValue)) {
            healthInsuranceType.setPublicHealthInsuranceGroupIdentifier(PublicHealthInsuranceGroupIdentifierType.SYGESIKRINGSGRUPPE_6);
        } else if ("7".equals(sSikrGrpKodeFieldValue)) {
            healthInsuranceType.setPublicHealthInsuranceGroupIdentifier(PublicHealthInsuranceGroupIdentifierType.SYGESIKRINGSGRUPPE_7);
        } else if ("8".equals(sSikrGrpKodeFieldValue)) {
            healthInsuranceType.setPublicHealthInsuranceGroupIdentifier(PublicHealthInsuranceGroupIdentifierType.SYGESIKRINGSGRUPPE_8);
        } else if ("9".equals(sSikrGrpKodeFieldValue)) {
            healthInsuranceType.setPublicHealthInsuranceGroupIdentifier(PublicHealthInsuranceGroupIdentifierType.SYGESIKRINGSGRUPPE_9);
        } else {
            throw new IllegalStateException("Unknown value for SSikrGrpKode: " + sSikrGrpKodeFieldValue);
        }
        // The key SIkraftDatoGrp is taken from "NSI - NOTUS Sikrede - def"
        String sIkraftDatoGrp = (String) record.get("SIkraftDatoGrp");
        int year = Integer.parseInt(sIkraftDatoGrp.substring(0, 4));
        int month = Integer.parseInt(sIkraftDatoGrp.substring(4, 6));
        int day = Integer.parseInt(sIkraftDatoGrp.substring(6, 8));
        healthInsuranceType.setPublicHealthInsuranceGroupStartDate(XMLCalendar.newXMLGregorianCalendar(year, month, day));
        return healthInsuranceType;
    }

}
