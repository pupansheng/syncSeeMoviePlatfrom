package com.pps.movie.db;

import java.util.Collection;

public interface GlobalDb {

   Object getValue(Object key);


   Object putValue(Object key, Object value);

   String putValue(Object value);


   Collection<Object> getAllValues();


   Object deleteValue(Object key);


}
