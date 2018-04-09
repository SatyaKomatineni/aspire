/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.htmlgen.test;
import com.ai.htmlgen.*;
import com.ai.application.interfaces.*;
import com.ai.data.*;

public class TestReportCalculator extends AReportCalculator
{
   // member variables      
   private int m_total = 0;

   // constructor
   public TestReportCalculator() 
   {
   }
   // for calculator
   public void processRow(final IDataRow row)
   {
      String col1 = row.getValue(1);
      m_total += Integer.parseInt(col1);
   }
   
   public String getAggregateValue(final String key)
   {
      return  Integer.toString(m_total);  
   }
} 
