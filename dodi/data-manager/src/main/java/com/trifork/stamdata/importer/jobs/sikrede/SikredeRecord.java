package com.trifork.stamdata.importer.jobs.sikrede;

import java.util.HashMap;

import com.google.common.collect.ImmutableMap;

public class SikredeRecord {

    ImmutableMap<String, Object> map;
    
    SikredeRecord()
    {
        map = ImmutableMap.of();
    }
    
    SikredeRecord setField(String fieldName, Object value)
    {
        SikredeRecord copy = new SikredeRecord();

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
}
