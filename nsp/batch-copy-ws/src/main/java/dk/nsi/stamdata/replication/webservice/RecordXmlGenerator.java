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

import com.trifork.stamdata.Nullable;
import com.trifork.stamdata.persistence.RecordMetadata;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.trifork.stamdata.Preconditions;
import com.trifork.stamdata.persistence.Record;
import com.trifork.stamdata.persistence.RecordSpecification;
import com.trifork.stamdata.persistence.RecordSpecification.FieldSpecification;
import com.trifork.stamdata.persistence.RecordSpecification.RecordFieldType;
import org.w3c.dom.Text;


public class RecordXmlGenerator
{
    public static final String NAMESPACE_URI = "http://www.w3.org/2005/Atom";
    private RecordSpecification recordSpecification;

    public RecordXmlGenerator(RecordSpecification recordSpecification)
    {
        this.recordSpecification = recordSpecification;
    }

    public Document generateXml(Record record)
    {
        return generateXml(Arrays.asList(record));
    }

    public Document generateXml(List<RecordMetadata> records, String register, String datatype)
    {
        Document document = createEmptyDocument();
        
        Element root = document.createElementNS(NAMESPACE_URI, "feed");
        document.appendChild(root);

        String expected =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                        "<atom:feed xmlns:atom=\"http://www.w3.org/2005/Atom\" xmlns=\"http://trifork.com/-/stamdata/3.0/cpr\">" +
                        "<atom:id>tag:trifork.com,2011:cpr/person/v1</atom:id>" +
                        "<atom:updated>2011-11-07T09:56:12.278Z</atom:updated>" +
                        "<atom:title>Stamdata Registry Feed</atom:title>" +
                        "<atom:author>" +
                        "<atom:name>National Sundheds IT</atom:name>"+
                        "</atom:author>" +
                        "<atom:entry>" +
                        "<atom:id>tag:trifork.com,2011:sikrede/sikrede/v1/13206597710000000085</atom:id>" +
                        "<atom:title/>" +
                        "<atom:updated></atom:updated>" +
                        "<atom:content type=\"application/xml\">" +
                        "<RecordType>" +
                        "<Foo>42</Foo>" +
                        "<Bar>ABCDEFGH</Bar>" +
                        "</RecordType>" +
                        "</atom:content>" +
                        "</atom:entry>" +
                        "</atom:feed>";

        addElement(document, root, NAMESPACE_URI, "id", String.format("tag:trifork.com,2011:%s/%s/v1", register, datatype));
        // FIXME: Use actual updated date
        addElement(document, root, NAMESPACE_URI, "updated", "2011-11-07T09:56:12.278Z");
        addElement(document, root, NAMESPACE_URI, "title", "Stamdata Registry Feed");
        Element author = addElement(document, root, NAMESPACE_URI, "author", null);
        addElement(document, author, NAMESPACE_URI, "name", "National Sundheds IT");
        
        for (RecordMetadata metadata : records)
        {
            Record record = metadata.getRecord();
            
            Element entry = addElement(document, root, NAMESPACE_URI, "entry", null);
            // FIXME: Hardcoded, replace
            addElement(document, entry, NAMESPACE_URI, "id", "tag:trifork.com,2011:sikrede/sikrede/v1/13206597710000000085");
            addElement(document, entry, NAMESPACE_URI, "title", null);

            // FIXME: Hardcoded, replace
            addElement(document, entry, NAMESPACE_URI, "updated", ""); // Use AtomDate.toString(...)
            
            Element content = addElement(document, entry, NAMESPACE_URI, "content", null);
            content.setAttribute("type", "application/xml");

            // FIXME: Continue tomorrow
        }

        return document;
    }
    
    private Element addElement(Document document, Element parent, String namespace, String tagName, @Nullable String value)
    {
        Element element = document.createElementNS(namespace, tagName);
        if(value != null)
        {
            element.setNodeValue(value);
        }
        parent.appendChild(element);
        return element;
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
