package com.trifork.sdm.replication.security;


import javax.inject.Singleton;

import com.google.inject.Provides;
import com.trifork.rid2cpr.CachingRID2CPRFacadeImpl;
import com.trifork.sdm.replication.util.PropertyServletModule;
import com.trifork.xmlquery.Namespaces;


public class SamlModule extends PropertyServletModule
{
	@Override
	protected void configureServlets()
	{
		/*
		bind(DispatcherServlet.class).in(Singleton.class);
		serve("/saml/*").with(DispatcherServlet.class);

		bind(SPFilter.class).in(Singleton.class);
		filter("/admin/*").through(SPFilter.class);
		
		bind(SamlFilter.class).in(Singleton.class);
		filter("/admin/*").through(SamlFilter.class);
		*/
	}


	@Provides
	@Singleton
	protected CachingRID2CPRFacadeImpl providerRidHelper()
	{
		String endpoint = property("replication.rid2cpr.endpoint");
		String keystore = property("replication.rid2cpr.keystore");
		String password = property("replication.rid2cpr.keystorePassword");

		CachingRID2CPRFacadeImpl ridHelper = new CachingRID2CPRFacadeImpl();

		// FIXME: Figure out what these namespaces should be.
		// Setting the defaults is deprecated, supposedly insecure!
		ridHelper.setNamespaces(Namespaces.getOIONamespaces());

		ridHelper.setEndpoint(endpoint);
		ridHelper.setKeystore(keystore);
		ridHelper.setKeystorePassword(password);
		ridHelper.setReadTimeout(60000);

		ridHelper.init();

		return ridHelper;
	}
}
