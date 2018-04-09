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

public class SimpleBEEvaluator implements IBooleanExpressionEvaluator 
                              ,ICreator
{
   public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
   {
      return this;
   }   
   public boolean evaluate(final String expression
           ,IFormHandler pageData
           ,IControlHandler loopData
           ,int curTurn )
   {
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
      for (int i=1;i<tokens.size();i++)
      {
          String key = (String)tokens.elementAt(i);
          String value = null;
          if (loopData == null)
          {
            value = pageData.getValue(key);
          }
          else      
          {
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
          } 
          // value obtained
          v.add(value);
      }// for
      return v;
   }           
                                       
} 
