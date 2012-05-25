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

public final class BemyndigelseRecordSpecs
{
    protected BemyndigelseRecordSpecs() {}
    
    public static final RecordSpecification ENTRY_RECORD_SPEC = RecordSpecification.createSpecification("Bemyndigelse", "kode", 
            field("kode", 100),
            field("bemyndigende_cpr", 10),
            field("bemyndigede_cpr", 10),
            field("bemyndigede_cvr", 8),
            field("system", 100),
            field("arbejdsfunktion", 100),
            field("rettighed", 100),
            field("status", 100),
            field("godkendelses_dato", 25),
            field("oprettelses_dato", 25),
            field("modificeret_dato", 25),
            field("gyldig_fra_dato", 25),
            field("gyldig_til_dato", 25));
}
