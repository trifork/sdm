package com.trifork.sdm.replication.util;

import java.security.SignatureException;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class AuthorizationManager
{
	private static final long MILLIS_TO_SECS = 1000;
	private static final String SEPARATOR = ":";

	private final String sharedSecret;


	public AuthorizationManager(String sharedSecret)
	{
		this.sharedSecret = sharedSecret;
	}


	public String create(String path, Date date)
	{
		long expires = date.getTime() / MILLIS_TO_SECS;
		
		try
		{
			String signature = calculateRFC2104HMAC(path + expires, sharedSecret);
			return expires + SEPARATOR + signature;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}


	public boolean validate(String path, String authorization)
	{
		long now = System.currentTimeMillis() / MILLIS_TO_SECS;

		try
		{
			String[] parts = authorization.split(SEPARATOR);
			long expires = Long.parseLong(parts[0]);
			String signature = parts[1];

			if (now <= expires)
			{
				String expectedSignature = calculateRFC2104HMAC(path + expires, sharedSecret);
				return signature.equals(expectedSignature);
			}
		}
		catch (Exception e)
		{
		}

		return false;
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
		final String ENCRYPTION_ALGORITHM = "HmacSHA1";

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
