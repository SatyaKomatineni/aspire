/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.common;
import java.util.Vector;
import java.util.Hashtable;

/*
 * 
 * Given a string and a set of arguments in a vector,
 * Use word separators like "{some-key}" to substitute the value
 * of keys and reconstruct the string.
 * 
 * Comments introduced 11/14/15
 * *******************************
 * All methods get rerouted to a base abstract substitute method
 * Argument conversion to a common method which is abstract
 * 
 * Escape characters are allowed looks like. 
 * But I am not sure how they work. I don't recall now
 * 
 * Notice there are 2 abstact methods
 * 
 * Originally written in 1996.
 * 
 */
public abstract class AArgSubstitutor
{
   //**************************
   //Abstract method 1
   //**************************
   public abstract String substitute(String inString,
                     Vector stringArguments,
                     String separatorString,
                     String escapeCharacter );
   
   public String substitute(String inString, Vector stringArguments )
   {
      return substitute( inString, stringArguments, "{}", null );
   }

   //**************************
   //Abstract method 2
   //**************************
   public abstract String substitute(String inString,
           IDictionary stringArguments,
           String separatorString,
           String escapeCharacter );

   //Please note that this method in older code is overriden
   //cauing concerns sometime. Beware of it
   //especially in sqlargsubstitutor
   public String substitute(String inString,
                     Hashtable stringArguments,
                     String separatorString,
                     String escapeCharacter )
  {
     return substitute(inString,new HashtableDictionary(stringArguments),separatorString,escapeCharacter);
  }
   
   public String substitute(String inString, Hashtable stringArguments )
   {
      return substitute( inString, stringArguments, "{}", null );
   }

   public String substitute(String inString, IDictionary stringArguments )
   {
      return substitute( inString, stringArguments, "{}", null );
   }
}

