package com.trifork.stamdata.replication;

import java.util.Random;

import dk.sosi.seal.xml.Base64;


public class TokenHelper {

	public static byte[] createRandomToken() {

		byte[] token = new byte[512];
		new Random().nextBytes(token);
		return token;
	}

	public static String createRandomEncodedToken() {

		return Base64.encode(createRandomToken());
	}
}
