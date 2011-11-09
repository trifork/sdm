/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */
package com.trifork.stamdata;

import static com.trifork.stamdata.Preconditions.checkArgument;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.MINUTES;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Ordering;
import com.trifork.stamdata.models.TemporalEntity;

public final class Entities
{
	private static final Map<Class<?>, List<Method>> columnCache = new MapMaker().expireAfterAccess(1, MINUTES).makeMap();
	private static final Map<Class<?>, Method> idColumnCache = new MapMaker().expireAfterAccess(1, MINUTES).makeMap();

	protected Entities()
	{
	}

	public static Object getEntityID(Object entity)
	{
		try
		{
			return Entities.getIdColumn(entity.getClass()).invoke(entity);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Could not get the entity's ID.", e);
		}
	}

	/**
	 * Returns a sorted list of all columns on a persistent entity.
	 * 
	 * @param type
	 *            The type of the entity to inspect.
	 * 
	 * @return An iterable of columns that are lexically ordered.
	 */
	public static List<Method> getColumns(Class<?> type)
	{
		if (columnCache.containsKey(type)) return columnCache.get(type);

		List<Method> columns = Lists.newArrayList();

		for (Method method : type.getMethods())
		{
			columns.add(method);
		}

		List<Method> sortedColumns = Ordering.usingToString().sortedCopy(columns);
		columnCache.put(type, sortedColumns);

		return sortedColumns;
	}

	public static String getColumnName(Method column)
	{
		checkArgument(column.isAnnotationPresent(Column.class), format("The method '%s' is not annotated with @Column.", column.toString()));
		Column annotation = column.getAnnotation(Column.class);

		if (annotation.name() != null && !annotation.name().isEmpty())
		{
			return annotation.name();
		}

		String name = column.getName();
		checkArgument(name.startsWith("get") && name.length() > 3, "Entity columns must have the format: getX(). The given method does not. " + column.toString());
		return name.substring(3);
	}

	public static Method getIdColumn(Class<?> type)
	{
		Method idColumn = idColumnCache.get(type);
		if (idColumn != null) return idColumn;

		for (Method column : getColumns(type))
		{
			if (column.isAnnotationPresent(Id.class))
			{
				return column;
			}
		}

		throw new IllegalArgumentException(format("The type '%s' does not have a method annotated with @Id.", type.getCanonicalName()));
	}

    public static String getEntityTypeDisplayName(Class<?> type)
    {
    	Entity output = type.getAnnotation(Entity.class);
    	if (output != null && !output.name().isEmpty()) return output.name();
    	return type.getSimpleName();
    }

    public static List<Method> getOutputMethods(Class<? extends TemporalEntity> type)
    {
    	Method[] methods = type.getMethods();
    	List<Method> outputMethods = Lists.newArrayList();
    
    	for (Method method : methods)
    	{
    		if (method.isAnnotationPresent(Column.class)) outputMethods.add(method);
    	}
    
    	return outputMethods;
    }

    /**
     * @param method A getter method, that is used for serialization.
     * @return The name used to designate this field when serializing
     */
    public static String getOutputFieldName(Method method)
    {
    	
    		Column output = method.getAnnotation(Column.class);
    	String name = method.getName().substring(3); // Strip "get"
    
    		if (output != null && output.name().length() > 0)
    		{
    			name = output.name();
    		}
    	
    	return name;
    }

    /**
     * @param type A type of StamdataEntity
     * @return the getter method that contains the unique id for the given
     *         StamdataEntity type
     */
    public static Method getIdMethod(Class<?> type)
    {
    	Method[] allMethods = type.getMethods();
    
    	for (Method method : allMethods)
    	{
    		if (method.isAnnotationPresent(Id.class))
    		{
    			return method;
    		}
    	}
    
    	return null;
    }

    public static String getIdColumnName(Class<?> entityType)
    {
    	return getOutputFieldName(getIdMethod(entityType));
    }
}
