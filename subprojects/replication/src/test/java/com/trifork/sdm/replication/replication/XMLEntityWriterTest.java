package com.trifork.sdm.replication.replication;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

import com.google.inject.Provides;
import com.trifork.sdm.replication.GuiceTest;
import com.trifork.sdm.replication.util.URLFactory;


public class XMLEntityWriterTest extends GuiceTest
{
	protected String nextURL;


	@Override
	protected void configure()
	{

	}


	@Test
	public void test() throws Exception
	{
		// Arrange

		// XMLEntityWriter xmlEntityWriter = new XMLEntityWriter(provider, urlFactory);

		// Act
		// xmlEntityWriter.write(outputStream, resourceType, OutputFormat.XML,
		// 100, sinceDate, sinceId)

		// Assert
	}


	@Provides
	public URLFactory provideURLFactory()
	{
		URLFactory factory = mock(URLFactory.class);
		when(factory.create(any(Class.class), anyInt(), anyString(), anyString())).thenReturn(nextURL);

		return factory;
	}
}
