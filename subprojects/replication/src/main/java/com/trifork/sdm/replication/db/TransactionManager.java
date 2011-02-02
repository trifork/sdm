package com.trifork.sdm.replication.db;


import java.sql.Connection;

import javax.sql.DataSource;

import org.aopalliance.intercept.*;

import com.google.inject.Provider;


public class TransactionManager implements MethodInterceptor, Provider<Connection>
{
	private final DataSource dataStore;
	private final ThreadLocal<Connection> connectionStore = new ThreadLocal<Connection>();


	public TransactionManager(DataSource source)
	{
		this.dataStore = source;
	}


	@Override
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


	@Override
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
		// if we fail to get the connection, no problem, the target method will
		// not get invoked at
		// all.
		conn = dataStore.getConnection();

		// Force auto commit to false.
		conn.setAutoCommit(false);

		// Set Thread Local connection.
		connectionStore.set(conn);

		// Make sure we commit/rollback, close the connection and unset the
		// thread local around
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

		public OutOfTransactionException() {}
	}
}
