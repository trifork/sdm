// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
// 
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
// 
// Contributor(s): Contributors are attributed in the source code
// where applicable.
// 
// The Original Code is "Stamdata".
// 
// The Initial Developer of the Original Code is Trifork Public A/S.
// 
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
// 
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

package com.trifork.stamdata.replication.gui;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import com.google.inject.TypeLiteral;
import com.google.inject.servlet.ServletModule;
import com.trifork.rid2cpr.CachingRID2CPRFacadeImpl;
import com.trifork.rid2cpr.RID2CPRFacade;
import com.trifork.stamdata.Nullable;
import com.trifork.stamdata.replication.gui.annotations.Whitelist;
import com.trifork.stamdata.replication.gui.controllers.ClientController;
import com.trifork.stamdata.replication.gui.controllers.LogController;
import com.trifork.stamdata.replication.gui.controllers.UserController;
import com.trifork.xmlquery.Namespaces;

import freemarker.cache.WebappTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;


public class GuiModule extends ServletModule {

	private final String rid2cprURL;
	private final String rid2cprKeystore;
	private final String rid2cprPassword;
	private final int rid2cprTimeout;

	private Map<String, String> whiteList;

	public GuiModule(String rid2cprURL, String rid2cprKeystore, @Nullable String rid2cprPassword, int rid2cprTimeout, Map<String, String> whiteList) {

		this.rid2cprURL = checkNotNull(rid2cprURL);
		this.rid2cprKeystore = checkNotNull(rid2cprKeystore);
		this.whiteList = checkNotNull(whiteList);
		this.rid2cprPassword = rid2cprPassword;
		
		checkArgument(rid2cprTimeout > 0);
		this.rid2cprTimeout = rid2cprTimeout;
	}

	@Override
	protected final void configureServlets() {

		// HTML TEMPLATE ENGINE
		//
		// We use Freemaker for HTML templates.
		// The template files can be found in the 'webapp' directory
		// and all have the extension .ftl.

		Configuration config = new Configuration();

		// Specify the data source where the template files come from.
		// Here I set a file directory for it:

		config.setTemplateLoader(new WebappTemplateLoader(getServletContext()));

		// Specify how templates will see the data-model.
		// We just use the default:

		config.setObjectWrapper(new DefaultObjectWrapper());

		bind(Configuration.class).toInstance(config);

		// Bind the RID 2 CPR helper to get the users' CPR
		// from a remote service.

		CachingRID2CPRFacadeImpl ridService = new CachingRID2CPRFacadeImpl();
		ridService.setEndpoint(rid2cprURL);
		ridService.setKeystore(rid2cprKeystore);
		ridService.setKeystorePassword(rid2cprPassword);
		ridService.setReadTimeout(rid2cprTimeout);

		// TODO: Setting the default namespaces is deprecated.
		// We should set our own. (The ones that we actually need.)

		ridService.setNamespaces(Namespaces.getOIONamespaces());
		ridService.init();

		bind(RID2CPRFacade.class).toInstance(ridService);

		// SERVE THE ADMIN GUI

		serve("/admin/users", "/admin/users/*").with(UserController.class);
		serve("/admin", "/admin/clients", "/admin/clients/*").with(ClientController.class);
		serve("/admin/log", "/admin/log/*").with(LogController.class);

		// WHITELIST CVR NUMBERS
		//
		// These CVR numbers that can be used when creating new administrators.

		if (whiteList.size() > 0) {
			bind(new TypeLiteral<Map<String, String>>() {}).annotatedWith(Whitelist.class).toInstance(whiteList);
		}
		else {
			addError("No cvr-numbers have been white-listed. Change the configuration file.");
		}
	}
}
