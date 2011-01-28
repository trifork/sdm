package com.trifork.sdm.replication.replication;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.sql.*;

import java.util.Calendar;

import javax.inject.Provider;

import org.junit.Test;

import com.google.inject.Provides;
import com.trifork.sdm.replication.GuiceTest;
import com.trifork.sdm.replication.util.URLFactory;
import com.trifork.stamdata.registre.sor.Apotek;


public class XMLEntityWriterTest extends GuiceTest
{
	protected String nextURL;


	@Test
	public void test() throws Exception
	{
		// Arrange

		Calendar now = Calendar.getInstance();

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

		Provider<Connection> provider = (Provider<Connection>) mock(Provider.class);
		when(provider.get()).thenReturn(connection);

		now.add(Calendar.DAY_OF_MONTH, -1);
		
//		URLFactory urlFactory = mock(URLFactory.class);
//		when(urlFactory.create(any(Class.class), anyInt(), anyString(), anyString())).
		
//		XMLEntityWriter xmlEntityWriter = new XMLEntityWriter(provider, urlFactory);
//		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//		
//		// Act
//		xmlEntityWriter.write(outputStream, Apotek.class, OutputFormat.XML, 100, now.getTime(), 0);

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
