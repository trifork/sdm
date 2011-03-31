package com.trifork.stamdata.replication.replication;

import static org.apache.commons.lang.RandomStringUtils.randomAscii;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;

import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.io.FileUtils;
import org.hibernate.ScrollableResults;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.trifork.stamdata.replication.mocks.MockEntity;


@Ignore
public class XMLEntityWriterTest {

	private AtomFeedWriter writer;
	private ByteArrayOutputStream outputStream;

	@Before
	public void setUp() throws Exception {

		Marshaller marshaller = mock(Marshaller.class);
		marshaller.marshal(anyObject(), Mockito.any(XMLStreamWriter.class));

		outputStream = new ByteArrayOutputStream();

		writer = new AtomFeedWriter(marshaller);
	}

	@Test
	public void test_can_generate_valid_atom_feed() throws Exception {

		ScrollableResults records = mock(ScrollableResults.class);

		when(records.next()).thenReturn(true, true, true, false);
		when(records.get(0)).thenReturn(createRecord(), createRecord(), createRecord());

		writer.write("foo/bar/v1", records, outputStream, false);

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

		when(entity.getId()).thenReturn(randomAscii(10));
		when(entity.getUpdated()).thenReturn(new Date());
		when(entity.getOffset()).thenCallRealMethod();

		return entity;
	}
}
