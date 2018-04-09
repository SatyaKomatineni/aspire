/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.application.defaultpkg;
import java.util.Hashtable;
import java.util.Vector;

public class RequestObjectCache
{
   private Hashtable m_objCache = new Hashtable();
   public void add(String requestName, Vector args, Object o)
   {
      m_objCache.put(requestName,o);
   }
   public void clearCache()
   {
      m_objCache.clear();
   }
} 
