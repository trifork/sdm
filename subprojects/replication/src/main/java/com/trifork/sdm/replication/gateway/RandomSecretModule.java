package com.trifork.sdm.replication.gateway;


import static org.slf4j.LoggerFactory.*;

import java.security.SecureRandom;

import org.slf4j.Logger;

import com.google.inject.AbstractModule;
import com.trifork.sdm.replication.settings.Secret;


/**
 * Module that produces a randomized secret key, used for encryption and signing.
 */
public class RandomSecretModule extends AbstractModule
{
	private static final Logger LOG = getLogger(RandomSecretModule.class);


	@Override
	protected void configure()
	{
		LOG.trace("Configuring security module.");

		SecureRandom random = new SecureRandom();
		byte[] secretBytes = new byte[512];
		random.nextBytes(secretBytes);

		String secret = new String(secretBytes);

		bindConstant().annotatedWith(Secret.class).to(secret);
	}
}
