package com.trifork.sdm.replication.admin.models;

import static org.slf4j.LoggerFactory.getLogger;

import java.sql.SQLException;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;

public class RepositoryErrorLogger implements MethodInterceptor {

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		try {
			return invocation.proceed();
		} catch (SQLException t) {
			Logger logger = getLogger(invocation.getClass());
			logger.error("Exception during " + invocation.getMethod().getName(), t);
			throw t;
		}
	}

}
