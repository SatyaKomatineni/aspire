/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.htmlgen;

import com.ai.htmlgen.*;
import com.ai.application.interfaces.*;
import com.ai.data.*;
import java.util.*;
import com.ai.application.utils.*;
import com.ai.common.*;

public class DefaultTotalsCalculator implements 
                        IReportTotalsCalculator
                        , ISingleThreaded 
                        , ICreator
   
{
   HashMap  m_colMap;
   String   m_loopName;
   int m_loopSize;
   
   // from creator
    public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
   {

      // Figure out a loop name
      String loopName = null;
      if (args != null)
      {
         loopName = (String)args;
      }
      else
      {
         loopName=getLoopName(requestName);
      }
      try
      {
         // Read columns strings
         String columnString = AppObjects.getIConfig().getValue(requestName + ".columns");
         Vector cols = Tokenizer.tokenize(columnString,",");

         // Create a hashmap with strings
         HashMap map = new HashMap();
      
         for(Enumeration e=cols.elements();e.hasMoreElements();)
         {
            String col = (String)e.nextElement();
            map.put(col.toLowerCase(),new Integer(0));
         }
      
         // Initialize the object
         init(loopName,map);
         return this;
      }
      catch(ConfigException x)
      {
         throw new RequestExecutionException("Error: DefaultReportTotalsExecutor config error",x);
      }
   }
   
   private String getLoopName(String requestName)
   {
      
      Vector v = Tokenizer.tokenize(requestName, ".");
      String loopName = (String)v.elementAt(v.size()-2);
      
      // see if user prefers a different prefix
      return AppObjects.getIConfig().getValue(requestName + ".totalsPrefix",loopName);
   }
   
   public void init(String loopName, HashMap columns)
   {
      m_loopName = loopName;
      m_colMap = columns;
      m_loopSize = loopName.length();
   }      
   public void endOfRows()
   {
   }
   public void processRow(final IDataRow row)
   {
      for (Iterator itr=m_colMap.keySet().iterator(); itr.hasNext();)
      {
         String key=(String)itr.next();
         String value = "0";
         try {value=row.getValue(key);} catch(FieldNameNotFoundException x)
         {
            AppObjects.log("Error: problem in calculating totals. ",x);
         }
         addElement(key,value);
      }
   }

   private void addElement(String key, String inValue)
   {
      Integer value = (Integer)m_colMap.get(key);
      int val = value.intValue();
      int inVal = Integer.parseInt(inValue);
      m_colMap.put(key,new Integer(val + inVal));
   }
   
   public String getAggregateValue(final String key)
   {
      if (key.length() <= m_loopSize)
      {
         return null;
      }
      String col = key.substring(m_loopSize + 1);
      Integer t_val = (Integer)m_colMap.get(col);
      if (t_val == null) 
      {
         AppObjects.warn(this,"Key not found in totals. %1s : %2s", key, col );
         return null;
      }
      return Integer.toString(t_val.intValue());
   }
} 
