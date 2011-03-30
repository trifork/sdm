package com.trifork.stamdata.replication.db;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class PersistenceFilter implements Filter {

	private static final Logger logger = getLogger(PersistenceFilter.class);
	private final Provider<Session> sessions;

	@Inject
	PersistenceFilter(Provider<Session> sessions) {

		this.sessions = sessions;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		Transaction transaction = sessions.get().getTransaction();

		try {
			transaction.begin();
			chain.doFilter(request, response);
			transaction.commit();
		}
		catch (Exception e) {
			transaction.rollback();
			logger.error("An unexpected error occured.", e);
		}
		finally {
			sessions.get().close();
		}
	}

	@Override
	public void destroy() {

	}
}
