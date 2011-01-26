package com.trifork.sdm.replication.admin;


import static org.slf4j.LoggerFactory.*;

import java.util.*;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.trifork.sdm.replication.admin.properties.Whitelist;


public class WhitelistModule extends AbstractModule
{
	private static final Logger LOG = getLogger(WhitelistModule.class);


	@Override
	protected void configure()
	{
		requireBinding(Properties.class);
	}


	@Inject
	@Provides
	@Whitelist
	@Singleton
	protected Map<String, String> provideWhitelist(Properties properties)
	{
		final String PREFIX = "replication.whitelist.";

		TreeMap<String, String> whitelist = new TreeMap<String, String>();

		@SuppressWarnings("rawtypes")
		Enumeration names = properties.propertyNames();

		if (!names.hasMoreElements())
		{
			LOG.warn("No CVRs have been white-listed.");
		}

		while (names.hasMoreElements())
		{
			String propertyName = (String) names.nextElement();

			if (propertyName.startsWith(PREFIX))
			{
				String firmName = propertyName.substring(PREFIX.length());
				String cvr = properties.getProperty(propertyName);

				whitelist.put(firmName, cvr);
			}
		}

		return whitelist;
	}
}
