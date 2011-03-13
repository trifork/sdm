package com.trifork.sdm.replication.admin;

import java.io.File;
import java.net.URL;
import java.util.*;

import com.google.inject.*;
import com.google.inject.servlet.RequestScoped;
import com.trifork.rid2cpr.CachingRID2CPRFacadeImpl;
import com.trifork.rid2cpr.RID2CPRFacade;
import com.trifork.sdm.replication.admin.annotations.Whitelist;
import com.trifork.sdm.replication.admin.controllers.*;
import com.trifork.sdm.replication.admin.security.SamlFilter;
import com.trifork.sdm.replication.util.ConfiguredModule;
import com.trifork.xmlquery.Namespaces;

import dk.itst.oiosaml.sp.UserAssertion;
import dk.itst.oiosaml.sp.UserAssertionHolder;
import dk.itst.oiosaml.sp.service.SPFilter;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;


public class AdminstrationModule extends ConfiguredModule {

	@SuppressWarnings("deprecation")
	@Override
	protected void configureServlets() {

		// HTML TEMPLATE ENGINE
		//
		// We use Freemaker to template HTML.
		// The template files can be found in the webapp-dir
		// and all have the extension .ftl.

		Configuration config = new Configuration();

		String TEMPLATE_DIR = "views";
		URL TEMPLATE_DIR_URL = getClass().getClassLoader().getResource(TEMPLATE_DIR);

		try {
			File templateDir = new File(TEMPLATE_DIR_URL.toURI());

			// Specify the data source where the template files come from.
			// Here I set a file directory for it:

			config.setDirectoryForTemplateLoading(templateDir);

			// Specify how templates will see the data-model.
			// We just use the default:

			config.setObjectWrapper(new DefaultObjectWrapper());

			bind(Configuration.class).toInstance(config);
		}
		catch (Exception e) {
			addError("Invalid template directory.", e);
		}


		// WHITELIST CVR NUMBERS
		//
		// These CVR numbers that can be used when creating new administrators.
		// The list in maintained in the config.properties file.

		String[] cvrNumbers = getConfig().getStringArray("whitelist");
		Set<String> whitelist = new HashSet<String>(Arrays.asList(cvrNumbers));

		if (whitelist.size() == 0) {
			addError("No CVR-numbers have been whitelisted. Change the configuration file.");
		}
		else {
			bind(new TypeLiteral<Set<String>>() {}).annotatedWith(Whitelist.class).toInstance(whitelist);
		}


		// FILTER ACCESS THROUGH SAML
		//

		bind(SPFilter.class).in(Singleton.class);
		filter("/admin", "/admin/*").through(SPFilter.class);
		filter("/admin", "/admin/*").through(SamlFilter.class);

		// Bind the RID 2 CPR helper to get the users' CPR
		// from a remote service.

		CachingRID2CPRFacadeImpl ridService = new CachingRID2CPRFacadeImpl();
		ridService.setEndpoint(getConfig().getString("rid2cpr.endpoint"));
		ridService.setKeystore(getConfig().getString("rid2cpr.keystore"));
		ridService.setKeystorePassword(getConfig().getString("rid2cpr.keystorePassword"));
		ridService.setReadTimeout(getConfig().getInt("rid2cpr.callTimeout") * 1000);
		
		// TODO: Setting the default namespaces is deprecated.
		// We should set our own. (The ones that we actually need.)

		ridService.setNamespaces(Namespaces.getOIONamespaces());
		ridService.init();
		bind(RID2CPRFacade.class).toInstance(ridService);


		// SERVE THE ADMIN GUI
		//

		serve("/admin/users", "/admin/users/*").with(UserController.class);
		serve("/admin/log", "/admin/log/*").with(AuditLogController.class);
		serve("/admin", "/admin/clients", "/admin/clients/*").with(ClientController.class);
	}


	/**
	 * This provider's only purpose is to wrap the UserAssertionHolder class,
	 * to make it easier to test SAML.
	 */
	@Provides
	@RequestScoped
	public UserAssertion providerUserAssertion() {

		return UserAssertionHolder.get();
	}
}
