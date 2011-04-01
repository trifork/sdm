// Stamdata - Copyright (C) 2011 National Board of e-Health (NSI)
// 
// All source code and information supplied as part of Stamdata is
// copyright to National Board of e-Health.
// 
// The source code has been released under a dual license - meaning you can
// use either licensed version of the library with your code.
// 
// It is released under the Common Public License 1.0, a copy of which can
// be found at the link below.
// http://www.opensource.org/licenses/cpl1.0.php
// 
// It is released under the LGPL (GNU Lesser General Public License), either
// version 2.1 of the License, or (at your option) any later version. A copy
// of which can be found at the link below.
// http://www.gnu.org/copyleft/lesser.html

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
