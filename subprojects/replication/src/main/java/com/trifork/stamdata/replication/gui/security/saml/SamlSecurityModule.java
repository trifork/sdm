package com.trifork.stamdata.replication.gui.security.saml;

import com.google.inject.Singleton;
import com.google.inject.servlet.RequestScoped;
import com.google.inject.servlet.ServletModule;
import com.trifork.stamdata.replication.gui.models.User;

import dk.itst.oiosaml.sp.service.SPFilter;

public class SamlSecurityModule extends ServletModule {

	@Override
	protected void configureServlets() {
		// FILTER ACCESS THROUGH SAML
		//
		// The LoginFilter requires that trafic has passed through
		// the SPFilter first.

		bind(SPFilter.class).in(Singleton.class);
		filter("/admin", "/admin/*").through(SPFilter.class);
		filter("/admin", "/admin/*").through(LoginFilter.class);
		bind(User.class).toProvider(LoginFilter.class).in(RequestScoped.class);
	}

}
