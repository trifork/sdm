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
package com.trifork.stamdata.specs;

import com.trifork.stamdata.persistence.RecordSpecification;

import static com.trifork.stamdata.persistence.RecordSpecification.RecordFieldType.ALPHANUMERICAL;

public class YderregisterRecordSpecs
{
    public static final RecordSpecification START_RECORD_TYPE = RecordSpecification.createSpec("DummyTable", "DummyKey",
            "OpgDato", ALPHANUMERICAL, 8,
            "Timestamp", ALPHANUMERICAL, 20,
            "Modt", ALPHANUMERICAL, 6,
            "SnitfladeId", ALPHANUMERICAL, 8
    );

    public static final RecordSpecification YDER_RECORD_TYPE = RecordSpecification.createSpec("Yderregister", "HistIdYder",
            "HistIdYder", ALPHANUMERICAL, 16,
            "AmtKodeYder", ALPHANUMERICAL, 2,
            "AmtTxtYder", ALPHANUMERICAL, 60,
            "YdernrYder", ALPHANUMERICAL, 6,
            "PrakBetegn", ALPHANUMERICAL, 50,
            // Att
            "AdrYder", ALPHANUMERICAL, 50,
            "PostnrYder", ALPHANUMERICAL, 4,
            "PostdistYder", ALPHANUMERICAL, 20,
            "TilgDatoYder", ALPHANUMERICAL, 8,
            "AfgDatoYder", ALPHANUMERICAL, 8,
            // OverensKode
            // OverenskomstTxt
            // LandsYdertypeKode
            // LandsYdertypeTxt
            "HvdSpecKode", ALPHANUMERICAL, 2,
            "HvdSpecTxt", ALPHANUMERICAL, 60,
            // IndberetFormKode
            // IndberetFormTxt
            // SelskFormKode
            // SelskFormTxt
            // SkatOpl
            // PrakFormKode
            // PrakFormTxt
            // PrakTypeKode
            // PrakTypeTxt
            // SamarbFormKode
            // SamarbFormTxt
            // PrakKomKode
            // PrakKomTxt
            "HvdTlf", ALPHANUMERICAL, 8,
            // Fax
            "EmailYder", ALPHANUMERICAL, 50,
            "WWW", ALPHANUMERICAL, 78
            // ...
    );

    public static final RecordSpecification PERSON_RECORD_TYPE = RecordSpecification.createSpec("YderregisterPerson", "HistIdPerson",
            "HistIdPerson", ALPHANUMERICAL, 16,
            "YdernrPerson", ALPHANUMERICAL, 6,
            "TilgDatoPerson", ALPHANUMERICAL, 8,
            "AfgDatoPerson", ALPHANUMERICAL, 8,
            "CprNr", ALPHANUMERICAL, 10,
            // Navn
            "PersonrolleKode", ALPHANUMERICAL, 2,
            "PersonrolleTxt", ALPHANUMERICAL, 60
            // ...
    );
}
