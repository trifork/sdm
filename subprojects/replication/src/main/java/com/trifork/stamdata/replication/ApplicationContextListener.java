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

package com.trifork.stamdata.replication;

import static org.slf4j.LoggerFactory.*;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.slf4j.Logger;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;
import com.trifork.stamdata.replication.db.DatabaseModule;
import com.trifork.stamdata.replication.gui.GuiModule;
import com.trifork.stamdata.replication.monitoring.MonitoringModule;
import com.trifork.stamdata.replication.monitoring.ProfilingModule;
import com.trifork.stamdata.replication.replication.RegistryModule;
import com.trifork.stamdata.replication.security.dgws.DGWSModule;


public class ApplicationContextListener extends GuiceServletContextListener {

	private static final Logger logger = getLogger(ApplicationContextListener.class);
	private ServletContext servletContext;

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		servletContext = servletContextEvent.getServletContext();
		super.contextInitialized(servletContextEvent);
	}
	@Override
	protected Injector getInjector() {

		Injector injector = null;

		try {
			logger.info("Configuring Stamdata Service.");

			// TODO: Each of these modules can be left in or out.
			// When deployed the user should configure which features
			// should be enabled.

			List<Module> modules = new ArrayList<Module>();

			// The order these modules are added is not unimportant.
			// Since some of them add filters to the filter chain
			// they must be placed in the right order, e.i. some of
			// the filters depend on settings from previous filters.

			modules.add(new DatabaseModule());
			modules.add(new ProfilingModule());
			modules.add(new DGWSModule());
			modules.add(new RegistryModule());
			modules.add(new GuiModule(servletContext));
			modules.add(new MonitoringModule());

			injector = Guice.createInjector(modules);

			logger.info("Service configured.");
		}
		catch (Exception e) {
			logger.error("Initialization failed do to a configuration error.", e);
		}

		return injector;
	}
}
