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
package com.trifork.stamdata.persistence;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;

public class Record
{
    private Map<String, Object> map = Maps.newHashMap();

    public Record put(String key, Object value)
    {
        map.put(key, value);
        return this;
    }
    
    int size()
    {
        return map.size();
    }
    
    public boolean containsKey(String key)
    {
        return map.containsKey(key);
    }
    
    public Object get(String key)
    {
        return map.get(key);
    }

    public Set<Map.Entry<String, Object>> fields()
    {
        return map.entrySet();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Record record = (Record) o;

        if (map != null ? !map.equals(record.map) : record.map != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        return map != null ? map.hashCode() : 0;
    }

	@Override
	public String toString() {
		return "Record [map=" + map + "]";
	}
 
}
