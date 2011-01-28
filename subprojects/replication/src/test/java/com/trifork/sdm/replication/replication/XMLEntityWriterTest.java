package com.trifork.sdm.replication.replication;

import static org.mockito.Mockito.*;

import java.sql.Connection;

import javax.inject.Provider;

import org.junit.Test;
import org.mockito.Mockito;

public class XMLEntityWriterTest {
	@SuppressWarnings("unchecked")
	@Test
	public void test() throws Exception {
		// Arrange
		Provider<Connection> providerMock = (Provider<Connection>) mock(Provider.class);
		new XMLEntityWriter(null, null);
		
		// Act
		
		// Assert
	}
}
