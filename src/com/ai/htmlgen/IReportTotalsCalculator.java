/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.htmlgen;
import com.ai.data.IDataRow;
public interface IReportTotalsCalculator 
{
   public void processRow(final IDataRow row);
   public String getAggregateValue(final String key);
   public void endOfRows();
} 
