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

public final class SikredeRecordSpecs
{
    protected SikredeRecordSpecs() {}
    
    public static final RecordSpecification START_RECORD_SPEC = RecordSpecification.createSpecification("DummyTable", "DummyKey", 
            field("PostType", 2).numerical().doNotPersist(),
            field("OpgDato", 8),
            field("Timestamp", 20),
            field("Modt", 6),
            field("SnitfladeId", 8));
    
    public static final RecordSpecification END_RECORD_SPEC = RecordSpecification.createSpecification("DummyTable", "DummyKey", 
            field("PostType", 2).numerical().doNotPersist(),
            field("AntPost", 8).numerical());
    
    public static final RecordSpecification ENTRY_RECORD_SPEC = RecordSpecification.createSpecification("SikredeGenerated", "CPRnr", 
            // Page 1 (6 of 11)
            field("PostType", 2).numerical().doNotPersist(),
            field("CPRnr", 10),
            field("SYdernr", 6),
            field("SIkraftDatoYder", 8),
            field("SRegDatoYder", 8),
            field("SSikrGrpKode", 1),
            field("SIkraftDatoGrp", 8),
            field("SRegDatoGrp", 8),
            field("SSikrKomKode", 3),
            field("SIkraftDatoKomKode", 3),
            field("SYdernrGI", 6),
            field("SIkraftDatoYderGI", 8),
            field("SRegDatoYderGI", 8),
            field("SSikrGrpKodeGI", 1),
            field("SIkraftDatoGrpGI", 8),
            field("SRegDatoGrpGI", 8),
            field("SYdernrFrem", 6),
            field("SIkraftDatoYderFrem", 8),
            field("SRegDatoYderFrem", 8),
            field("SSikrGrpKodeFrem", 1),

            // Page 2 (7 of 11)
            field("SIkraftDatoGrpFrem", 8),
            field("SRegDatoGrpFrem", 8),
            field("SKon", 1),
            field("SAlder", 3),
            field("SFolgerskabsPerson", 10),
            field("SStatus", 2),
            field("SBevisDato", 8),
            // ...
            field("PNavn", 34),
            // ...
            field("SBSStatsborgerskabKode", 2),
            field("SBSStatsborgerskab", 47),
            field("SSKAdrLinie1", 40),
            field("SSKAdrLinie2", 40),

            // Page 3 (8 of 11)
            field("SSKBopelsLand", 40),
            field("SSKBopelsLAndKode", 2),
            field("SSKEmailAdr", 50),
            field("SSKFamilieRelation", 10),
            field("SSKFodselsdato", 10),
            field("SSKGyldigFra", 10),
            field("SSKGyldigTil", 10),
            field("SSKMobilNr", 20),
            field("SSKPostNrBy", 40),
            field("SSLForsikringsinstans", 21),
            field("SSLForsikringsinstansKode", 10),
            field("SSLForsikringsnr", 15),
            field("SSLGyldigFra", 10),
            field("SSLGyldigTil", 10),
            field("SSLSocSikretLand", 47),
            field("SSLSocSikretLandKode", 2));
}
