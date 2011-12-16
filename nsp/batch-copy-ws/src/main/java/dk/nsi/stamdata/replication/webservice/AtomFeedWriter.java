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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;

import com.google.inject.Inject;

import dk.nsi.stamdata.views.View;
import dk.nsi.stamdata.views.Views;

/**
 * Writes a set of records into an output Atom 1.0 output feed.
 * 
 * @author Thomas Børlum <thb@trifork.com>
 */
public class AtomFeedWriter
{
	private static final String STREAM_ENCODING = "UTF-8";

	private static final String ATOM_NS = "http://www.w3.org/2005/Atom";

	/**
	 * The tag prefix is used to create unique id's for the entities. This is a
	 * well defined scheme, and you should not change it, not even the year.
	 */
	private static final String TAG_PREFIX = "tag:nsi.dk,2011:";

	private final ViewXmlHelper viewXmlHelper;

	@Inject
	AtomFeedWriter(ViewXmlHelper viewXmlHelper)
	{
		this.viewXmlHelper = checkNotNull(viewXmlHelper);
	}

	public <T extends View> org.w3c.dom.Document write(Class<T> viewClass, List<T> records) throws IOException
	{
		checkNotNull(viewClass);
		checkNotNull(records);
		
		String entityName = Views.getViewPath(viewClass);

		try
		{
		    Document document = DocumentHelper.createDocument();
	        document.setXMLEncoding("utf-8");
	        
			// Start the feed.
		    
            Element feed = document.addElement("atom:feed", ATOM_NS);
            
            // Get the namespace of the view class.
            
            String viewNS = viewClass.getPackage().getAnnotation(XmlSchema.class).namespace();
            Namespace namespace = new Namespace(null, viewNS);
            document.getRootElement().add(namespace);
            
			writeFeedMetadata(entityName, feed);

			// Write each record as an ATOM entry.

			Marshaller marshaller = viewXmlHelper.createMarshaller(viewClass);
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, STREAM_ENCODING);

			for(Object record: records)
			{
			    View view = (View) record;
				writeEntry(feed, entityName, view, marshaller);
			}
			
			return convertToW3C(document);
		}
		catch (Exception e)
		{
			throw new IOException("Failed while writing ATOM feed.", e);
		}
	}

	protected void writeEntry(Element feed, String path, View record, Marshaller marshaller) throws JAXBException
	{
		Element entry = feed.addElement("atom:entry", ATOM_NS);
		
		entry.addElement("atom:id", ATOM_NS).addText(TAG_PREFIX + path + "/" + record.getOffset());
		entry.addElement("atom:title", ATOM_NS); // Empty, but required by ATOM for human redability.
		entry.addElement("atom:updated", ATOM_NS).addText(AtomDate.toString(record.getUpdated()));

		// Write the actual entity inside the content tag.

		Element content = entry.addElement("atom:content", ATOM_NS).addAttribute("type", "application/xml");
		
		DocumentResult dr = new DocumentResult();
		marshaller.marshal(record, dr);
		content.add(dr.getDocument().getRootElement());
	}

	protected void writeFeedMetadata(String path, Element feed)
	{
		// There is currently no stability in the feeds' output,
		// This means that if you access the same URL two times
		// you might get two different results. There is nothing
		// wrong with that, stability is simply an attractive property.
		// Therefore we have to change 'updated' and 'id' every time a
		// page is accessed.
		//
		// TODO: Add offset=nextOffset count=records.size() to the feed id.

		feed.addElement("atom:id", ATOM_NS).addText(TAG_PREFIX + path);
		feed.addElement("atom:updated", ATOM_NS).addText(AtomDate.toString(new Date()));

		// Write the feed meta data.

		feed.addElement("atom:title", ATOM_NS).addText("Stamdata Registry Feed");
		feed.addElement("atom:author", ATOM_NS).addElement("atom:name", ATOM_NS).addText("National Sundheds IT");
    }

    private static final TransformerFactory transformerFactory = TransformerFactory.newInstance();

    public static org.w3c.dom.Document convertToW3C(org.dom4j.Document dom4jdoc) throws TransformerException
    {
        SAXSource source = new DocumentSource(dom4jdoc);
        DOMResult result = new DOMResult();

        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        
        transformer.transform(source, result);
        return (org.w3c.dom.Document) result.getNode();
    }
}
