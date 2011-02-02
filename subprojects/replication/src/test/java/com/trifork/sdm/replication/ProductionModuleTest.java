package com.trifork.sdm.replication;


import static org.junit.Assert.*;

import org.junit.Test;

import com.google.inject.CreationException;
import com.google.inject.Guice;


public class ProductionModuleTest
{
	@Test
	public void should_not_fail_to_initialize()
	{
		// The creation of the injector will fail if the setup if wrong,
		// or we are missing some configuration in the config.properties.
		
		try
		{
			Guice.createInjector(new ProductionModule());
		}
		catch (CreationException e)
		{
			fail();
		}
	}
}
