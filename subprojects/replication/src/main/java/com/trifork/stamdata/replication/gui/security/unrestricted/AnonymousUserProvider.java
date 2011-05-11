package com.trifork.stamdata.replication.gui.security.unrestricted;

import com.google.inject.Provider;
import com.trifork.stamdata.replication.gui.models.User;


public class AnonymousUserProvider implements Provider<User> {

	@Override
	public User get() {

		return new User("Anonymous", "CVR:00000000-RID:0000");
	}
}
