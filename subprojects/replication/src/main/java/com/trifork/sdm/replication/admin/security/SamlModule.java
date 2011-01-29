package com.trifork.sdm.replication.admin.security;

import javax.inject.Singleton;

import com.google.inject.Provides;
import com.google.inject.servlet.RequestScoped;
import com.trifork.rid2cpr.CachingRID2CPRFacadeImpl;
import com.trifork.rid2cpr.RID2CPRFacade;
import com.trifork.sdm.replication.util.PropertyServletModule;
import com.trifork.xmlquery.Namespaces;

import dk.itst.oiosaml.sp.UserAssertion;
import dk.itst.oiosaml.sp.UserAssertionHolder;
import dk.itst.oiosaml.sp.service.DispatcherServlet;
import dk.itst.oiosaml.sp.service.SPFilter;

public class SamlModule extends PropertyServletModule
{
	@Override
	protected void configureServlets()
	{
		bind(DispatcherServlet.class).in(Singleton.class);
		serve("/saml/*").with(DispatcherServlet.class);

		bind(SPFilter.class).in(Singleton.class);
		filter("/admin", "/admin/*").through(SPFilter.class);

		bind(SamlFilter.class).in(Singleton.class);
		filter("admin", "/admin/*").through(SamlFilter.class);

		// Bind the RID 2 CPR helper to get the users' CPR
		// from a remote service.

		String endpoint = property("rid2cpr.endpoint");
		String keystore = property("rid2cpr.keystore");
		String password = property("rid2cpr.keystorePassword");

		CachingRID2CPRFacadeImpl ridService = new CachingRID2CPRFacadeImpl();

		// FIXME: Figure out what these namespaces should be.
		// Setting the defaults is deprecated, supposedly insecure!
		ridService.setNamespaces(Namespaces.getOIONamespaces());
		ridService.setEndpoint(endpoint);
		ridService.setKeystore(keystore);
		ridService.setKeystorePassword(password);
		ridService.setReadTimeout(60000); // TODO: What excatly is this number?

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
