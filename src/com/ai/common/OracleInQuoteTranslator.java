/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
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
 * comma separated and comma encoded string
 * 
 * the output is take each value and single quote it.
 * Just like the "quote" version of this function.
 * 
 * ex:
 * var arg = blah,blah,blah3
 * select * from in (arg.inquote)
 * 
 * will make it
 * 
 * select * from in ('blah','blah','blah3')
 */
public class OracleInQuoteTranslator implements ITranslator, ICreator
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
      Vector items = Tokenizer.tokenize(inString,",");
      StringBuffer finalString = new StringBuffer();
      int i=0;
      for(Enumeration e=items.elements();e.hasMoreElements();)
      {
         String itemString = (String)e.nextElement();
         if (i==0)
         {
            finalString.append(encodeSingleQuotes(itemString));
         }
         else
         {
            finalString.append( "," + encodeSingleQuotes(itemString));
         }
         i++;
      }
      return finalString.toString();
  }
  private String encodeSingleQuotes(String inString)
  {
      int quoteIndex = inString.lastIndexOf('\'');
      if (quoteIndex == -1)
      {
         // there is no quote
         return ("'" + inString + "'" );
      }
      return "'" + StringUtils.encode(inString,'\'','\'','\'') + "'";
  }
} 