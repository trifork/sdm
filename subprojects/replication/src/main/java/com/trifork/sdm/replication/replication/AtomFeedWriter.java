package com.trifork.sdm.replication.replication;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.google.inject.Inject;
import com.trifork.sdm.replication.replication.annotations.Registry;
import com.trifork.sdm.replication.replication.models.Record;
import com.trifork.sdm.replication.util.AtomDate;
import com.trifork.sdm.replication.util.Namespace;

public class AtomFeedWriter {

	private static final String ATOM_NS = "http://www.w3.org/2005/Atom";
	private static final String TAG_PREFIX = "tag:trifork.com,2011:";

	private final Marshaller marshaller;


	@Inject
	AtomFeedWriter(@Registry Marshaller marshaller) {
		this.marshaller = marshaller;
	}


	public void write(String entityName, List<? extends Record> records, XMLStreamWriter writer) throws IOException {

		try {
			// Start the feed.

			writer.writeStartDocument();

			writer.setDefaultNamespace(ATOM_NS);
			writer.writeStartElement("feed");
			writer.writeNamespace(null, ATOM_NS);
			writer.writeNamespace("sd", Namespace.STAMDATA_3_0);

			writeFeedMetadata(entityName, records, writer);

			// Write each record as an ATOM entry.

			for (Record record : records) {
				writeEntry(writer, entityName, record);
			}

			// End the feed.

			writer.writeEndDocument();
		}
		catch (Exception e) {
			throw new IOException(e);
		}
	}


	protected void writeEntry(XMLStreamWriter feed, String path, Record record) throws XMLStreamException, JAXBException {

		feed.writeStartElement("entry");

		feed.writeStartElement("id");
		feed.writeCharacters(TAG_PREFIX + path + "?" + "offset=" + record.getOffset());
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


	protected void writeFeedMetadata(String path, List<? extends Record> records, XMLStreamWriter feed) throws XMLStreamException {

		// There is currently no stability in the feeds' output,
		// This means that if you access the same URL two times
		// you might get two different results. There is nothing
		// wrong with that, stability is simply an attractive property.
		// Therefore we have to change 'updated' and 'id' every time a
		// page is accessed.

		feed.writeStartElement("id");
		feed.writeCharacters(TAG_PREFIX + path /* TODO: "?offset=" + lastRecord.getOffset() + "&count=" + records.size() */);
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
