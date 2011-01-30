package com.trifork.sdm.replication.client;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import java.util.Date;

import javax.persistence.Column;

import com.trifork.stamdata.*;


public class RecordExtractor
{

	private final List<EntityEntry> elements = new ArrayList<EntityEntry>();
	private final String pidColumn;


	public RecordExtractor(Class<? extends Record> entity)
	{
		this.pidColumn = Entities.getIdColumnName(entity);

		for (Method getter : entity.getMethods())
		{
			Column annotation = getter.getAnnotation(Column.class);

			if (annotation != null)
			{
				elements.add(new EntityEntry(getter));
			}
		}
	}


	public Map<String, Object> extract(ResultSet row) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SQLException
	{
		// Make an instance of the entity.

		Map<String, Object> properties = new HashMap<String, Object>();

		// Fill all the property values.

		for (EntityEntry entry : elements)
		{
			Object value;

			if (entry.parameterType.isAssignableFrom(String.class))
			{

				value = row.getString(entry.columnName);
			}
			else if (entry.parameterType.isAssignableFrom(Date.class))
			{

				value = row.getTimestamp(entry.columnName);
			}
			else if (entry.parameterType == int.class || entry.parameterType == Integer.class)
			{

				value = row.getInt(entry.columnName);
			}
			else if (entry.parameterType == long.class || entry.parameterType == Long.class)
			{

				value = row.getLong(entry.columnName);
			}
			else if (entry.parameterType == Boolean.class || entry.parameterType == boolean.class)
			{

				value = row.getBoolean(entry.columnName);
			}
			else
			{

				throw new RuntimeException("Unsupported Extractor Column Type: " + entry.parameterType.getSimpleName());
			}

			properties.put(entry.columnName, value);
		}

		// Fill the mandatory property values.

		properties.put("__PID", row.getLong(pidColumn));
		properties.put("__ModifiedDate", row.getTimestamp("ModifiedDate"));
		properties.put("__ValidFrom", row.getTimestamp("ValidFrom"));
		properties.put("__ValidTo", row.getTimestamp("ValidTo"));

		return properties;
	}

	/**
	 * Helper class that generates the XML.
	 * 
	 * We might as well generate the start- and end-tags at initialization, that
	 * way we don't have to do it on a per instance basis.
	 */
	protected class EntityEntry
	{

		public String columnName;
		public Class<?> parameterType;


		public EntityEntry(Method getter)
		{

			this.columnName = Entities.getColumnName(getter);
			this.parameterType = getter.getReturnType();
		}
	}
}
