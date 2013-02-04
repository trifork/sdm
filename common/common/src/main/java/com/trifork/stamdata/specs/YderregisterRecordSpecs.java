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

import static com.trifork.stamdata.persistence.RecordSpecification.field;

import com.trifork.stamdata.persistence.RecordSpecification;

public class YderregisterRecordSpecs
{
    public static final RecordSpecification START_RECORD_TYPE = RecordSpecification.createSpecification("DummyTable", "DummyKey",
            field("OpgDato", 8),
            field("Timestamp", 20),
            field("Modt", 6),
            field("SnitfladeId", 8)
    );

    public static final RecordSpecification YDER_RECORD_TYPE = RecordSpecification.createSpecification("Yderregister", "Id",
            field("Id", 32),
            field("HistIdYder", 16),
            field("AmtKodeYder", 2),
            field("AmtTxtYder", 60),
            field("YdernrYder", 6),
            field("PrakBetegn", 50),
            // Att
            field("AdrYder", 50),
            field("PostnrYder", 4),
            field("PostdistYder", 20),
            field("TilgDatoYder", 8),
            field("AfgDatoYder", 8),
            // OverensKode
            // OverenskomstTxt
            // LandsYdertypeKode
            // LandsYdertypeTxt
            field("HvdSpecKode", 2),
            field("HvdSpecTxt", 60),
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
            field("HvdTlf", 8),
            // Fax
            field("EmailYder", 50),
            field("WWW", 78)
            // ...
    );

    public static final RecordSpecification PERSON_RECORD_TYPE = RecordSpecification.createSpecification("YderregisterPerson", "Id",
            field("Id", 32),
            field("HistIdPerson", 16),
            field("YdernrPerson", 6),
            field("TilgDatoPerson", 8),
            field("AfgDatoPerson", 8),
            field("CprNr", 10),
            // Navn
            field("PersonrolleKode", 2),
            field("PersonrolleTxt", 60)
            // ...
    );
}
