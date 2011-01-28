package com.trifork.sdm.replication.saml;

import dk.itst.oiosaml.sp.UserAssertion;
import dk.itst.oiosaml.sp.UserAssertionHolder;

/**
 * This class's purpose is to wrap the UserAssertionHolder class, to make it
 * easier (ready not insane) to test SAML.
 */
public class SingleSignonHelper
{
	public UserAssertion getUser()
	{
		return UserAssertionHolder.get();
	}
}
