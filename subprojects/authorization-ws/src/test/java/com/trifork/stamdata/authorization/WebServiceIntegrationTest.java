package com.trifork.stamdata.authorization;

import org.junit.Test;

import com.google.inject.Injector;

public class WebServiceIntegrationTest
{
	@Test
	public void should_be_able_to_start_the_webservice_from_the_guice_configuration()
	{
		ApplicationContextListener contextListener = new ApplicationContextListener();

		Injector injector = contextListener.getInjector();

		injector.getInstance(WebService.class);
	}
}
