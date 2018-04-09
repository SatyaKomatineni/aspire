/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.htmlgen;
import java.util.*;
import com.ai.common.*;
import com.ai.application.defaultpkg.*;
import com.ai.application.interfaces.*;
import com.ai.application.utils.*;

/**
 * 
 * May 2014
 * *****************************
 * bug fix
 * On a loop handler it is calling getvalue() with a turn
 * this introduces unexpected behavior in GenericTableHandler6
 * 
 * Previous notes as of May 2014
 * ******************************
 * Supports default evaluation of '=' and also the rest of the boolean expression evaluation
 * Remember that (,)= are reserved characters
 * Currently no escape is being supported
 *
 * Rules for a function
 * **********************
 * syntax: <!--RLF_TAG IF func1(a,b,c) -->
 * All the arguments are keys
 * No literals supported
 *
 * Rules for equals
 * *****************
 * syntax: <!--RLF_TAG IF key=literal_value -->
 * or
 * syntax: <!--RLF_TAG IF key=key1.key -->
 *
 * Left hand side is always an aspire key
 * The right hand side is a liternal value after trimming
 * After the right hand side is trimmed if it ends with a .key then it is treated as a key
 * If a key is there then only one "." is allowed
 * Not having a left hand side key in the data set will produce a "false" result
 * Not having a right hand side key in the data set will produce a "false" result
 *
 * If the condition is not behaving as expected check the log file
 * The log file will have the left hand value and right hand value printed
 *
 */
