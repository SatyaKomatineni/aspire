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

public abstract class AReportCalculator implements 
                        IReportTotalsCalculator
                        , ISingleThreaded 
                        , ICreator
   
{
   // from creator
    public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
      {
         return this;
      }
   public void endOfRows()
   {
   }
   // for calculator
   abstract public void processRow(final IDataRow row);
   abstract public String getAggregateValue(final String key);
} 
