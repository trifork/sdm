package com.trifork.sdm.replication.db;


import java.sql.Connection;
import java.sql.SQLException;

import javax.inject.Inject;
import javax.inject.Provider;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;


public class TransactionManager implements MethodInterceptor
{
	private final Provider<Connection> connectionProvider;


	@Inject
	TransactionManager(Provider<Connection> connectionProvider)
	{
		this.connectionProvider = connectionProvider;
	}


	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable
	{
		Connection connection = null;
		Object invocationResult = null;

		Throwable invocationExpection = null;

		try
		{
			connection = connectionProvider.get();
			invocationResult = invocation.proceed();
			connection.commit();
		}
		catch (Throwable t)
		{
			try
			{
				if (connection != null) connection.rollback();
			}
			catch (SQLException e)
			{
			}

			// We need to throw the original exception
			// later to be completely transparent.

			invocationExpection = t;
		}
		finally
		{
			try
			{
				if (connection != null) connection.close();
			}
			catch (SQLException e)
			{
			}

			if (invocationExpection != null) throw invocationExpection;
		}

		return invocationResult;
	}
}
