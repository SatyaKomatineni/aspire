/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.common;
import java.util.*;

import com.ai.application.interfaces.ConfigException;

public abstract class DDictionary implements IStringDictionary
{
   private Vector v = new Vector();
   public void addChild(IDictionary childDictionary)
   {
      v.addElement(childDictionary);
   }
   public void removeChild(IDictionary childDictionary)
   {
      v.removeElement(childDictionary);
   }
   public String getAsString(String key)
   throws ConfigException
   {
	   Object o = get(key);
	   if (o instanceof String)
	   {
		   return (String)o;
	   }
	   throw new ConfigException("Sorry, String type expected");
   }
   public Object get(Object key)
   {
      Object value = internalGet(key);
      if (value != null)
      {
         return value;
      }
      
      // It is not in this set
      // look for them in the vector
      if (v.size() == 0)
      {
         // empty vector return null
         return null;
      }
      
      for(Enumeration e=v.elements();e.hasMoreElements();)
      {
         IDictionary curDictionary = (IDictionary)e.nextElement();
         value = curDictionary.get(key);
         if (value != null)
         {
            return value;
         }
      }
      // All dictionaries searched
      // no value for the key found
      return null;
   }
   /**
    * return null if you can't find the key 
    */
   public abstract Object internalGet(Object key);
   
   public void getKeys(List list)
   {
      // empty default
   }
} 