package com.trifork.sdm.replication.replication;


import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.sql.*;
import java.util.Calendar;

import javax.xml.stream.XMLStreamReader;

import org.custommonkey.xmlunit.*;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.google.inject.Provider;
import com.sun.xml.fastinfoset.stax.StAXDocumentParser;
import com.trifork.sdm.replication.util.URLFactory;
import com.trifork.stamdata.registre.sor.Apotek;


public class XMLEntityWriterTest extends XMLTestCase
{
	private XMLEntityWriter xmlEntityWriter;
	private URLFactory urlFactory;
	private Calendar now;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception
	{
		now = Calendar.getInstance();

		ResultSet resultSet = mock(ResultSet.class);
		when(resultSet.next()).thenReturn(true, true, false);
		when(resultSet.getString("ApotekPID")).thenReturn("42");
		when(resultSet.getTimestamp("ModifiedDate")).thenReturn(new Timestamp(now.getTimeInMillis()));
		when(resultSet.getTimestamp("ValidFrom")).thenReturn(new Timestamp(Integer.MIN_VALUE));
		when(resultSet.getTimestamp("ValidTo")).thenReturn(new Timestamp(Integer.MAX_VALUE));

		PreparedStatement preparedStatement = mock(PreparedStatement.class);
		when(preparedStatement.executeQuery()).thenReturn(resultSet);

		Connection connection = mock(Connection.class);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

		Provider<Connection> provider = mock(Provider.class);
		when(provider.get()).thenReturn(connection);

		now.add(Calendar.DAY_OF_MONTH, -1);

		urlFactory = mock(URLFactory.class);
		when(urlFactory.create(org.mockito.Matchers.any(Class.class), anyInt(), anyString(), anyString())).thenReturn("http://www.some.url");

		xmlEntityWriter = new XMLEntityWriter(provider, urlFactory);
	}


	@Test
	public void test_can_generate_valid_fi() throws Exception
	{
		// Arrange

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		// Act

		xmlEntityWriter.write(outputStream, Apotek.class, OutputFormat.FastInfoset, 100, now.getTime(), 0);

		// Assert

		InputStream page = new ByteArrayInputStream(outputStream.toByteArray());
		XMLStreamReader streamReader = new StAXDocumentParser(page);
		
		// Run through all the document and make sure we can parse it.
		// TODO: Validate it against a schema.
		
		while (streamReader.next() != streamReader.END_DOCUMENT) {}
	}


	@Test
	public void test_can_generate_valid_xml() throws Exception
	{
		// Arrange

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		// Act

		xmlEntityWriter.write(outputStream, Apotek.class, OutputFormat.XML, 100, now.getTime(), 0);

		// Assert

		String xml = outputStream.toString();
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<page>" + "<apotek rowId=\"\" historyId=\"\" effectuationDate=\"\" expirationDate=\"\" />" + "<apotek rowId=\"\" historyId=\"\" effectuationDate=\"\" expirationDate=\"\" />" + "<nextPageURL>http://www.some.url</nextPageURL>" + "</page>";
		assertXMLSimilar(expected, xml);
		assertThat(xml, containsString("<nextPageURL>http://www.some.url</nextPageURL>"));
		verify(urlFactory).create(eq(Apotek.class), eq(100), org.mockito.Matchers.endsWith("0000000042"), eq("XML"));
	}


	private void assertXMLSimilar(String expected, String actual) throws SAXException, IOException
	{
		assertNotNull(actual);

		IgnoreTextAndAttributeValuesDifferenceListener differenceListener = new IgnoreTextAndAttributeValuesDifferenceListener();
		Diff diff = new Diff(expected, actual);
		diff.overrideDifferenceListener(differenceListener);

		assertTrue("Not similar: " + diff, diff.similar());
	}
}
