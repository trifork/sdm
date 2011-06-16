package com.trifork.stamdata.lookup.security;

import java.util.Collection;

import com.google.inject.Provides;
import com.google.inject.servlet.ServletModule;
import com.trifork.stamdata.lookup.security.annotations.AuthorizedClients;

public class SecurityModule extends ServletModule {

	private final Collection<String> authorizedClients;

	public SecurityModule(Collection<String> authorizedClients) {
		this.authorizedClients = authorizedClients;
	}
	
	@Override
	protected void configureServlets() {
		filter("*").through(AuthorizationFilter.class);
	}
	
	@AuthorizedClients
	@Provides
	public Collection<String> provideAuthorizedClients() {
		return authorizedClients;
	}
}
