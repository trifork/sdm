package com.trifork.sdm.replication;


import com.google.inject.AbstractModule;
import com.trifork.sdm.replication.db.DatabaseModule;
import com.trifork.sdm.replication.dgws.DGWSModule;
import com.trifork.sdm.replication.gui.GuiModule;
import com.trifork.sdm.replication.monitoring.MonitoringModule;
import com.trifork.sdm.replication.replication.RegistryModule;


public class ProductionModule extends AbstractModule {

	@Override
	protected void configure() {
		
		install(new DatabaseModule());
		install(new DGWSModule());
		install(new RegistryModule());
		install(new GuiModule());
		install(new MonitoringModule());
	}
}
