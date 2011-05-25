package com.trifork.stamdata.lookup.rest;

import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class RestModule extends JerseyServletModule {

	@Override
	protected void configureServlets() {
        bind(PersonResource.class);

        serve("/*").with(GuiceContainer.class);
		
	}

}
