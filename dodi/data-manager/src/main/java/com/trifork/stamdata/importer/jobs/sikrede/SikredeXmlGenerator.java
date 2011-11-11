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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.trifork.stamdata.persistence.SikredeFields;
import com.trifork.stamdata.persistence.SikredeRecord;
import com.trifork.stamdata.persistence.SikredeFields.SikredeFieldSpecification;
import com.trifork.stamdata.persistence.SikredeFields.SikredeType;

public class SikredeXmlGenerator {

    private SikredeFields sikredeFields;

    public SikredeXmlGenerator(SikredeFields sikredeFields)
    {
        this.sikredeFields = sikredeFields;
    }
    
    public Document generateXml(SikredeRecord record)
    {
        if(!sikredeFields.conformsToSpecifications(record))
        {
            // TODO: This message does not inform the user of what went wrong. conformsToSpecification should be supplemented with a method which throws an exception with more information
            throw new IllegalArgumentException("The supplied record does not conform to the specification");
        }
        
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = builderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Unable to configure document builder", e);
        }
        
        Document document = documentBuilder.newDocument();
        
        Element sikredeRecordElement = document.createElement("SikredeRecord");
        document.appendChild(sikredeRecordElement);
        
        for(SikredeFieldSpecification fieldSpecification: sikredeFields.getFieldSpecificationsInCorrectOrder())
        {
            Element fieldElement = document.createElement(fieldSpecification.name);
            fieldElement.setTextContent(valueAsString(record, fieldSpecification));
            sikredeRecordElement.appendChild(fieldElement);
        }
                
        return document;
    }
    
    private String valueAsString(SikredeRecord record, SikredeFieldSpecification fieldSpecification)
    {
        if(fieldSpecification.type == SikredeType.ALFANUMERICAL)
        {
            return (String) record.get(fieldSpecification.name);
        }
        else if(fieldSpecification.type == SikredeType.NUMERICAL)
        {
            return Integer.toString((Integer) record.get(fieldSpecification.name));
        } 
        else
        {
            throw new AssertionError("Unknown type: " + fieldSpecification.type);
        }
    }
}
