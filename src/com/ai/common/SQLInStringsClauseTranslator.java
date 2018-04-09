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
import com.ai.application.utils.AppObjects;

/**
 * The input string is going to be a 
 * comma separated list of string values
 * as in "select * from x where someid in ('111ddd','22','33')"
 * 
 * it will look as
 * select * from x where someid in ({arg.in-clause-strings})
 * 
 * This returns the same input string
 * no translations.
 * this is just a sanity check.
 * Check to make sure they are actually ids.
 */
public class SQLInStringsClauseTranslator implements ITranslator, ICreator
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
		  AppObjects.warn(this, "Invalid SQL in clause argument:Empty string");
		  return "\'\'";
	  }
	  //There is something in the string
      List<String> items = Tokenizer.tokenizeAsList(inString,",");
      StringBuffer ns = new StringBuffer();
      
      boolean first = true;
      //There will be at least one item
      //each item  must be an integer like an id
      //Check to see if each is a valid value
      for(String itemname:items)
      {
    	  if (first == true)
    	  {
    		  //dont put a comma
    		  first = false;
    	  }
    	  else
    	  {
    		  //it is not a first
    		  //put a comma;
        	  ns.append(",");
    	  }
    	  ns.append("'" + itemname + "'");
      }
      return ns.toString();
  }
} 