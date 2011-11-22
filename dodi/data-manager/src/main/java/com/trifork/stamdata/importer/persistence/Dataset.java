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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trifork.stamdata.Entities;
import com.trifork.stamdata.models.TemporalEntity;

/**
 * @author Rune Skou Larsen <rsj@trifork.com>
 */
@Deprecated
public class Dataset<T extends TemporalEntity>
{
    private static final Logger logger = LoggerFactory.getLogger(Dataset.class);
    
	private final Map<Object, T> entities = new HashMap<Object, T>();
	private final Class<T> type;

	public Dataset(Class<T> type)
	{
	    this.type = type;
	}

	public Dataset(List<T> records, Class<T> type)
	{
		this(type);

		for (T record : records)
		{
			add(record);
		}
	}

	public int size()
	{
		return getEntities().size();
	}

	public Collection<T> getEntities()
	{
		return entities.values();
	}

	public T getEntityById(Object id)
	{
		return entities.get(id);
	}

	public boolean containsKey(Object id)
	{
		return entities.containsKey(id);
	}

	public Class<T> getType()
	{
		return type;
	}

	public void add(T entity)
	{
		Object id = Entities.getEntityID(entity);
		Object previousValue = entities.put(id, entity);
		
		if (previousValue != null)
		{
            // FIXME: This is actually and error, but it has always been this way.
            // Double keys should not happen.

		    logger.warn("Two entries in a single import contains the same id. type={}, id={}", type.getSimpleName(), id);
		}
	}
}
