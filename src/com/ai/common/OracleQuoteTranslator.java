/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.common;

import com.ai.application.interfaces.ICreator;
import com.ai.application.interfaces.RequestExecutionException;


/**
 * 
 * 11/29/15 (tbd)
 * *************
 * Not sure why, I am noticing multiple instances of this
 * in the log file. This should be a singleton! 
 * Debug this later
 * 
 */
public class OracleQuoteTranslator implements ITranslator, ICreator
{
   public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
   {
      return this;
   }   
  public String translateString(final String inString)
  {
      if (inString == null) 
      {
          return "null";
      }
      if (inString.equals("") == true)
      {
         return "null";
      }
      int quoteIndex = inString.lastIndexOf('\'');
      if (quoteIndex == -1)
      {
         // there is no quote
         return ("'" + inString + "'" );
      }
      return "'" + StringUtils.encode(inString,'\'','\'','\'') + "'";
  }
} 