// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
//
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
//
// Contributor(s): Contributors are attributed in the source code
// where applicable.
//
// The Original Code is "Stamdata".
//
// The Initial Developer of the Original Code is Trifork Public A/S.
//
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
//
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

package com.trifork.stamdata.importer.persistence;

import java.lang.reflect.Method;
import java.util.*;

import org.slf4j.*;

import com.trifork.stamdata.importer.util.Dates;


/**
 * @author Rune Skou Larsen <rsj@trifork.com>
 */
public abstract class AbstractStamdataEntity implements Record
{
	private static final Logger logger = LoggerFactory.getLogger(AbstractStamdataEntity.class);

	private static final Map<Class<? extends Record>, Method> idMethodCache = new HashMap<Class<? extends Record>, Method>();
	static final Map<Method, String> outputFieldNames = new HashMap<Method, String>();

	@Override
	public Object getKey()
	{
		Method idMethod = getIdMethod(getClass());
		try
		{
			return idMethod.invoke(this);
		}
		catch (Exception e)
		{
			logger.error("Error getting id for object of class: " + getClass());
			return null;
		}
	}

	/**
	 * @param type A type of StamdataEntity
	 * @return the getter method that contains the unique id for the given
	 *         StamdataEntity type
	 */
	public static Method getIdMethod(Class<? extends Record> type)
	{
		Method m = idMethodCache.get(type);

		if (m != null) return m;
		
		for (Method method : type.getMethods())
		{
			if (method.isAnnotationPresent(Id.class))
			{
				idMethodCache.put(type, method);
				return method;
			}
		}

		throw new RuntimeException("Could not find idmethod for class: " + type + " A getter must be annotated with @Id!");
	}

	@Override
	public Date getValidTo()
	{
		return Dates.THE_END_OF_TIME;
	}
}
