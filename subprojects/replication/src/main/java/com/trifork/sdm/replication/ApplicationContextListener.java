package com.trifork.sdm.replication;


import static org.slf4j.LoggerFactory.*;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;

import com.google.inject.*;
import com.google.inject.servlet.GuiceServletContextListener;
import com.trifork.sdm.replication.db.DatabaseModule;
import com.trifork.sdm.replication.dgws.DGWSModule;
import com.trifork.sdm.replication.gui.GuiModule;
import com.trifork.sdm.replication.monitoring.MonitoringModule;
import com.trifork.sdm.replication.replication.RegistryModule;


public class ApplicationContextListener extends GuiceServletContextListener {

	private static final Logger logger = getLogger(ApplicationContextListener.class);


	@Override
	protected Injector getInjector() {
		Injector injector = null;

		try {
			logger.info("Initializing Stamdata Registry Service.");

			Collection<Module> modules = new ArrayList<Module>();

			modules.add(new DatabaseModule());
			modules.add(new DGWSModule());
			modules.add(new RegistryModule());
			modules.add(new GuiModule());
			modules.add(new MonitoringModule());

			injector = Guice.createInjector(modules);

			logger.info("Service initialized.");
		}
		catch (Exception e) {
			logger.error("Initialization failed, do to configuration error.", e);
		}

		return injector;
	}
}
