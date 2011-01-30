package com.trifork.stamdata;


import static java.lang.String.format;

import java.lang.reflect.Method;
import java.sql.Date;
import java.util.*;
import java.util.Map.Entry;

import javax.persistence.Id;



public class EntityHelper
{
	public static Method getIdMethod(Class<? extends Record> entity)
	{
		Method id = null;

		for (Method method : entity.getMethods())
		{
			if (method.isAnnotationPresent(Id.class))
			{
				id = method;
			}
		}

		assert id != null : format("Entity %s does not have a @Id method.", entity.getSimpleName());

		return id;
	}


	public static Method getMethodForColumnName(Class<? extends Record> entitySet, String name)
	{
		for (Method method : Entities.getColumns(entitySet))
		{
			if (name.equals(Entities.getColumnName(method)))
			{
				return method;
			}
		}

		return null;
	}


	public static boolean isDateColumn(Class<? extends Record> entitySet, String name) throws SecurityException, NoSuchMethodException
	{
		return getMethodForColumnName(entitySet, name).getReturnType().equals(Date.class);
	}


	public static boolean isLongColumn(Class<? extends Record> entitySet, String name) throws SecurityException, NoSuchMethodException
	{
		return getMethodForColumnName(entitySet, name).getReturnType().equals(Long.class) || getMethodForColumnName(entitySet, name).getReturnType().equals(long.class);
	}


	public static boolean isBooleanColumn(Class<? extends Record> entitySet, String name) throws SecurityException, NoSuchMethodException
	{
		return getMethodForColumnName(entitySet, name).getReturnType().equals(Boolean.class) || getMethodForColumnName(entitySet, name).getReturnType().equals(boolean.class);
	}


	public static boolean isIntegerColumn(Class<? extends Record> entitySet, String name) throws SecurityException, NoSuchMethodException
	{
		return getMethodForColumnName(entitySet, name).getReturnType().equals(Integer.class) || getMethodForColumnName(entitySet, name).getReturnType().equals(int.class);
	}


	public static boolean isStringColumn(Class<? extends Record> entitySet, String name) throws SecurityException, NoSuchMethodException
	{
		return getMethodForColumnName(entitySet, name).getReturnType().equals(String.class);
	}


	static Set<Class<? extends Record>> resourceTypes = null;


	public static Class<? extends Record> getEntityByName(String name)
	{
		Class<? extends Record> type = null;

		for (Class<? extends Record> resourceType : Entities.all())
		{
			if (Entities.getName(resourceType).equals(name))
			{
				type = resourceType;
				break;
			}
		}

		return type;
	}


	public static int getOrder(Entry<String, Method> o1)
	{
		XmlOrder order = o1.getValue().getAnnotation(XmlOrder.class);

		assert order != null;
		System.out.println(o1.getValue().getName());
		return order.value();
	}
}
