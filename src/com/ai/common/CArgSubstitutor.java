/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.common;
import java.util.Vector;
import com.ai.application.interfaces.ICreator;
import java.util.StringTokenizer;
import java.util.Hashtable;
import java.util.Enumeration;


/**
 * Stands for Concrete ArgSubstitutor
 * 
 * Logic
 * **************
 * Break a string that looks like "one {key1} ... {key2} two" into
 * one, key1, ..., key2, two
 * Put it back together as "one key1-value ... key2-value two"
 * Notice the escape character is not used here!!
 * May be an enhancement for future
 * 
 * @see SQLArgSubstitutor for advanced substitutions
 * 
 * 12/4/15
 * *************
 * Consider adding a warning if the default methods are called
 * they are likely wrong
 * 
 */
public class CArgSubstitutor extends AArgSubstitutor
{
// No member variables in this class
// as this is a strategy class
   @Override
   public String substitute(String inString,
                     Vector stringArguments,
                     String separatorString,
                     String escapeCharacter )
   {
         StringBuffer buf = new StringBuffer();
         StringTokenizer tokenizer = new StringTokenizer(inString,separatorString );
         for(int i=0;tokenizer.hasMoreTokens();i++)
         {
            String curToken = tokenizer.nextToken();
            if (i%2 == 0)
            {
               // even token
               buf.append(curToken);
            }
            else
            {
               buf.append(stringArguments.elementAt(i/2));
            }
         }
         return buf.toString();
   }

   @Override
   public String substitute(String inString,
                     IDictionary stringArguments,
                     String separatorString,
                     String escapeCharacter )
   {
         StringBuffer buf = new StringBuffer();
         StringTokenizer tokenizer = new StringTokenizer(inString,separatorString );
         for(int i=0;tokenizer.hasMoreTokens();i++)
         {
            String curToken = tokenizer.nextToken();
            if (i%2 == 0)
            {
               // even token
               buf.append(curToken);
            }
            else
            {
               buf.append(stringArguments.get(curToken.toLowerCase()));
            }
         }
         return buf.toString();
   }
}
