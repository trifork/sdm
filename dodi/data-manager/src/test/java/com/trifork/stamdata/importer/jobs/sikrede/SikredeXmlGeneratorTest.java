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

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import com.trifork.stamdata.persistence.SikredeFields;
import com.trifork.stamdata.persistence.SikredeRecord;
import com.trifork.stamdata.persistence.SikredeRecordBuilder;
import com.trifork.stamdata.persistence.SikredeFields.SikredeType;

public class SikredeXmlGeneratorTest {

    private SikredeFields exampleSikredeFields;
    private SikredeXmlGenerator exampleXmlGenerator;

    @Before
    public void initialiseVariables()
    {
        this.exampleSikredeFields = SikredeFields.newSikredeFields(
                "Foo", SikredeType.NUMERICAL, 2,
                "Bar", SikredeType.ALFANUMERICAL, 10);
        exampleXmlGenerator = new SikredeXmlGenerator(exampleSikredeFields);
    }
    
    @Test
    public void testSimpleXmlDocumentGeneration() throws ClassCastException, ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        SikredeRecordBuilder recordBuilder = new SikredeRecordBuilder(exampleSikredeFields);
        SikredeRecord sikredeRecord = recordBuilder.field("Foo", 42).field("Bar", "ABCDEFGH").build();
        Document document = exampleXmlGenerator.generateXml(sikredeRecord);
        
        String expected = 
                "<?xml version=\"1.0\" encoding=\"UTF-16\"?>\n" + 
                        "<SikredeRecord>"+
                        "<Foo>42</Foo>" + 
                        "<Bar>ABCDEFGH</Bar>" + 
                        "</SikredeRecord>";
        String actual = serializeDomDocument(document);
        
        Assert.assertEquals(expected, actual);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testEmptyRecordGeneration()
    {
        SikredeRecord sikredeRecord = new SikredeRecord();
        exampleXmlGenerator.generateXml(sikredeRecord);
    }
    
    @Test
    public void testEmptySchemaRecordGeneration() throws ClassCastException, ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        exampleSikredeFields = SikredeFields.newSikredeFields();
        exampleXmlGenerator = new SikredeXmlGenerator(exampleSikredeFields);
        SikredeRecordBuilder recordBuilder = new SikredeRecordBuilder(exampleSikredeFields);
        SikredeRecord sikredeRecord = recordBuilder.build();
        Document document = exampleXmlGenerator.generateXml(sikredeRecord);
        
        String expected = 
                "<?xml version=\"1.0\" encoding=\"UTF-16\"?>\n" + 
                        "<SikredeRecord/>";
        String actual = serializeDomDocument(document);
        
        Assert.assertEquals(expected, actual);
    }
    
    private String serializeDomDocument(Document document) throws ClassCastException, ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
        DOMImplementationLS impl = (DOMImplementationLS)registry.getDOMImplementation("LS");
        LSSerializer writer = impl.createLSSerializer();
        return writer.writeToString(document);
    }
}
