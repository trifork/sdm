package com.trifork.sdm.replication.admin.models;

import static org.mockito.Mockito.*;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.sql.SQLException;

import org.aopalliance.intercept.MethodInvocation;
import org.junit.Before;
import org.junit.Test;

public class RepositoryErrorLoggerTest {
	private MethodInvocation invocation;
	private RepositoryErrorLogger errorLogger;

	@Before
	public void setUp() throws Throwable {
		invocation = mock(MethodInvocation.class);
		errorLogger = new RepositoryErrorLogger();
	}
	
	@Test
	public void test_can_log_on_sql_exception() throws Throwable {
		// Arrange
		when(invocation.getMethod()).thenReturn(this.getClass().getMethod("doNothing"));
		when(invocation.proceed()).thenThrow(new SQLException());
		
		// Act
		try {
			errorLogger.invoke(invocation);
			fail();
		} catch (SQLException e) {
			// Expected
		}
		
		// Assert
		// This is not the best way to verify that we log the error. But the code
		// uses the static getLogger method, which we can not intercept.
		verify(invocation).getMethod();
	}
	
	public void doNothing() {}
	
	@Test(expected = NullPointerException.class)
	public void test_rethrow_exception_when_not_sql_exception() throws Throwable {
		// Arrange
		when(invocation.proceed()).thenThrow(new NullPointerException());
		
		// Act
		errorLogger.invoke(invocation);
		
		// Assert
	}
}
