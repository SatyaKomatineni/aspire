/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.common;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.ai.application.utils.AppObjects;

public class Utils
{
   static public String convertToString( Enumeration e)
   {
      StringBuffer buf = new StringBuffer();
      while(e.hasMoreElements())
      {
         buf.append((String)e.nextElement());
         buf.append("\n");
      }
      return buf.toString();
   }
   /**
    * Return the true request name.
    * Factory object appends a "request." prefix to the true request name
    * It is useful sometimes to know the true request name
    */
   static public String getTrueRequestString(String compoundRequestName)
   {
        Vector v = Tokenizer.tokenize(compoundRequestName,".");
        return (String)v.elementAt(1);
   }

   static public Hashtable getAsHashtable(Map inMap)
   {
      if (inMap instanceof Hashtable)
      {
         return (Hashtable)inMap;
      }
      else
      {
         Hashtable t = new Hashtable();
         t.putAll(inMap);
         return t;
      }
   }// end of function
   static public void massert(Object srcObject, boolean expression, String msg)
   {
	   if (expression == true) return;
	   AppObjects.error(srcObject, "Assertion Failed: %1s",msg);
	   throw new RuntimeException("Assertion exception:" + msg);
   }
   
   public static Hashtable convertDictionary(IDictionary d)
   {
	   AppObjects.warn("Utils", "This is not a good way to do. Figure out to handle IDictionary directly");
	   Hashtable h = new Hashtable();
	   List l = new ArrayList();
	   d.getKeys(l);
	   for(Object key : l)
	   {
		   Object value = d.get(key);
		   h.put(key, value);
	   }
	   return h;
   }
} //end of class
