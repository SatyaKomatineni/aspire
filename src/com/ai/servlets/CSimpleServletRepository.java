/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.servlets;
import java.util.Hashtable;

public class CSimpleServletRepository implements IServletRepository 
{
   static private CSimpleServletRepository m_instance = null;
   Hashtable m_storedObjects = new Hashtable();
   
  public CSimpleServletRepository() 
  {
  }
   static public synchronized IServletRepository getInstance()
   {
      if (m_instance != null) return m_instance;
      m_instance = new CSimpleServletRepository();
      return m_instance;
   }
   public Object getObject(final String name )
   {
      return m_storedObjects.get(name);
   }
   public void setObject(final String name, Object obj)
   {
      m_storedObjects.put(name, obj);
   }
} 