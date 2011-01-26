package com.trifork.stamdata.persistence;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Table;

import com.trifork.stamdata.EntityHelper;
import com.trifork.stamdata.Entities;
import com.trifork.stamdata.Record;


public class Dataset<T extends Record>
{

	private Map<Object, List<T>> entities = new HashMap<Object, List<T>>();
	private Class<? extends Record> type;


	public Dataset(List<T> entities, Class<? extends Record> T)
	{

		this.type = T;

		for (T entity : entities)
		{
			List<T> ents = new ArrayList<T>();
			ents.add(entity);
			this.entities.put(entity.getKey(), ents);
		}
	}


	public Dataset(Class<? extends Record> type)
	{

		this.type = type;
	}


	public Collection<T> getEntities()
	{

		Collection<T> allEnts = new ArrayList<T>();

		for (List<T> ents : entities.values())
		{
			allEnts.addAll(ents);
		}

		return allEnts;
	}


	public T getRecordById(Object id)
	{
		List<T> ents = entities.get(id);

		if (ents == null) return null;

		if (ents.size() == 1) return ents.get(0);

		throw new RuntimeException("Multiple entities exist with entityid " + id);
	}


	public List<T> getEntitiesById(Object id)
	{

		return entities.get(id);
	}


	public Class<? extends Record> getType()
	{

		return type;
	}


	/**
	 * @return the name that this entity type (class) should be displayed with
	 *         when output
	 */
	public String getEntityTypeDisplayName()
	{

		return getEntityTypeDisplayName(type);
	}


	public static String getEntityTypeDisplayName(Class<? extends Record> type)
	{

		String name;

		Table output = type.getAnnotation(Table.class);

		if (output != null)
		{
			name = output.name();
		}
		else
		{
			name = type.getSimpleName();
		}

		return name;
	}


	public void removeRecords(List<T> entities)
	{

		for (T entity : entities)
		{
			this.entities.remove(entity.getKey());
		}
	}


	public void addRecord(T record)
	{

		Object id = record.getKey();

		List<T> records = entities.get(id);

		if (records == null)
		{
			records = new ArrayList<T>();
			entities.put(id, records);
		}

		records.add(record);
	}


	public String getIdOutputName()
	{

		return Entities.getColumnName(EntityHelper.getIdMethod(type));
	}


	public static String getIdOutputName(Class<? extends Record> type)
	{

		return Entities.getColumnName(EntityHelper.getIdMethod(type));
	}
}
