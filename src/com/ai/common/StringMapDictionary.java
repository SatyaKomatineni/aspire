/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.common;
import java.util.*;

/**
 *
 * Introduced 2014
 * Not fully tested. Use with caution
 */
public class StringMapDictionary 
      extends DDictionary 
      implements IUpdatableDictionary
{
   private Map<String,Object> m_map;
   public StringMapDictionary(Map<String,Object> m)
   {
      m_map = m;
   }
   public StringMapDictionary()
   {
      this(new HashMap<String,Object>());
   }
   
   public Object internalGet(Object key)
   {
      return m_map.get(key);
   }
   public void getKeys(List list)
   {
      // empty default
      Iterator<String> itr=m_map.keySet().iterator();
      while(itr.hasNext())
      {
         String elem = itr.next();
         list.add(elem);
      }
      return;
   }
   public IUpdatableDictionary set(Object key, Object value)
   {
      m_map.put((String)key,value);
      return this;
   }
    
} 