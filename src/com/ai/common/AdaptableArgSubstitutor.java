/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.common;
import com.ai.application.interfaces.ICreator;
import com.ai.application.utils.*;
import java.util.StringTokenizer;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.*;

public class AdaptableArgSubstitutor extends AArgSubstitutor
{
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
    public String substitute(String inString,
                     IDictionary stringArguments,
                     String separatorString,
                     String escapeCharacter )
   {
/*      // temporary fix for case sensitivity
      Hashtable stringArguments = new Hashtable();
      for (Enumeration e=inStringArguments.keys();e.hasMoreElements();)
      {
         String key = (String)e.nextElement();
         stringArguments.put(key.toLowerCase(),inStringArguments.get(key));
      }
*/
        StringBuffer buf = new StringBuffer();
         StringTokenizer tokenizer = new StringTokenizer(inString,separatorString );
         int i;
         if(separatorString.indexOf(inString.charAt(0)) == -1)
         {
            // doesn't start with a separator
            i=0;
         }
         else
         {
            i=1;
         }
         for(;tokenizer.hasMoreTokens();i++)
         {
            String curToken = tokenizer.nextToken();
            if (i%2 == 0)
            {
               // even token that is a plain string
               buf.append(curToken);
            }
            else
            {
               // odd token that is a key
               buf.append(translate(curToken.toLowerCase(),stringArguments));
            }
         }
         return buf.toString();
   }
   /*
    * Over ride this method
    */
   protected String translate(String key, IDictionary keyValMap)
   {
      return (String)keyValMap.get(key);
   }
   /**
    * utility function to load a translator based on a translatorName
    */
   public static  ITranslator getTranslator(final String translatorName)
   {
      try
      {
         String translatorRequest =
         AppObjects.getIConfig().getValue(com.ai.servlets.AspireConstants.SUBSTITUTIONS_CONTEXT + "." + translatorName,null);
         if (translatorRequest == null)
         {
            return null;
         }
         Object obj =
         AppObjects.getIFactory().getObject(translatorRequest,null);
         return (ITranslator)obj;
      }
      catch(com.ai.application.interfaces.RequestExecutionException x)
      {
         AppObjects.log(x);
         return null;
      }
   }

}
