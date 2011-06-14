package com.trifork.stamdata.lookup.rest;

import com.google.common.collect.ImmutableMap;
import com.sun.jersey.core.util.FeaturesAndProperties;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class RestModule extends JerseyServletModule {
	@Override
	protected void configureServlets() {
        bind(PersonResource.class);
        bind(PersonClientResource.class);
        serve("/*").with(GuiceContainer.class, ImmutableMap.of(FeaturesAndProperties.FEATURE_FORMATTED, Boolean.TRUE.toString()));
	}
}
