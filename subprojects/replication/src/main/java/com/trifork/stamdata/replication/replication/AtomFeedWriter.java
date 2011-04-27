
// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
// 
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
// 
// Contributor(s): Contributors are attributed in the source code
// where applicable.
// 
// The Original Code is "Stamdata".
// 
// The Initial Developer of the Original Code is Trifork Public A/S.
// 
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
// 
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

package com.trifork.stamdata.replication.replication;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.hibernate.ScrollableResults;

import com.google.inject.Inject;
import com.sun.xml.fastinfoset.stax.factory.StAXOutputFactory;
import com.trifork.stamdata.replication.replication.views.View;
import com.trifork.stamdata.replication.replication.views.Views;

/**
 * Writes a set of records into an output stream as an Atom 1.0 feed.
 * 
 * @author Thomas Børlum (thb@trifork.com)
 */
public class AtomFeedWriter {

	private static final String ATOM_NS = "http://www.w3.org/2005/Atom";
	
	/**
	 * The tag prefix is used to create unique id's for the entities.
	 * This is a well defined scheme, and you should not change it,
	 * not even the year.
	 */
	private static final String TAG_PREFIX = "tag:trifork.com,2011:";

	private final ViewXmlHelper viewXmlHelper;

	@Inject
	AtomFeedWriter(ViewXmlHelper viewXmlHelper) {

		this.viewXmlHelper = checkNotNull(viewXmlHelper);
	}

	public int write(Class<? extends View> viewClass, ScrollableResults records, OutputStream outputStream, boolean useFastInfoset) throws IOException {

		checkNotNull(viewClass);
		checkNotNull(records);
		checkNotNull(outputStream);
		records.beforeFirst();
		
		String entityName = Views.getViewPath(viewClass);
		int writtenRecords = 0;
		
		try {
			XMLStreamWriter writer;
			
			writer = (useFastInfoset)
				? StAXOutputFactory.newInstance().createXMLStreamWriter(outputStream, "UTF-8")
				: XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream, "UTF-8");
			
			// Start the feed.

			writer.writeStartDocument("utf-8", "1.0");

			writer.setDefaultNamespace(ATOM_NS);
			writer.writeStartElement("feed");
			writer.writeNamespace(null, ATOM_NS);
			writer.writeNamespace("sd", viewXmlHelper.getNamespace(viewClass.newInstance()));

			writeFeedMetadata(entityName, writer);

			// Write each record as an ATOM entry.

			Marshaller marshaller = viewXmlHelper.createMarshaller(viewClass);
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

			while (records.next()) {
				View view = (View)records.get(0);
				writeEntry(writer, entityName, view, marshaller);
				writtenRecords++;
			}

			// End the feed.

			writer.writeEndDocument();
			return writtenRecords;
		}
		catch (Exception e) {
			throw new IOException(e);
		}
	}

	protected void writeEntry(XMLStreamWriter feed, String path, View record, Marshaller marshaller) throws XMLStreamException, JAXBException {

		feed.writeStartElement("entry");

		feed.writeStartElement("id");
		feed.writeCharacters(TAG_PREFIX + path + "/" + record.getOffset());
		feed.writeEndElement(); // Id

		// The title element is required,
		// we'll just leave it empty.

		feed.writeEmptyElement("title");

		feed.writeStartElement("updated");
		feed.writeCharacters(AtomDate.format(record.getUpdated()));
		feed.writeEndElement(); // Updated

		// Write the actual entity inside the content tag.

		feed.writeStartElement("content");
		feed.writeAttribute("type", "application/xml");
		marshaller.marshal(record, feed);
		feed.writeEndElement(); // Content

		feed.writeEndElement(); // Entry
	}

	protected void writeFeedMetadata(String path, XMLStreamWriter feed) throws XMLStreamException {

		// There is currently no stability in the feeds' output,
		// This means that if you access the same URL two times
		// you might get two different results. There is nothing
		// wrong with that, stability is simply an attractive property.
		// Therefore we have to change 'updated' and 'id' every time a
		// page is accessed.
		//
		// TODO: Add offset=nextOffset count=records.size() to the feed id.

		feed.writeStartElement("id");
		feed.writeCharacters(TAG_PREFIX + path);
		feed.writeEndElement(); // Id

		feed.writeStartElement("updated");
		feed.writeCharacters(AtomDate.format(new Date()));
		feed.writeEndElement(); // Updated

		// Write the feed meta data.

		feed.writeStartElement("title");
		feed.writeCharacters("Stamdata Registry Feed");
		feed.writeEndElement(); // Title

		feed.writeStartElement("author");

		feed.writeStartElement("name");
		feed.writeCharacters("Trifork Public A/S");
		feed.writeEndElement(); // Name

		feed.writeStartElement("email");
		feed.writeCharacters("stamdata.support@trifork.com");
		feed.writeEndElement(); // Email

		feed.writeEndElement(); // Author
	}
}
