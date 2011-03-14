package com.trifork.stamdata.replication.db;

import static org.slf4j.LoggerFactory.getLogger;
import java.io.IOException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.slf4j.Logger;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class PersistenceFilter implements Filter {

	private static final Logger logger = getLogger(PersistenceFilter.class);
	private final Provider<EntityManager> em;

	@Inject
	PersistenceFilter(Provider<EntityManager> em) {

		this.em = em;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		EntityTransaction transaction = em.get().getTransaction();

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
			em.get().close();
		}
	}

	@Override
	public void destroy() {

	}
}
