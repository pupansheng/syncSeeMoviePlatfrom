package com.pps.movie.db;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author
 * @discription;基于本地map的缓存
 * @time 2020/12/24 14:26
 */
@Component
public class LocalCacheDb implements GlobalDb {

    private static final ConcurrentHashMap<Object,Object> MEMORY_STORAGE=new ConcurrentHashMap<>();
    @Override
    public Object getValue(Object key) {
        return MEMORY_STORAGE.get(key);
    }

    @Override
    public String putValue(Object value) {
        String s = UUID.randomUUID().toString();
        putValue(s,value);
        return  s;
    }

    @Override
    public Object putValue(Object key, Object value) {
      return   MEMORY_STORAGE.put(key,value);
    }
    @Override
    public Collection<Object> getAllValues() {
       return MEMORY_STORAGE.values();
    }

    @Override
    public Object deleteValue(Object key) {
        return MEMORY_STORAGE.remove(key);
    }
}
