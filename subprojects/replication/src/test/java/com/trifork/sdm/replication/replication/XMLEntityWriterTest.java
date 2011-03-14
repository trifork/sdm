package com.trifork.sdm.replication.replication;


import static org.apache.commons.lang.RandomStringUtils.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.util.*;

import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.*;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.trifork.sdm.replication.mocks.MockEntity;
import com.trifork.sdm.replication.replication.models.Record;


public class XMLEntityWriterTest {

	private AtomFeedWriter writer;
	private XMLStreamWriter streamWriter;
	private ByteArrayOutputStream outputStream;


	@Before
	public void setUp() throws Exception {

		Marshaller marshaller = mock(Marshaller.class);
		marshaller.marshal(anyObject(), Mockito.any(XMLStreamWriter.class));

		outputStream = new ByteArrayOutputStream();
		streamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream);

		writer = new AtomFeedWriter(marshaller);
	}


	@Test
	public void test_can_generate_valid_atom_feed() throws Exception {

		List<Record> records = new ArrayList<Record>();

		records.add(createRecord());
		records.add(createRecord());
		records.add(createRecord());

		writer.write("foo/bar/v1", records, streamWriter);

		ByteArrayInputStream output = new ByteArrayInputStream(outputStream.toByteArray());
		
		// ATOM 1.0 can actually not be described fully by XML Schema.
		// But for simplicity we use a XSD that is almost complete.

		// 1. Lookup a factory for the W3C XML Schema language
		SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

		// 2. Compile the schema.
		// Here the schema is loaded from a java.io.File, but you could use
		// a java.net.URL or a javax.xml.transform.Source instead.
		File schemaLocation = FileUtils.toFile(getClass().getResource("Atom10.xsd"));
		Schema schema = factory.newSchema(schemaLocation);

		// 3. Get a validator from the schema.
		Validator validator = schema.newValidator();

		// 4. Parse the document you want to check.
		Source source = new StreamSource(output);

		// 5. Check the document
		validator.validate(source);
	}


	// TODO: Assert that the entities contain the right info.

	// HELPERS

	public MockEntity createRecord() {

		MockEntity entity = mock(MockEntity.class);

		when(entity.getID()).thenReturn(randomAscii(10));
		when(entity.getUpdated()).thenReturn(new Date());
		when(entity.getOffset()).thenCallRealMethod();

		return entity;
	}
}
