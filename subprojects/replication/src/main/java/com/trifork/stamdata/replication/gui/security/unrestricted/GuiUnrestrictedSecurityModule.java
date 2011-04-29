package com.trifork.stamdata.replication.gui.security.unrestricted;

import com.google.inject.servlet.RequestScoped;
import com.google.inject.servlet.ServletModule;
import com.trifork.stamdata.replication.gui.models.User;

public class GuiUnrestrictedSecurityModule extends ServletModule {

	@Override
	protected void configureServlets() {
		bind(User.class).toProvider(AnonymousUserProvider.class).in(RequestScoped.class);
	}
	
}
