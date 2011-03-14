package com.trifork.stamdata.replication;

import static org.slf4j.LoggerFactory.*;
import java.util.ArrayList;
import java.util.List;
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
			modules.add(new GuiModule());
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
