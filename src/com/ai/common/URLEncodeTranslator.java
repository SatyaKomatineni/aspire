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
 * Why does a URLEncode worry about nulls?
 * Is this class being used?
 * 
 */
public class URLEncodeTranslator implements ITranslator, ICreator
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
      return java.net.URLEncoder.encode(inString);
  }
} 