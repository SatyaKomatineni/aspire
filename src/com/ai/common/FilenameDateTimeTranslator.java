/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.common;
import com.ai.application.interfaces.ICreator;
import java.text.SimpleDateFormat;
import com.ai.application.interfaces.RequestExecutionException;
import java.util.Date;

public class FilenameDateTimeTranslator implements ITranslator, ICreator
{
   static private SimpleDateFormat milFormat = new SimpleDateFormat("yyyyMMddHHmm");
   
   public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
   {
      return this;
   }   
  public String translateString(final String inString)
  {
     return inString + "." + getDateTimeAsString();
  }
  private String getDateTimeAsString()
  {
      return milFormat.format(new Date());
  } 
} 
