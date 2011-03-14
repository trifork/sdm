package com.trifork.sdm.replication.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

import com.google.inject.*;
import com.google.inject.servlet.RequestScoped;
import com.trifork.rid2cpr.CachingRID2CPRFacadeImpl;
import com.trifork.rid2cpr.RID2CPRFacade;
import com.trifork.sdm.replication.gui.annotations.Whitelist;
import com.trifork.sdm.replication.gui.controllers.*;
import com.trifork.sdm.replication.gui.models.User;
import com.trifork.sdm.replication.gui.models.UserDao;
import com.trifork.sdm.replication.gui.security.LoginFilter;
import com.trifork.sdm.replication.util.ConfiguredModule;
import com.trifork.xmlquery.Namespaces;

import dk.itst.oiosaml.sp.UserAssertion;
import dk.itst.oiosaml.sp.UserAssertionHolder;
import dk.itst.oiosaml.sp.service.SPFilter;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;


public class GuiModule extends ConfiguredModule {

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
		filter("/admin", "/admin/*").through(LoginFilter.class);

		// Bind the RID 2 CPR helper to get the users' CPR
		// from a remote service.

		CachingRID2CPRFacadeImpl ridService = new CachingRID2CPRFacadeImpl();
		ridService.setEndpoint(getConfig().getString("rid2cpr.endpoint"));

		String keystore = getConfig().getString("rid2cpr.keystore");
		keystore = getClass().getClassLoader().getResource(keystore).toExternalForm();

		ridService.setKeystore(keystore);
		ridService.setKeystorePassword(getConfig().getString("rid2cpr.keystorePassword"));
		ridService.setReadTimeout(getConfig().getInt("rid2cpr.callTimeout"));

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
	 * This provider's only purpose is to wrap the UserAssertionHolder class, to make it easier to
	 * test SAML.
	 */
	@Provides
	@RequestScoped
	public UserAssertion providerUserAssertion() {

		return UserAssertionHolder.get();
	}


	@Provides
	@RequestScoped
	public User getCurrentUser(UserDao users, RID2CPRFacade service) throws SQLException {

		User user = null;

		try {
			UserAssertion assertion = UserAssertionHolder.get();

			// Look up the user's CPR by converting the RID (an ID on the user's certificate)
			// via a remote web-service. The results are cached.

			if (assertion != null) {
				String userID = assertion.getUserId();
				String userCPR = service.getCPR(userID);
				String userCVR = assertion.getCVRNumberIdentifier();

				user = users.find(userCPR, userCVR);
			}
		}
		catch (IOException e) {
			// TODO: Log
		}

		return user;
	}
}
