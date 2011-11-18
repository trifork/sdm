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

import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;

import com.trifork.stamdata.Nullable;
import com.trifork.stamdata.persistence.RecordMetadata;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.DocumentSource;
import org.joda.time.DateTime;

import com.trifork.stamdata.persistence.Record;
import com.trifork.stamdata.persistence.RecordSpecification;
import com.trifork.stamdata.persistence.RecordSpecification.FieldSpecification;
import com.trifork.stamdata.persistence.RecordSpecification.RecordFieldType;


public class RecordXmlGenerator
{
    public static final String ATOM_NAMESPACE_URI = "http://www.w3.org/2005/Atom";
    public static final String STAMDATA_NAMESPACE_URI_PREFIX = "http://trifork.com/-/stamdata/3.0/";
    
    private RecordSpecification recordSpecification;

    public RecordXmlGenerator(RecordSpecification recordSpecification)
    {
        this.recordSpecification = recordSpecification;
    }

    public org.w3c.dom.Document generateXml(List<RecordMetadata> records, String register, String datatype, DateTime updated) throws TransformerException
    {
        String stamdataNamespaceUri = STAMDATA_NAMESPACE_URI_PREFIX + register;

        Document document = DocumentHelper.createDocument();
        document.setXMLEncoding("utf-8");
        
        Element root = document.addElement("atom:feed", ATOM_NAMESPACE_URI);
        
        addElement(root, ATOM_NAMESPACE_URI, "atom:id", String.format("tag:trifork.com,2011:%s/%s/v1", register, datatype));
        addElement(root, ATOM_NAMESPACE_URI, "atom:updated", AtomDate.toString(updated.toDate()));
        addElement(root, ATOM_NAMESPACE_URI, "atom:title", "Stamdata Registry Feed");
        Element author = addElement(root, ATOM_NAMESPACE_URI, "atom:author", null);
        addElement(author, ATOM_NAMESPACE_URI, "atom:name", "National Sundheds IT");
        
        for (RecordMetadata metadata : records)
        {
            Element entry = addElement(root, ATOM_NAMESPACE_URI, "atom:entry", null);
            String atomId = String.format("tag:trifork.com,2011:%s/%s/v1/%d%07d", register, datatype, metadata.getModifiedDate().getMillis(), metadata.getPid());
            addElement(entry, ATOM_NAMESPACE_URI, "atom:id", atomId);
            addElement(entry, ATOM_NAMESPACE_URI, "atom:title", null);

            addElement(entry, ATOM_NAMESPACE_URI, "atom:updated", AtomDate.toString(metadata.getModifiedDate().toDate()));
            
            Element content = addElement(entry, ATOM_NAMESPACE_URI, "atom:content", null);
            content.addAttribute("type", "application/xml");

            Element recordElement = addElement(content, stamdataNamespaceUri, datatype, null);
            for(FieldSpecification fieldSpecification : recordSpecification.getFieldSpecs())
            {
                addElement(recordElement, stamdataNamespaceUri, fieldSpecification.name, valueAsString(metadata.getRecord(), fieldSpecification));
            }
        }

        return convertToW3C(document);
    }
    
    private Element addElement(Element parent, String namespace, String tagName, @Nullable String value)
    {
        Element element = parent.addElement(tagName, namespace);
        if(value != null)
        {
            element.setText(value);
        }
        return element;
    }

    private String valueAsString(Record record, FieldSpecification fieldSpecification)
    {
        if(record.get(fieldSpecification.name) == null)
        {
            return "";
        }
        else if (fieldSpecification.type == RecordSpecification.RecordFieldType.ALPHANUMERICAL)
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
    
    private static final TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();

    public static org.w3c.dom.Document convertToW3C(org.dom4j.Document dom4jdoc) throws TransformerException
    {
        SAXSource source = new DocumentSource(dom4jdoc);
        DOMResult result = new DOMResult();

        Transformer transformer = TRANSFORMER_FACTORY.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        
        transformer.transform(source, result);
        return (org.w3c.dom.Document) result.getNode();
    }
}
