/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.common;
import java.util.Hashtable;
import java.util.StringTokenizer;

import com.ai.application.utils.AppObjects;
import com.ai.db.DBException;

/**
 * Substitutes parameters in to an sql statement.
 * The rules are as follows:
 * 0. The inputs are a 'string' and a hashtable of parameters
 * 1. Anything between {} is a token that needs to be replaced from a hashtable
 * 2. An assumption is that all the keys in the hashtable are lowercase
 * 3. There are two cases for the token
 * 3.1 case1: token does not have type specifier such as a quotable string
 * 3.2 if the token is not found in the hash table substitute "null" in the string
 * 3.3 if the token is found and the value is an empty string substitute "null" as well
 * 4.1 case2: token has a type specifier suggesting a quote
 * 4.2 Same logic as above but this time quotes the retrieved value if there is a string
 * 
 * 12/4/15
 * **********************
 * Deprecated
 * Use its optimized versions
 * 
 * This class doesn't use Idictionary args properly as it
 * delegates it to base class which is implemented wrong.
 * it does correctly for hashtable, but the callers will be 
 * inefficient. this is fixed in the following pair.
 *
 * @see SQLArgSubstitutor2Updated
 * @see SQLArgSubstitutor2WithArgValidation
 * 
 * 11/14/15
 * ********************
 * Not sure why I even bother to extend CArgSubstituor?
 * There may be a bug if called with an IDictionary!
 * 
 * This guy will misbehave if input is not a hashtable 
 * tbd: Optimize it later for IDictionary that can work with 
 * either a Map or Hashtable or a Hashmap.
 * 
 * 
 * Originally
 * ***************
 * @version                    1.37, 26 Jun 1996
 */
public class SQLArgSubstitutor extends CArgSubstitutor 
{
   public static void main(String[] args)
   {
      com.ai.application.defaultpkg.ApplicationHolder.initApplication(args[0],args);
      SQLArgSubstitutor s = new SQLArgSubstitutor();
      Hashtable t = new Hashtable();
      t.put("abc","satya,satya,satya");      
      t.put("def","satya'satya");      
      System.out.println(s.substitute("{abc}|def|xyz",t));
      System.out.println(s.substitute("a{abc}|def|xyz",t));
      System.out.println(s.substitute("a{abc.quote}|def|xyz",t));
      System.out.println(s.substitute("a{def.quote}|def|xyz",t));
   }                                   
   @Override
   public String substitute(String inString, 
                     Hashtable stringArguments, 
                     String separatorString,
                     String escapeCharacter )
   {
	   try
	   {
		   String substitutedString = substituteWithExceptions(inString, stringArguments, separatorString, escapeCharacter);
		   return substitutedString;
	   }
	   catch(DBException x)
	   {
		   throw new RuntimeException("DBException from substitute", x);
	   }
   }
   public String substituteWithExceptions(String inString, 
                     Hashtable stringArguments, 
                     String separatorString,
                     String escapeCharacter )
   throws DBException
   {
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
               // even token
               buf.append(curToken);
            }
            else
            {
               // if curToken {somefiled.fieldtype} minus the braces
               FieldParser fieldParser = new FieldParser(curToken);
               if (fieldParser.getFieldType() == null)
               {
            	  //No field type. So a non string field.
                  String value = (String)stringArguments.get(curToken.toLowerCase());
                  
                  //This is a nonstring field. validate it
                  //This will throw an invalid SQLInjection exception
                  validateNonStringField(value);
                  
                  //it it is valid it is unlikely that the following conditions will ever occur
                  //this code is there for backward compatibility
                  if (value == null)
                  {
                     buf.append("null");
                     continue;
                  }
                  if (value.equals("") == true)
                  {
                     buf.append("null");
                     continue;
                  }
                  buf.append(value);
               }
               else
               {
            	  //Field type is available. use a translator to translate
                  String value = (String)stringArguments.get(fieldParser.getFieldName().toLowerCase());
                  
                  //It will be odd if this field value is null
                  //Such a key doesn't exist in the input
                  if (StringUtils.isEmpty(value))
                  {
                	  AppObjects.warn(this, "Value for field %s is null or empty", fieldParser.getFieldName());
                  }
                  ITranslator translator = getTranslator(fieldParser.getFieldType());
                  if (translator != null)
                  {
                	 //Value is translated
                	 String valueToAppend = translator.translateString(value);
                	 validateTypedStringField(fieldParser.getFieldType(), valueToAppend);
                     buf.append(valueToAppend);
                  }
                  else
                  {
                	 //No translator available for this field type
                	 //Value is the value from the input arguments
                	 //This should not happen. log an error
                	 AppObjects.error(this, "No translator found for field type:%s. Value is %s", 
                			 fieldParser.getFieldType(),
                			 value);
                	 //Likely throw an exception
                     validateNonStringField(value);
                     buf.append(value);
                  }
               }                  
            }
         }
         return buf.toString();
   }                    
   private ITranslator getTranslator(final String translatorName)
   {
      try
      {
         String translatorRequest =
         AppObjects.getIConfig().getValue(com.ai.servlets.AspireConstants.SUBSTITUTIONS_CONTEXT + "." + translatorName,null);
         if (translatorRequest == null)
         {
        	AppObjects.error(this, "No translator available for name %s",translatorName);
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
  
   /**
    * Introduced first as part of dynamic list/custom object design
    * This is a specialized case of getTranslator
    * @return ITranslator
    */
   public static ITranslator getQuoteTranslator()
   {
	  final String translatorName = "quote";
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
   //Call this method to evaluate empty fields
   protected void validateNonStringField(String nonStringFieldValue)
   throws DBException
   {
	   //none here in this class. for backward compatibility
   }
   
   //Validate typed values
   protected void validateTypedStringField(String fieldType, String fieldValue)
   throws DBException
   {
	   //none here in this class. for backward compatibility
   }
}//eof-class

