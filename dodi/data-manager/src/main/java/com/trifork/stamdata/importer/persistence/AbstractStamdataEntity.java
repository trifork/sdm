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


package com.trifork.stamdata.importer.persistence;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Id;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trifork.stamdata.Entities;
import com.trifork.stamdata.importer.util.DateUtils;
import com.trifork.stamdata.models.TemporalEntity;


/**
 * @author Rune Skou Larsen <rsj@trifork.com>
 */
public abstract class AbstractStamdataEntity implements TemporalEntity
{
	private static final Logger logger = LoggerFactory.getLogger(AbstractStamdataEntity.class);

	private static final Map<Class<? extends TemporalEntity>, Method> idMethodCache = new HashMap<Class<? extends TemporalEntity>, Method>();
	private static final Map<Method, String> outputFieldNames = new HashMap<Method, String>();

	public Object getKey()
	{
		return Entities.getEntityID(this);
	}

	/**
	 * @param type A type of StamdataEntity
	 * @return the getter method that contains the unique id for the given
	 *         StamdataEntity type
	 */
	public static Method getIdMethod(Class<? extends TemporalEntity> type)
	{
		Method m = idMethodCache.get(type);

		if (m != null) return m;

		Method[] allMethods = type.getMethods();

		for (Method method : allMethods)
		{
			if (method.isAnnotationPresent(Id.class))
			{
				idMethodCache.put(type, method);
				return method;
			}
		}

		// TODO: This should be an precondition exception.

		logger.error("Could not find idmethod for class: " + type + " A getter must be annotated with @Id!");

		return null;
	}

	/**
	 * @param method A getter method, that is used for serialization.
	 * @return The name used to designate this field when serializing
	 */
	public static String getOutputFieldName(Method method)
	{
		String name = outputFieldNames.get(method);

		if (name == null)
		{
			Column output = method.getAnnotation(Column.class);
			name = method.getName().substring(3); // Strip "get"

			if (output != null && output.name().length() > 0)
			{
				name = output.name();
			}

			outputFieldNames.put(method, name);
		}
		return name;
	}

	public static List<Method> getOutputMethods(Class<? extends TemporalEntity> type)
	{
		Method[] methods = type.getMethods();
		List<Method> outputMethods = new ArrayList<Method>();

		for (Method method : methods)
		{
			if (method.isAnnotationPresent(Column.class)) outputMethods.add(method);
		}

		return outputMethods;
	}

	@Override
	public Date getValidTo()
	{
		return DateUtils.THE_END_OF_TIME;
	}
}
