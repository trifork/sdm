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
package dk.nsi.stamdata.replication.webservice;

import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.trifork.stamdata.Preconditions;
import com.trifork.stamdata.persistence.Record;
import com.trifork.stamdata.persistence.RecordSpecification;
import com.trifork.stamdata.persistence.RecordSpecification.FieldSpecification;
import com.trifork.stamdata.persistence.RecordSpecification.RecordFieldType;


public class RecordXmlGenerator
{
    private RecordSpecification recordSpecification;

    public RecordXmlGenerator(RecordSpecification recordSpecification)
    {
        this.recordSpecification = recordSpecification;
    }

    public Document generateXml(Record record)
    {
        return generateXml(Arrays.asList(record));
    }

    public Document generateXml(List<Record> records) 
    {
        Document document = createEmptyDocument();
        
        Element root = document.createElementNS("http://www.w3.org/2005/Atom", "feed");
        document.appendChild(root);

        for(Record record : records)
        {
            Preconditions.checkArgument(recordSpecification.conformsToSpecifications(record), "The supplied record does not conform to the specification");
            addRecordToDocument(record, document, root);
        }
        return document;
    }

    private Document createEmptyDocument() 
    {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(false);
        DocumentBuilder documentBuilder = null;

        try
        {
            documentBuilder = builderFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e)
        {
            throw new RuntimeException("Unable to configure document builder", e);
        }

        return documentBuilder.newDocument();
    }

    private void addRecordToDocument(Record record, Document document, Element root) 
    {
        Element sikredeRecordElement = document.createElement("Record");
        root.appendChild(sikredeRecordElement);
        
        for (FieldSpecification fieldSpecification: recordSpecification.getFieldSpecs())
        {
            Element fieldElement = document.createElement(fieldSpecification.name);
            fieldElement.setTextContent(valueAsString(record, fieldSpecification));
            sikredeRecordElement.appendChild(fieldElement);
        }
    }
    
    private String valueAsString(Record record, FieldSpecification fieldSpecification)
    {
        if (fieldSpecification.type == RecordSpecification.RecordFieldType.ALPHANUMERICAL)
        {
            return String.valueOf(record.get(fieldSpecification.name));
        }
        else if (fieldSpecification.type == RecordFieldType.NUMERICAL)
        {
            return Integer.toString((Integer) record.get(fieldSpecification.name));
        }
        else
        {
            throw new AssertionError("Unknown type: " + fieldSpecification.type);
        }
    }
}
