package com.trifork.sdm.replication;

import static com.google.inject.util.Modules.*;

import com.google.inject.*;


public abstract class GuiceTest extends AbstractModule
{
	private static Injector injector;


	protected GuiceTest()
	{
		Module production = new ProductionModule();
		Module test = override(production).with(this);

		injector = Guice.createInjector(test);
	}


	protected Injector getInjector()
	{
		return injector;
	}
}
