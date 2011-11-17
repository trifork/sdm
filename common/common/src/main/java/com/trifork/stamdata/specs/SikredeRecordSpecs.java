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
import static com.trifork.stamdata.persistence.RecordSpecification.RecordFieldType.NUMERICAL;

public final class SikredeRecordSpecs
{
    protected SikredeRecordSpecs() {}

    public static final RecordSpecification START_RECORD_SPEC = RecordSpecification.createSpec("DummyTable", "DummyKey",
            "PostType", NUMERICAL, 2,
            "OpgDato", ALPHANUMERICAL, 8,
            "Timestamp", ALPHANUMERICAL, 20,
            "Modt", ALPHANUMERICAL, 6,
            "SnitfladeId", ALPHANUMERICAL, 8);

    public static final RecordSpecification END_RECORD_SPEC = RecordSpecification.createSpec("DummyTable", "DummyKey",
            "PostType", NUMERICAL, 2,
            "AntPost", NUMERICAL, 8);

    public static final RecordSpecification ENTRY_RECORD_SPEC = RecordSpecification.createSpec("SikredeGenerated", "CPRnr",
            // Page 1 (6 of 11)
            "PostType", NUMERICAL, 2,
            "CPRnr", ALPHANUMERICAL, 10,
            "SYdernr", ALPHANUMERICAL, 6,
            "SIkraftDatoYder", ALPHANUMERICAL, 8,
            "SRegDatoYder", ALPHANUMERICAL, 8,
            "SSikrGrpKode", ALPHANUMERICAL, 1,
            "SIkraftDatoGrp", ALPHANUMERICAL, 8,
            "SRegDatoGrp", ALPHANUMERICAL, 8,
            "SSikrKomKode", ALPHANUMERICAL, 3,
            "SIkraftDatoKomKode", ALPHANUMERICAL, 3,
            "SYdernrGI", ALPHANUMERICAL, 6,
            "SIkraftDatoYderGI", ALPHANUMERICAL, 8,
            "SRegDatoYderGI", ALPHANUMERICAL, 8,
            "SSikrGrpKodeGI", ALPHANUMERICAL, 1,
            "SIkraftDatoGrpGI", ALPHANUMERICAL, 8,
            "SRegDatoGrpGI", ALPHANUMERICAL, 8,
            "SYdernrFrem", ALPHANUMERICAL, 6,
            "SIkraftDatoYderFrem", ALPHANUMERICAL, 8,
            "SRegDatoYderFrem", ALPHANUMERICAL, 8,
            "SSikrGrpKodeFrem", ALPHANUMERICAL, 1,

            // Page 2 (7 of 11)
            "SIkraftDatoGrpFrem", ALPHANUMERICAL, 8,
            "SRegDatoGrpFrem", ALPHANUMERICAL, 8,
            "SKon", ALPHANUMERICAL, 1,
            "SAlder", ALPHANUMERICAL, 3,
            "SFolgerskabsPerson", ALPHANUMERICAL, 10,
            "SStatus", ALPHANUMERICAL, 2,
            "SBevisDato", ALPHANUMERICAL, 8,
            // ...
            "PNavn", ALPHANUMERICAL, 34,
            // ...
            "SBSStatsborgerskabKode", ALPHANUMERICAL, 2,
            "SBSStatsborgerskab", ALPHANUMERICAL, 47,
            "SSKAdrLinie1", ALPHANUMERICAL, 40,
            "SSKAdrLinie2", ALPHANUMERICAL, 40,

            // Page 3 (8 of 11)
            "SSKBopelsLand", ALPHANUMERICAL, 40,
            "SSKBopelsLAndKode", ALPHANUMERICAL, 2,
            "SSKEmailAdr", ALPHANUMERICAL, 50,
            "SSKFamilieRelation", ALPHANUMERICAL, 10,
            "SSKFodselsdato", ALPHANUMERICAL, 10,
            "SSKGyldigFra", ALPHANUMERICAL, 10,
            "SSKGyldigTil", ALPHANUMERICAL, 10,
            "SSKMobilNr", ALPHANUMERICAL, 20,
            "SSKPostNrBy", ALPHANUMERICAL, 40,
            "SSLForsikringsinstans", ALPHANUMERICAL, 21,
            "SSLForsikringsinstansKode", ALPHANUMERICAL, 10,
            "SSLForsikringsnr", ALPHANUMERICAL, 15,
            "SSLGyldigFra", ALPHANUMERICAL, 10,
            "SSLGyldigTil", ALPHANUMERICAL, 10,
            "SSLSocSikretLand", ALPHANUMERICAL, 47,
            "SSLSocSikretLandKode", ALPHANUMERICAL, 2);
}
