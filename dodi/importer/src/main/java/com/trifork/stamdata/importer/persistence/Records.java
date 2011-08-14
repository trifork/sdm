package com.trifork.stamdata.importer.persistence;

import java.lang.reflect.Method;
import java.util.*;

import com.google.common.collect.*;


public class Records
{

	public static List<Method> getOrderedColumnList(Class<? extends Record> type)
	{
		List<Method> column = Lists.newArrayList();

		for (Method method : type.getMethods())
		{
			if (method.isAnnotationPresent(Output.class))
			{
				column.add(method);
			}
		}

		return Ordering.usingToString().sortedCopy(column);
	}

	/**
	 * @param method
	 *            A getter method, that is used for serialization.
	 * @return The name used to designate this field when serializing
	 */
	public static String getColumnName(Method method)
	{
		String name = AbstractStamdataEntity.outputFieldNames.get(method);

		if (name == null)
		{
			Output output = method.getAnnotation(Output.class);
			name = method.getName().substring(3); // Strip "get"

			if (output != null && output.name().length() > 0)
			{
				name = output.name();
			}

			AbstractStamdataEntity.outputFieldNames.put(method, name);
		}
		return name;
	}

	public static String getTableName(Class<?> type)
	{
		Output output = type.getAnnotation(Output.class);

		if (output != null && !"".equals(output.name()))
		{
			return output.name();
		}

		return type.getSimpleName();
	}
}
