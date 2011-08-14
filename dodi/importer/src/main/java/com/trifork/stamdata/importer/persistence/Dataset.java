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

import static com.trifork.stamdata.importer.persistence.AbstractStamdataEntity.*;

import java.util.*;


/**
 * @author Rune Skou Larsen <rsj@trifork.com>
 */
public class Dataset<T extends Record>
{
	private final Map<Object, List<T>> entities = new HashMap<Object, List<T>>();
	private final Class<T> type;

	public Dataset(Class<T> type)
	{
		this.type = type;
	}

	public Dataset(List<T> entities, Class<T> type)
	{
		this.type = type;

		for (T entity : entities)
		{
			List<T> ents = new ArrayList<T>();
			ents.add(entity);
			this.entities.put(entity.getKey(), ents);
		}
	}

	public int size()
	{
		return getEntities().size();
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

	public T getEntityById(Object id)
	{
		List<T> ents = entities.get(id);
		if (ents == null)
		{
			return null;
		}
		if (ents.size() == 1)
		{
			return ents.get(0);
		}
		throw new RuntimeException("Multiple entities exist with entityid " + id);
	}

	public List<T> getEntitiesById(Object id)
	{
		return entities.get(id);
	}

	public Class<T> getType()
	{
		return type;
	}

	/**
	 * @return the name that this entity type (class) should be displayed with
	 *         when output
	 */
	public String getEntityTypeDisplayName()
	{
		Output output = type.getAnnotation(Output.class);
		if (output != null && !"".equals(output.name()))
		{
			return output.name();
		}
		return type.getSimpleName();
	}

	public static String getEntityTypeDisplayName(Class<? extends Record> type)
	{
		Output output = type.getAnnotation(Output.class);
		if (output != null && !"".equals(output.name()))
		{
			return output.name();
		}
		return type.getSimpleName();
	}

	public void removeEntities(List<T> entities)
	{
		for (T entity : entities)
		{
			this.entities.remove(entity.getKey());
		}
	}

	public void addEntity(T entity)
	{
		Object id = entity.getKey();

		List<T> ents = entities.get(id);

		if (ents == null)
		{
			ents = new ArrayList<T>();
			entities.put(id, ents);
		}

		ents.add(entity);
	}

	public static String getIdOutputName(Class<? extends Record> clazz)
	{
		return Records.getColumnName(getIdMethod(clazz));
	}
}
