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
import com.trifork.stamdata.replication.replication.annotations.Registry;
import com.trifork.stamdata.replication.replication.views.View;
import com.trifork.stamdata.replication.util.Namespace;


public class AtomFeedWriter {

	private static final String ATOM_NS = "http://www.w3.org/2005/Atom";
	private static final String TAG_PREFIX = "tag:trifork.com,2011:";

	private final Marshaller marshaller;

	@Inject
	AtomFeedWriter(@Registry Marshaller marshaller) {

		this.marshaller = checkNotNull(marshaller);
	}

	public void write(String entityName, ScrollableResults records, OutputStream outputStream, boolean useFastInfoset) throws IOException {

		checkNotNull(entityName);
		checkNotNull(records);
		checkNotNull(outputStream);
		
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
			writer.writeNamespace("sd", Namespace.STAMDATA_3_0);

			writeFeedMetadata(entityName, writer);

			// Write each record as an ATOM entry.

			while (records.next()) {
				View view = (View)records.get(0);
				writeEntry(writer, entityName, view);
			}

			// End the feed.

			writer.writeEndDocument();
		}
		catch (Exception e) {
			throw new IOException(e);
		}
	}

	protected void writeEntry(XMLStreamWriter feed, String path, View record) throws XMLStreamException, JAXBException {

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
