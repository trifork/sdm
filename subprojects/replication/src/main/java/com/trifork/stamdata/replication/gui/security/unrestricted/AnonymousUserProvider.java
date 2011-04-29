package com.trifork.stamdata.replication.gui.security.unrestricted;

import com.google.inject.Provider;
import com.trifork.stamdata.replication.gui.models.User;

public class AnonymousUserProvider implements Provider<User> {

	@Override
	public User get() {
		return new User("Anonymous", "0000000000", "00000000", "");
	}

}
