package com.trifork.sdm.replication.util;

public class WebLinking {

	public static String createNextLink(String entityID, String offset) {
		return String.format("<stamdata://%s?offset=%s>; rel=\"next\"", entityID, offset);
	}
}
