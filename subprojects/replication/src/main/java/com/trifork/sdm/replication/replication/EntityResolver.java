package com.trifork.sdm.replication.replication;

import java.util.Map;

import com.trifork.stamdata.Record;


/**
 * Resolves a class based on it's resource name.
 * 
 * This is in fact a glorified map that is easy to inject. You can also think of
 * it as a factory.
 */
public class EntityResolver
{
	private final Map<String, Class<? extends Record>> mappings;


	/**
	 * Creates a new instance of the class.
	 * 
	 * @param mappings
	 * a map from resource name to type.
	 */
	public EntityResolver(Map<String, Class<? extends Record>> mappings)
	{
		assert mappings != null;

		this.mappings = mappings;
	}


	/**
	 * Resolves a resource name to a type.
	 * 
	 * @param resourceName
	 * name of the resource.
	 * @return the type mapped to the resource name, or null if it doesn't
	 * exist.
	 */
	public Class<? extends Record> get(String resourceName)
	{
		return mappings.get(resourceName);
	}
}
