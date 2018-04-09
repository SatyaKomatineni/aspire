/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.common;
import java.util.*;

import com.ai.application.interfaces.ICreator;
import com.ai.application.interfaces.RequestExecutionException;

/**
 * The input string is going to be a 
 * comma separated list of ids
 * as in "select * from x where someid in (111,22,33)"
 * 
 * it will look as
 * select * from x where someid in ({arg.sqlids})
 * 
 * This returns the same input string
 * no translations.
 * this is just a sanity check.
 * Check to make sure they are actually ids.
 */
public class SQLInIdsClauseTranslator implements ITranslator, ICreator
{
   public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
   {
      return this;
   }   
  public String translateString(final String inString)
  {
	  if (StringUtils.isEmpty(inString))
	  {
		  throw new RuntimeException("Invalid SQL in clause argument:Empty string");
	  }
	  //There is something in the string
      List<String> items = Tokenizer.tokenizeAsList(inString,",");
      
      //There will be at least one item
      //each item  must be an integer like an id
      //Check to see if each is a valid value
      for(String id:items)
      {
    	  //This will throw an illegal NumberFormatException
    	  //satisfying our check
    	  Long idValue = Long.parseLong(id);
      }
      return inString;
  }
} 