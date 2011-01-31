package com.trifork.sdm.replication.admin;

import com.trifork.sdm.replication.admin.controllers.*;
import com.trifork.sdm.replication.admin.security.WhitelistModule;
import com.trifork.sdm.replication.admin.views.TemplateModule;
import com.trifork.sdm.replication.util.ConfiguredModule;


public class AdminstrationModule extends ConfiguredModule
{
	@Override
	protected void configureServlets()
	{
		serve("/admin/users", "/admin/users/*").with(UserController.class);
		serve("/admin/log", "/admin/log/*").with(AuditLogController.class);
		serve("/admin", "/admin/clients", "/admin/clients/*").with(ClientController.class);

		// Template Engine used for the views' HTML.

		install(new TemplateModule());

		// White list of CVR numbers that can be used when creating
		// new administrators.

		install(new WhitelistModule());

		// Filter users through SAML, redirecting then to login
		// if needed.

		// TODO: install(new SamlModule());
	}
}
