/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.common;                  
import com.ai.application.utils.*;
import com.ai.application.interfaces.*;


/**
 * Knows in one place various translation keys
 * Should replace such things as OracleTranslator etc.
 *
 * Make sure you register the GeneralTranslator against each of the keys
 *
 * Ex:
 *
 */
public class GeneralTranslator implements ITranslator1, ICreator {

   public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
   {
      return this;
   }   

  public String translateString(final String inString)
  {
     return translateString(inString, "default");
  }
   public String translateString(final String inString, final String translateKey)
   {
   
      if (translateKey.equals("empty"))
      {
         return emptyTranslation(inString);
      }
      else if (translateKey.equals("default"))
      {
         return defaultTranslation(inString);
      }
      else if (translateKey.equals("urlencode"))
      {
         return urlEncodeTranslation(inString);
      }
      else
      {
         // unrecognized key
         AppObjects.log("Error: Unrecognized trnslation key:  " + translateKey);
         return "";
      }   
   }
   private String defaultTranslation(String value)
   {
      if (value == null) return "";
      return value;
   }
   
   private String emptyTranslation(String value)
   {
      if (value == null) return "";
      return value;
   }
   
   private String urlEncodeTranslation(String value)
   {
      if (value == null) return "";
      return java.net.URLEncoder.encode(value);
   }
} 
