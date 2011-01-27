package com.trifork.sdm.replication.db;


import java.lang.annotation.*;
import java.sql.Connection;

import javax.sql.DataSource;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.google.inject.Inject;
import com.google.inject.Provider;


public class TransactionManager implements MethodInterceptor, Provider<Connection>
{
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public static @interface Transactional
	{
	}


	// We can't use constructor injection with interceptors,
	// so we are using field injection instead.
	@Inject
	private DataSource dataStore;
	private ThreadLocal<Connection> connectionStore = new ThreadLocal<Connection>();


	public Connection get() throws OutOfTransactionException
	{
		Connection connection = connectionStore.get();

		// If the interceptor would fail to get the connection
		// we would not get to call the provider from client code at all

		if (connection == null)
		{
			throw new OutOfTransactionException();
		}

		return connection;
	}


	public Object invoke(MethodInvocation methodInvocation) throws Throwable
	{
		Connection conn = connectionStore.get();

		// The thread local connection can only be set by the interceptor
		// if it is not null the interceptor was already invoked.

		if (conn != null)
		{
			// just continue
			return methodInvocation.proceed();
		}

		// We must open a new connection and appropriately close it at the end
		// if we fail to get the connection, no problem, the target method will not get invoked at
		// all.
		conn = dataStore.getConnection();

		// Force auto commit to false.
		conn.setAutoCommit(false);

		// Set Thread Local connection.
		connectionStore.set(conn);
		Connection deleteMe = connectionStore.get();

		// Make sure we commit/rollback, close the connection and unset the thread local around
		// top-level method call
		try
		{
			Object returnValue = methodInvocation.proceed();
			conn.commit();
			return returnValue;
		}
		catch (Throwable t)
		{
			conn.rollback();
			throw t;
		}
		finally
		{
			connectionStore.set(null);
			conn.close();
		}
	}


	public static class OutOfTransactionException extends RuntimeException
	{
		private static final long serialVersionUID = 1L;


		public OutOfTransactionException()
		{
		}


		public OutOfTransactionException(String message)
		{
			super(message);
		}


		public OutOfTransactionException(String message, Throwable cause)
		{
			super(message, cause);
		}


		public OutOfTransactionException(Throwable cause)
		{
			super(cause);
		}
	}
}
