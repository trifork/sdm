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

import static com.trifork.stamdata.persistence.RecordSpecification.field;

import java.util.Arrays;

import javax.xml.transform.TransformerException;

import com.trifork.stamdata.persistence.Record;
import com.trifork.stamdata.persistence.RecordBuilder;
import com.trifork.stamdata.persistence.RecordMetadata;
import com.trifork.stamdata.persistence.RecordSpecification;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

public class RecordXmlGeneratorTest {

    private RecordSpecification exampleRecordSpecification;
    private RecordXmlGenerator exampleXmlGenerator;

    @Before
    public void initialiseVariables()
    {
        this.exampleRecordSpecification = RecordSpecification.createSpecification("RecordType", "Dummy",
                field("Foo", 2).numerical(),
                field("Bar", 10));
        exampleXmlGenerator = new RecordXmlGenerator(exampleRecordSpecification);
    }
    
    @Test
    public void testSimpleXmlDocumentGeneration() throws ClassCastException, ClassNotFoundException, InstantiationException, IllegalAccessException, TransformerException
    {
        Instant validFrom = new DateTime(2005, 1, 1, 0, 0).toInstant();
        Instant validTo = new DateTime(2006, 1, 1, 0, 0).toInstant();
        Instant modifiedDate = new DateTime(2007, 1, 1, 0, 0, DateTimeZone.UTC).toInstant();
        Long pid = 10L;
        Record record = new RecordBuilder(exampleRecordSpecification).field("Foo", 42).field("Bar", "ABCDEFGH").build();
        RecordMetadata recordMetadata = new RecordMetadata(validFrom, validTo, modifiedDate, pid, record);

        DateTime updated = new DateTime(2011, 10, 17, 18, 41, 13, 123, DateTimeZone.UTC);
        Document document = exampleXmlGenerator.generateXml(Arrays.asList(recordMetadata), "sikrede", "sikrede", updated);

        String updatedDateInString = "2011-10-17T18:41:13.123Z";
        String modifiedDateInString = "2007-01-01T00:00:00.000Z";
        
        // FIXME: This should be UTF-8
        String expected = 
        "<?xml version=\"1.0\" encoding=\"UTF-16\"?>" +
        "<atom:feed xmlns:atom=\"http://www.w3.org/2005/Atom\">" +
        "<atom:id>tag:nsi.dk,2011:sikrede/sikrede/v1</atom:id>" +
        "<atom:updated>"+ updatedDateInString + "</atom:updated>" +
        "<atom:title>Stamdata Registry Feed</atom:title>" +
        "<atom:author>" +
        "<atom:name>National Sundheds IT</atom:name>"+
        "</atom:author>" +
        "<atom:entry>" +
        "<atom:id>tag:nsi.dk,2011:sikrede/sikrede/v1/11676096000000000010</atom:id>" +
        "<atom:title/>" +
        "<atom:updated>" + modifiedDateInString + "</atom:updated>" +
        "<atom:content type=\"application/xml\">" +
        "<sikrede xmlns=\"http://nsi.dk/-/stamdata/3.0/sikrede\">" +
        "<Foo>42</Foo>" +
        "<Bar>ABCDEFGH</Bar>" +
        "<ValidFrom>2004-12-31T23:00:00.000Z</ValidFrom>" +
        "<ValidTo>2005-12-31T23:00:00.000Z</ValidTo>" +
        "<ModifiedDate>2007-01-01T00:00:00.000Z</ModifiedDate>" +
        "</sikrede>" +
        "</atom:content>" +
        "</atom:entry>" +
        "</atom:feed>";

        String actual = serializeDomDocument(document);
        
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testStillValidXmlDocumentGeneration() throws ClassCastException, ClassNotFoundException, InstantiationException, IllegalAccessException, TransformerException
    {
        Instant validFrom = new DateTime(2005, 1, 1, 0, 0).toInstant();
        Instant validTo = null;
        Instant modifiedDate = new DateTime(2007, 1, 1, 0, 0, DateTimeZone.UTC).toInstant();
        Long pid = 10L;
        Record record = new RecordBuilder(exampleRecordSpecification).field("Foo", 42).field("Bar", "ABCDEFGH").build();
        RecordMetadata recordMetadata = new RecordMetadata(validFrom, validTo, modifiedDate, pid, record);
        
        DateTime updated = new DateTime(2011, 10, 17, 18, 41, 13, 123, DateTimeZone.UTC);
        Document document = exampleXmlGenerator.generateXml(Arrays.asList(recordMetadata), "sikrede", "sikrede", updated);
        
        String updatedDateInString = "2011-10-17T18:41:13.123Z";
        String modifiedDateInString = "2007-01-01T00:00:00.000Z";
        
        // FIXME: This should be UTF-8
        String expected = 
                "<?xml version=\"1.0\" encoding=\"UTF-16\"?>" +
                        "<atom:feed xmlns:atom=\"http://www.w3.org/2005/Atom\">" +
                        "<atom:id>tag:nsi.dk,2011:sikrede/sikrede/v1</atom:id>" +
                        "<atom:updated>"+ updatedDateInString + "</atom:updated>" +
                        "<atom:title>Stamdata Registry Feed</atom:title>" +
                        "<atom:author>" +
                        "<atom:name>National Sundheds IT</atom:name>"+
                        "</atom:author>" +
                        "<atom:entry>" +
                        "<atom:id>tag:nsi.dk,2011:sikrede/sikrede/v1/11676096000000000010</atom:id>" +
                        "<atom:title/>" +
                        "<atom:updated>" + modifiedDateInString + "</atom:updated>" +
                        "<atom:content type=\"application/xml\">" +
                        "<sikrede xmlns=\"http://nsi.dk/-/stamdata/3.0/sikrede\">" +
                        "<Foo>42</Foo>" +
                        "<Bar>ABCDEFGH</Bar>" +
                        "<ValidFrom>2004-12-31T23:00:00.000Z</ValidFrom>" +
                        "<ValidTo>2999-12-31T00:00:00.000Z</ValidTo>" +
                        "<ModifiedDate>2007-01-01T00:00:00.000Z</ModifiedDate>" +
                        "</sikrede>" +
                        "</atom:content>" +
                        "</atom:entry>" +
                        "</atom:feed>";
        
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
