package com.trifork.stamdata.replication.replication;

import static com.google.common.base.Preconditions.checkNotNull;


public class WebLinking {

	public static String createNextLink(String entityID, String offset) {

		checkNotNull(entityID);
		checkNotNull(offset);

		return String.format("<stamdata://%s?offset=%s>; rel=\"next\"", entityID, offset);
	}
}
