package com.trifork.sdm.replication.admin.security;


import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.servlet.RequestScoped;
import com.trifork.rid2cpr.CachingRID2CPRFacadeImpl;
import com.trifork.rid2cpr.RID2CPRFacade;
import com.trifork.sdm.replication.util.ConfiguredModule;
import com.trifork.xmlquery.Namespaces;

import dk.itst.oiosaml.sp.UserAssertion;
import dk.itst.oiosaml.sp.UserAssertionHolder;
import dk.itst.oiosaml.sp.service.DispatcherServlet;
import dk.itst.oiosaml.sp.service.SPFilter;


public class SamlModule extends ConfiguredModule
{
	@SuppressWarnings("deprecation")
	@Override
	protected void configureServlets()
	{
		bind(SPFilter.class).in(Singleton.class);
		filter("/admin", "/admin/*").through(SPFilter.class);

		bind(SamlFilter.class).in(Singleton.class);
		filter("/admin", "/admin/*").through(SamlFilter.class);

		bind(DispatcherServlet.class).in(Singleton.class);
		serve("/saml/*").with(DispatcherServlet.class);

		// Bind the RID 2 CPR helper to get the users' CPR
		// from a remote service.

		CachingRID2CPRFacadeImpl ridService = new CachingRID2CPRFacadeImpl();

		ridService.setEndpoint(getConfig().getString("rid2cpr.endpoint"));
		ridService.setKeystore(getConfig().getString("rid2cpr.keystore"));
		ridService.setKeystorePassword(getConfig().getString("rid2cpr.keystorePassword"));
		
		// Setting the defaults is deprecated.
		
		ridService.setNamespaces(Namespaces.getOIONamespaces());

		int timeout = getConfig().getInt("rid2cpr.callTimeout");
		ridService.setReadTimeout(timeout * 1000);

		ridService.init();

		bind(RID2CPRFacade.class).toInstance(ridService);
	}


	/**
	 * This provider's only purpose is to wrap the UserAssertionHolder class, to make it easier
	 * (read not insanely difficult) to test SAML.
	 */
	@Provides
	@RequestScoped
	public UserAssertion providerUserAssertion()
	{
		return UserAssertionHolder.get();
	}
}