public class SimpleBEEvaluator2 implements IBooleanExpressionEvaluator
                              ,ICreator
{
   public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
   {
      return this;
   }

   /**
    * The expression should be case sensitive
    * Any case insensitive operations will be decided inside
    */
   public boolean evaluate(final String expression
           ,IFormHandler pageData
           ,IControlHandler loopData
           ,int curTurn )
   {
      String newExpression = expression.trim();
      //What is the first character
      boolean bNotInPlace = false;
      int c = newExpression.charAt(0);
      if (c == '!')
      {
         bNotInPlace = true;
         newExpression = newExpression.substring(1);
      }

      boolean ret = false;

       if (isItAFunction(expression) == true)
       {
              //Treat it as a function
              ret = evaluateFunction(newExpression,pageData,loopData,curTurn);
       }
       else
       {
            // Treat it as a direct expression
            ret = evaluateEquals(newExpression,pageData,loopData,curTurn);
       }

       if (bNotInPlace == true)
       {
          return !ret;
       }
       else
       {
          return ret;
       }
   }
   private boolean evaluateEquals(final String expression
                   ,IFormHandler pageData
                   ,IControlHandler loopData
           ,int curTurn )
   {
       // break up into given key/value
       StringTokenizer tknizr = new StringTokenizer(expression,"=");
       String givenKey = tknizr.nextToken().trim();
       String givenValue = tknizr.nextToken().trim();
       
       if(!(loopData instanceof IControlHandler3))
       {
    	   AppObjects.error(this, "Transform passed a control handler that is not supported");
    	   AppObjects.info(this, "Use an older version of this class or upgrade your table handler to 6 or later");
    	   throw new RuntimeException("Mismatched controlhandler");
       }

       String conditionValue=getKeyValueFromData(givenKey,pageData,(IControlHandler3)loopData,curTurn);
       if (conditionValue == null)
       {
           AppObjects.error(this,"IF Condition, no left side key found in the data set. value is null. key:%1s", givenKey);
           return false;
       }
       if (conditionValue == "")
       {
           AppObjects.warn(this,"IF Condition, no left side key found in the data set. value is empty. key:%1s",givenKey);
       }

       //Got the key value
       String targetCompareValue=null;
       String lGivenValue = givenValue;

       String targetKey = extractKey(lGivenValue);
       if (targetKey != null)
       {
            targetCompareValue = getKeyValueFromData(targetKey,pageData,(IControlHandler3)loopData,curTurn);
            if (targetCompareValue == null)
            {
                AppObjects.error(this,"IF Condition, no righthand side key found in the data set. value is null. key:%1s", targetKey);
                return false;
            }
            if (targetCompareValue == "")
            {
                AppObjects.warn(this,"IF Condition, no righthand side key found in the data set. value is empty. key:%1s", targetKey);
            }
       }
       else
       {
           //No target key, means what we have is a literal
           if (givenValue.equals("\"\""))
           {
              //the right hand side is an empty string
              targetCompareValue = "";
           }
           else
           {
              targetCompareValue=givenValue; // Case sensitive
           }
       }

       //We have conditionValue (leftValue) and targetCompareValue(rightValue)
       //Log them for verification
       AppObjects.info(this,"Info: LeftValue|RightValue:%1s | %2s",conditionValue,targetCompareValue);
       return conditionValue.equals(targetCompareValue);
   }

   private String extractKey(String keyValue)
   {
       Vector v = Tokenizer.tokenize(keyValue,".");
       if (v.size() > 1)
       {
           //more than one element
           return (String)v.get(0);
       }
       else
       {
           //not more than one element
           return null;
       }

   }
   private String getKeyValueFromData(String key, IFormHandler pageData, IControlHandler3 loopData, int curTurn)
   {
       if (loopData != null)
       {
           return loopData.getValue(key);
       }
       else
       {
           return pageData.getValue(key);
       }
   }
   private boolean isItAFunction(final String expression)
   {
       // it is a function if it has both ( and )
       if ((expression.indexOf("(")) == -1)
       {
           //( character is not there
           return false;
       }
       if ((expression.indexOf(")")) == -1)
       {
           //( character is not there
           return false;
       }
       //both characters exist
       return true;

   }
   private boolean evaluateFunction(final String caseSensitiveExpression
                   ,IFormHandler pageData
                   ,IControlHandler loopData
           ,int curTurn )
   {
      String expression = caseSensitiveExpression.toLowerCase();
      Vector v = Tokenizer.tokenize(expression,"(,)");
      // v[0] would be function name followed by arguments
      // translate args

      Object obj = null;
      try
      {
         String functionName = (String)v.elementAt(0);

         obj = AppObjects.getIFactory().getObject("Aspire.BooleanFunction." + functionName
                        ,translateArgs(v,pageData,loopData,curTurn)
                        );
      }
      catch (RequestExecutionException x)
      {
         AppObjects.log(x);
         return false;
      }
      if (!(obj instanceof Boolean))
      {
         AppObjects.log("error: Wrong type of object returned by boolean expression evaluator");
         return false;
      }
      Boolean bobj = (Boolean)obj;
      return bobj.booleanValue();
   }

   Vector translateArgs(Vector tokens
           ,IFormHandler pageData
           ,IControlHandler loopData
           ,int curTurn )
   {
      Vector v = new Vector();

      //Add the function itself
      v.add(tokens.elementAt(0));
      for (int i=1;i<tokens.size();i++)
      {
         //Get the key
          String key = (String)tokens.elementAt(i);

          //If the key is a literal just add it and continue
          String literalValue = getLiteralValue(key);
          if (literalValue != null)
          {
             AppObjects.trace(this,"Adding a literal:%1s",literalValue);
             v.add(literalValue);
             continue;
          }

          String value = null;

          //If there is no loop data, just get it from the page data and continue
          if (loopData == null)
          {
            value = pageData.getValue(key);
            v.add(value);
            continue;
          }

          //Loop data available
          if (loopData instanceof IControlHandler3)
          {

            value = ((IControlHandler3)loopData).getValue(key);
          }
          else
          {
            value = loopData.getValue(key,curTurn);
          }
          if (value == null)
          {
            value = pageData.getValue(key);
          }
          else if (value.equals("Field not found"))
          {
            value = pageData.getValue(key);
          }
          else if (value.equals("No data found"))
          {
            value = pageData.getValue(key);
          }
          // value obtained
          v.add(value);
      }// for
      return v;
   }
   private String getLiteralValue(String val)
   {
      String newVal = val.trim();
      if (newVal.equals("\"\""))
      {
         //it is an empty string
         return "";
      }
      if (newVal.charAt(0) == '\"')
      {
         return newVal.substring(1,newVal.length()-1);
      }
      return null;
   }
}


