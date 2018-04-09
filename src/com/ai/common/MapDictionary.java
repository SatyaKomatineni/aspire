/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.common;
import java.util.*;

/**
 * Use StringMapDictionary
 *
 */
public class MapDictionary 
      extends DDictionary 
      implements IUpdatableDictionary
{
   private Map m_map;
   public MapDictionary(Map m)
   {
      m_map = m;
   }
   public MapDictionary()
   {
      this(new HashMap());
   }
   
   public Object internalGet(Object key)
   {
      return m_map.get(key);
   }
   public void getKeys(List list)
   {
      // empty default
      Iterator itr=m_map.keySet().iterator();
      while(itr.hasNext())
      {
         Object elem = itr.next();
         list.add(elem);
      }
      return;
   }
   public IUpdatableDictionary set(Object key, Object value)
   {
      m_map.put(key,value);
      return this;
   }
    
} 