package com.trifork.sdm.replication.util;


import java.security.SignatureException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;

import org.apache.commons.codec.binary.Base64;

import com.trifork.sdm.replication.settings.Secret;
import com.trifork.stamdata.Nullable;


public class SignatureFactory
{
	private static final String ENCRYPTION_ALGORITHM = "HmacSHA1";

	private final String secret;


	@Inject
	SignatureFactory(@Secret String secret)
	{
		this.secret = secret;
	}


	public String create(String type, long expires, @Nullable String historyId, int pageSize)
	{
		String signature = null;

		StringBuilder builder = new StringBuilder().append(type).append(expires).append(historyId).append(pageSize);

		try
		{
			signature = calculateRFC2104HMAC(builder.toString(), secret);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}

		return signature;
	}


	/**
	 * Computes RFC 2104-complaint HMAC signature.
	 * 
	 * @param data The data to be signed.
	 * @param key The signing key.
	 * @return The Base64-encoded RFC 2104-complaint HMAC signature.
	 * @throws java.security.SignatureException when signature generation fails
	 */
	private static String calculateRFC2104HMAC(String data, String key) throws SignatureException
	{
		String result;

		try
		{
			// get an hmac_sha1 key from the raw key bytes
			SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), ENCRYPTION_ALGORITHM);

			// get an hmac_sha1 Mac instance and initialize with the signing key
			Mac mac = Mac.getInstance(ENCRYPTION_ALGORITHM);
			mac.init(signingKey);

			// compute the hmac on input data bytes
			byte[] rawHmac = mac.doFinal(data.getBytes());

			// base64-encode the hmac
			result = Base64.encodeBase64URLSafeString(rawHmac);
		}
		catch (Exception e)
		{
			throw new SignatureException("Failed to generate HMAC encoding: " + e.getMessage());
		}

		return result;
	}
}
