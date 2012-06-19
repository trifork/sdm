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

public final class SorRelationsRecordSpecs
{
    protected SorRelationsRecordSpecs() {}
    
    public static final RecordSpecification RELATIONS_RECORD_SPEC = RecordSpecification.createSpecification("SORRelationer", "SORRelationerPID", 
            field("sor_parent", 100),
            field("sor_child", 100)
    );

    public static final RecordSpecification SHAK_YDER_RECORD_SPEC = RecordSpecification.createSpecification("SORYderSHAKRelationer", "SORYderSHAKRelationerPID", 
            field("shak_yder", 100),
            field("sor", 100)
    );

}
