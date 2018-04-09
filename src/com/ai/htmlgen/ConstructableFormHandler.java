/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */

package com.ai.htmlgen;

import com.ai.application.utils.*;
import com.ai.data.*;
import java.util.Vector;
import java.util.Hashtable;
import com.ai.application.interfaces.*;
import java.util.Enumeration;

public class ConstructableFormHandler extends AFormHandlerWithRequestControlHandlers 
{
   private String m_formName;
   private Hashtable m_urlArguments;

   public ConstructableFormHandler( String formName, Hashtable initialArgs )
   {
      init(formName, initialArgs );
   }

   public void addArgument( String key, String value )
   {
      m_urlArguments.put(key, value );
   }      
   public IIterator getKeys()
   {
      Vector v = new Vector();
      for(Enumeration e=m_urlArguments.keys();e.hasMoreElements();)
      {
         v.addElement(e.nextElement());
      }
      return new VectorIterator(v);
   }
   
   public String getValue(final String key )
   {
      String value = (String)m_urlArguments.get(key.toLowerCase());
      if (value == null)
      {
         AppObjects.trace(this,"Value not found for key: %1s", key.toLowerCase() );
         return "";
      }
      return value;
   }
   
   public String getFormName()
   {
      return m_formName;
   }
   public Hashtable getUrlArguments()
   {
      return m_urlArguments;
   }

   private Hashtable convertToLowerCase(Hashtable inHashtable)
   {
      Hashtable table = new Hashtable();
      for(Enumeration e=inHashtable.keys();e.hasMoreElements();)
      {
         String key = (String)e.nextElement();
         table.put(key.toLowerCase(),inHashtable.get(key));
      }
      return table;
   }
   public void init(final String formName, final Hashtable arguments )
   {
        m_formName = formName;
        if (arguments == null) 
        {
         m_urlArguments = new Hashtable();
        }
        else
        {
         m_urlArguments = arguments;
        }
   }
} 