package com.trifork.sdm.replication.replication;

import static org.mockito.Mockito.*;

import java.sql.Connection;

import org.junit.Test;

import com.google.inject.Provider;
import com.trifork.sdm.replication.util.URLFactory;


public class XMLEntityWriterTest
{
	@SuppressWarnings("unchecked")
	@Test
	public void test() throws Exception
	{
		// Arrange
		Provider<Connection> provider = mock(Provider.class);
		URLFactory urlFactory = mock(URLFactory.class);
		XMLEntityWriter xmlEntityWriter = new XMLEntityWriter(provider, urlFactory);

		// Act
		// xmlEntityWriter.write(outputStream, resourceType, OutputFormat.XML,
		// 100, sinceDate, sinceId)

		// Assert
	}
}
