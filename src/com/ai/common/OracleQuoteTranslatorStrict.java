/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.common;

import com.ai.application.interfaces.ICreator;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.db.SQLInjectionException;


/**
 * 
 * 11/29/15 (tbd)
 * *************
 * Not sure why, I am noticing multiple instances of this
 * in the log file. This should be a singleton! 
 * Debug this later
 * 
 */
public class OracleQuoteTranslatorStrict implements ITranslator2, ICreator
{
   public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
   {
      return this;
   }   
   
  @Override
  public String translateString(final String inString)
  {
	  return translateString(UNKNOWN,inString,UNKNOWN);
  }
  public String translateString(final String inString, final String translateKey)
  {
	  return translateString(UNKNOWN,inString,translateKey);
  }
  
	/**
	 * @param fieldName : Used for reference and debugging
	 * @param fieldValue : This is the value to be translated
	 * @param fieldType : The type of field 
	 * @return translated value
	 */
  public String translateString(final String fieldName,
		   final String fieldValueToBeTranslated, 
		   final String fieldType)
  {
	  try
	  {
		  return translateStringWithExceptions(fieldValueToBeTranslated);
	  }
	  catch(SQLInjectionException x)
	  {
		   //problem
		   AppObjects.error(this, "Problem translating. fiedlName:%s, fieldType:%s"
				   ,fieldName
				   ,fieldType);
		  throw new RuntimeException("This is a required field. Null not allowed. Use a different qualifier.", x);
	  }
  }
  private String translateStringWithExceptions(final String inString)
  throws SQLInjectionException
  {
	  //Don't allow nulls
      if (inString == null) 
      {
    	  throw new SQLInjectionException("Key not found in arguments");
      }
      if (StringUtils.isEmpty(inString))
      {
    	  throw new SQLInjectionException("Value for the specificed key is an empty string");
      }
      
      //The following has to be allowed
      //if (inString.trim().equalsIgnoreCase("null"))
      //{
      //	  throw new SQLInjectionException("Value passed is null. Not allowed:" + inString);
      //}
      
      int quoteIndex = inString.lastIndexOf('\'');
      if (quoteIndex == -1)
      {
         // there is no quote
         return ("'" + inString + "'" );
      }
      return "'" + StringUtils.encode(inString,'\'','\'','\'') + "'";
  }
}//eof-class 