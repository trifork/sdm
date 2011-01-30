package com.trifork.sdm.replication.admin.security;

import com.google.inject.*;
import com.google.inject.servlet.RequestScoped;
import com.trifork.rid2cpr.*;
import com.trifork.sdm.replication.util.PropertyServletModule;

import dk.itst.oiosaml.sp.*;
import dk.itst.oiosaml.sp.service.*;


public class SamlModule extends PropertyServletModule
{
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

		// FIXME: Figure out what these namespaces should be.
		// Setting the defaults is deprecated.
		ridService.setEndpoint(property("rid2cpr.endpoint"));
		ridService.setKeystore(property("rid2cpr.keystore"));
		ridService.setKeystorePassword(property("rid2cpr.keystorePassword"));

		int timeout = Integer.parseInt(property("rid2cpr.callTimeout"));
		ridService.setReadTimeout(timeout * 1000);

		ridService.init();

		bind(RID2CPRFacade.class).toInstance(ridService);
	}


	/**
	 * This provider's only purpose is to wrap the UserAssertionHolder class, to
	 * make it easier (read not insanely difficult) to test SAML.
	 */
	@Provides
	@RequestScoped
	public UserAssertion providerUserAssertion()
	{
		return UserAssertionHolder.get();
	}
}
