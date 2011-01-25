package com.trifork.sdm.replication.admin;


import java.util.*;

import com.trifork.sdm.replication.settings.Whitelist;
import com.trifork.sdm.replication.util.PropertyModule;


public class WhitelistModule extends PropertyModule
{
	private static final String PREFIX = "replication.whitelist.";


	@Override
	@SuppressWarnings("rawtypes")
	protected void configure()
	{
		TreeMap<String, String> whitelist = new TreeMap<String, String>();

		Enumeration names = getProperties().propertyNames();

		while (names.hasMoreElements())
		{

			String propertyName = (String) names.nextElement();

			if (propertyName.startsWith(PREFIX))
			{

				String firmName = propertyName.substring(PREFIX.length());
				String cvr = property(propertyName);

				whitelist.put(firmName, cvr);
			}
		}

		bind(Map.class).annotatedWith(Whitelist.class).toInstance(whitelist);
	}
}
