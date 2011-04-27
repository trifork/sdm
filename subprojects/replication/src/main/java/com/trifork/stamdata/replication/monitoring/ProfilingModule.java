
// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
// 
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
// 
// Contributor(s): Contributors are attributed in the source code
// where applicable.
// 
// The Original Code is "Stamdata".
// 
// The Initial Developer of the Original Code is Trifork Public A/S.
// 
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
// 
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

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

/**
 * A simple filter that that messures the time it takes to make a request.
 * 
 * @author Thomas Børlum (thb@trifork.com)
 */
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
