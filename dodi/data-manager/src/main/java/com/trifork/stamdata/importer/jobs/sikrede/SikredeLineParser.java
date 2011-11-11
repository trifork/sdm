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
package com.trifork.stamdata.importer.jobs.sikrede;

import com.trifork.stamdata.persistence.SikredeFields;
import com.trifork.stamdata.persistence.SikredeFields.SikredeFieldSpecification;
import com.trifork.stamdata.persistence.SikredeFields.SikredeType;
import com.trifork.stamdata.persistence.SikredeRecord;
import com.trifork.stamdata.persistence.SikredeRecordBuilder;

public class SikredeLineParser {

    private SikredeFields sikredeFields;
    
    public SikredeLineParser(SikredeFields sikredeFields)
    {
        this.sikredeFields = sikredeFields;
    }
    
    public SikredeRecord parseLine(String line)
    {
        if(line.length() != sikredeFields.acceptedTotalLineLength())
        {
            throw new IllegalArgumentException("Supplied line had length " + line.length() + " but only lines of length " + sikredeFields.acceptedTotalLineLength() + " are accepted");
        }
        
        SikredeRecordBuilder builder = new SikredeRecordBuilder(sikredeFields);
        int offset = 0;
        for(SikredeFieldSpecification fieldSpecification: sikredeFields.getFieldSpecificationsInCorrectOrder())
        {
            String subString = line.substring(offset, offset + fieldSpecification.length);
            
            if(fieldSpecification.type == SikredeType.ALFANUMERICAL)
            {
                builder.field(fieldSpecification.name, subString.trim());
            }
            else if(fieldSpecification.type == SikredeType.NUMERICAL)
            {
                // This will potentially throw a runtime exception on bad input
                builder.field(fieldSpecification.name, Integer.parseInt(subString.trim()));
            }
            else
            {
                throw new AssertionError("Should match exactly one of the types alfanumerical or numerical.");
            }
            
            offset += fieldSpecification.length;
        }
        
        return builder.build();
    }
}
