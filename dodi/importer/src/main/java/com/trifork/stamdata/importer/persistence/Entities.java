package com.trifork.stamdata.importer.persistence;

import static com.trifork.stamdata.Preconditions.checkArgument;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.MINUTES;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Id;

import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Ordering;

public final class Entities
{
	private static final Map<Class<?>, Iterable<Method>> columnCache = new MapMaker().expireAfterAccess(1, MINUTES).makeMap(); 
	private static final Map<Class<?>, Method> idColumnCache = new MapMaker().expireAfterAccess(1, MINUTES).makeMap();
	
	protected Entities()
	{
	}
	
	/**
	 * Returns a sorted list of all columns on a persistent entity.
	 * 
	 * @param type The type of the entity to inspect.
	 * 
	 * @return An iterable of columns that are lexically ordered.
	 */
	public static Iterable<Method> getColumns(Class<?> type)
	{
		if (columnCache.containsKey(type)) return columnCache.get(type);
		
		List<Method> columns = Lists.newArrayList();
		
		for (Method method : type.getMethods())
		{
			columns.add(method);
		}
		
		Iterable<Method> sortedColumns = Ordering.usingToString().sortedCopy(columns);
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
	
	public static String getIdColumnNameOfEntity(Class<?> type)
	{
		return getColumnName(getIdColumn(type));
	}
}
