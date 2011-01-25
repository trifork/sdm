package com.trifork.stamdata.importer;


import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class Configuration
{
	private static Properties properties;


	public Configuration(String configFilePath) throws ConfigurationException
	{
		if (properties != null) return;
		
		try
		{
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configFilePath);

			properties = new Properties();
			properties.load(inputStream);
		}
		catch (IOException e)
		{
			throw new ConfigurationException("Could not load property file='config.properties'.", e);
		}
	}
	
	public String getStringProperty(String key, String defaultValue) throws ConfigurationException
	{
		return properties.getProperty(key, defaultValue);
	}


	public String getStringProperty(String key) throws ConfigurationException
	{
		String value = properties.getProperty(key);

		if (value == null)
		{
			throw new ConfigurationException(String.format("Property %s was not defined.", key));
		}

		return value;
	}


	public boolean getBooleanProperty(String key) throws ConfigurationException
	{
		String stringValue = getStringProperty(key);
		boolean value = Boolean.parseBoolean(stringValue);
		return value;
	}


	public int getIntProperty(String key) throws ConfigurationException
	{
		int value;
		String stringValue = getStringProperty(key);

		try
		{
			value = Integer.parseInt(stringValue);
		}
		catch (NumberFormatException e)
		{
			String message = String
					.format("Property with name='%s' and value='%s' could not be parsed to type='%s'.", key, stringValue, Date.class);
			throw new ConfigurationException(message, e);
		}

		return value;
	}


	public Map<String, String> getPropertiesContaining(String substring)
	{

		Map<String, String> foundProperties = new HashMap<String, String>();

		for (Map.Entry<Object, Object> property : properties.entrySet())
		{

			String key = property.getKey().toString();

			if (key.contains(substring))
			{
				String value = property.getValue().toString();
				foundProperties.put(key, value);
			}
		}

		return foundProperties;
	}


	public Date getDateProperty(String key) throws ConfigurationException
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		Date value = null;
		String stringValue = getStringProperty(key);

		try
		{
			value = dateFormat.parse(stringValue);
		}
		catch (ParseException e)
		{
			throw new ConfigurationException("", e);
		}

		return value;
	}
}
