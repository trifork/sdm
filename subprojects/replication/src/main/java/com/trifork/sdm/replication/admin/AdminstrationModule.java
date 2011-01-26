package com.trifork.sdm.replication.admin;


import com.trifork.sdm.replication.admin.controllers.*;
import com.trifork.sdm.replication.admin.views.TemplateModule;
import com.trifork.sdm.replication.security.SamlModule;
import com.trifork.sdm.replication.util.PropertyServletModule;


public class AdminstrationModule extends PropertyServletModule
{
	@Override
	protected void configureServlets()
	{
		serve("/admin/admins", "/admin/admins/*").with(AdminController.class);
		serve("/admin/log", "/admin/log/*").with(LogController.class);
		serve("/admin", "/admin/users", "/admin/users/*").with(ClientController.class);

		// Template Engine used for the views' HTML.

		install(new TemplateModule());

		// White list of CVR numbers that can be used when creating
		// new administrators.

		install(new WhitelistModule());

		// Filter users through SAML, redirecting then to login
		// if needed.

		install(new SamlModule());
	}
}
