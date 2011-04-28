package com.trifork.stamdata.replication.gui.security.twowayssl;

import com.google.inject.servlet.RequestScoped;
import com.google.inject.servlet.ServletModule;
import com.trifork.stamdata.replication.gui.models.User;

public class TwoWaySslSecurityModule extends ServletModule {

	@Override
	protected void configureServlets() {
		filter("/admin", "/admin/*").through(TwoWaySslLoginFilter.class);
		bind(User.class).toProvider(TwoWaySslUserProvider.class).in(RequestScoped.class);
	}

}
