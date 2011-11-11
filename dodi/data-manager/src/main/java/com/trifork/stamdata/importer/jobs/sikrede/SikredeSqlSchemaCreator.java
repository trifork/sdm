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

public class SikredeSqlSchemaCreator {
    
    public static String createSqlSchema(SikredeFields sikredeFields)
    {
        SikredeSqlSchemaCreator creator = new SikredeSqlSchemaCreator(sikredeFields);
        return creator.buildSqlSchema();
    }
    
    private SikredeFields sikredeFields;
    
    private SikredeSqlSchemaCreator(SikredeFields sikredeFields)
    {
        this.sikredeFields = sikredeFields;
    }
    
    private String buildSqlSchema()
    {
        StringBuilder builder = new StringBuilder();
        
        builder.append("CREATE TABLE SikredeGenerated (\n");
        
        builder.append("\tSikredePID BIGINT(15) AUTO_INCREMENT NOT NULL PRIMARY KEY");
        
        for (SikredeFieldSpecification fieldSpecification: sikredeFields.getFieldSpecificationsInCorrectOrder())
        {
            if(fieldSpecification.type == SikredeType.NUMERICAL)
            {
                builder.append(String.format(",\n\t%s BIGINT NOT NULL", fieldSpecification.name));
            }
            else if(fieldSpecification.type == SikredeType.ALFANUMERICAL)
            {
                builder.append(String.format(",\n\t%s VARCHAR(%d) NOT NULL", fieldSpecification.name, fieldSpecification.length));
            }
            else
            {
                throw new AssertionError("Field specification must have a type.");
            }
        }
        
        builder.append(",\n\tValidFrom DateTime NOT NULL");
        builder.append(",\n\tValidTo DateTime");
        
        builder.append("\n) ENGINE=InnoDB COLLATE=utf8_bin;\n");
        
        return builder.toString();
    }
}