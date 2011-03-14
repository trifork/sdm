package com.trifork.stamdata.replication.monitoring;

import static org.slf4j.LoggerFactory.getLogger;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;


@Singleton
public class ProfilingModule extends ServletModule implements Filter {

	private static final Logger logger = getLogger(ProfilingModule.class);

	@Override
	protected void configureServlets() {

		filter("/stamdata/*").through(ProfilingModule.class);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		StopWatch timer = new StopWatch();

		timer.start();

		chain.doFilter(request, response);

		timer.stop();

		logger.info("Responded in: " + timer.getTime() / 1000.0 + " secs.");
	}

	@Override
	public void destroy() {

	}
}
