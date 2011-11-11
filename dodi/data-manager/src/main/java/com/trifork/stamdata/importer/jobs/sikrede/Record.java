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
package com.trifork.stamdata.importer.jobs.sikrede;

import java.util.HashMap;

import com.google.common.collect.ImmutableMap;

public class Record
{
    ImmutableMap<String, Object> map;
    
    Record()
    {
        map = ImmutableMap.of();
    }
    
    Record setField(String fieldName, Object value)
    {
        Record copy = new Record();

        HashMap<String, Object> tempMap = new HashMap<String, Object>(map);
        tempMap.put(fieldName, value);
        copy.map = ImmutableMap.copyOf(tempMap);
        
        return copy;
    }
    
    Object getField(String fieldName)
    {
        return map.get(fieldName);
    }
    
    int size()
    {
        return map.size();
    }
    
    boolean containsKey(String key)
    {
        return map.containsKey(key);
    }
    
    Object get(String key)
    {
        return map.get(key);
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
}
