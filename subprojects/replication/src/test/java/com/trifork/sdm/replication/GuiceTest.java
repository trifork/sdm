package com.trifork.sdm.replication;

import static com.google.inject.util.Modules.*;

import org.junit.After;

import com.google.inject.*;


/**
 * Test superclass meant to make testing with Guice easier.
 * 
 * Override the {@code configure()} method to setup custom dependencies for your
 * test classes.
 * 
 * Generally the injector should be interacted with during the set up phase.
 * This means while the {@code @Before} methods are executed. It is not
 * available in before {@code @BeforeClass} methods.
 */
public abstract class GuiceTest extends AbstractModule
{
	/**
	 * This is left static since JUnit is quite *special* when it comes to how
	 * it wants to initialize its tests.
	 */
	private final Injector injector;


	protected GuiceTest()
	{		
		Module production = new ProductionModule();
		Module test = override(production).with(this);

		injector = Guice.createInjector(test);
	}


	/**
	 * This method is designed to be implemented by subclasses when they need
	 * special dependencies overridden in the DI graph.
	 */
	@Override
	protected void configure()
	{
		// No action by default.
	}


	/**
	 * Gets the injector that holds the tests' dependency graph.
	 */
	protected Injector getInjector()
	{
		return injector;
	}
}
