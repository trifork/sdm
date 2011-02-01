package com.trifork.sdm.replication.db;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;

public class TransactionManagerTest {
	@Test
	public void test_rollback_on_error() throws Throwable {
		// Arrange
		Connection connection = mock(Connection.class);
		DataSource dataSource = mock(DataSource.class);
		when(dataSource.getConnection()).thenReturn(connection);
		
		MethodInvocation methodInvocation = mock(MethodInvocation.class);
		when(methodInvocation.proceed()).thenThrow(new SQLException());
		
		TransactionManager transactionManager = new TransactionManager(dataSource);
		
		// Act
		try {
			transactionManager.invoke(methodInvocation);
			fail();
		} catch (Throwable t) {
			// Expected
		}
		
		// Assert
		verify(connection).setAutoCommit(false);
		verify(connection, never()).commit();
		verify(connection).rollback();
		verify(connection).close();
	}
}
