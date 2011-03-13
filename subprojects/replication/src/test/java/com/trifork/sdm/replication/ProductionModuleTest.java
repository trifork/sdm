package com.trifork.sdm.replication;


import static org.junit.Assert.*;

import org.junit.Test;

import com.google.inject.*;
import com.trifork.sdm.replication.admin.AdminstrationModule;
import com.trifork.sdm.replication.db.DatabaseModule;
import com.trifork.sdm.replication.dgws.DGWSModule;
import com.trifork.sdm.replication.monitoring.MonitoringModule;
import com.trifork.sdm.replication.replication.RegistryModule;


public class ProductionModuleTest {

	@Test
	public void should_not_fail_to_initialize() {
		// The creation of the injector will fail if the setup if wrong,
		// or we are missing some configuration in the config.properties.

		try {
			Guice.createInjector(new AbstractModule() {

				@Override
				public void configure() {
					install(new DatabaseModule());
					install(new DGWSModule());
					install(new RegistryModule());
					install(new AdminstrationModule());
					install(new MonitoringModule());
				}
			});
		}
		catch (CreationException e) {
			fail();
		}
	}
}
