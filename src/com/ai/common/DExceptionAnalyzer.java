/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */


package com.ai.common;

import com.ai.application.interfaces.*;
import java.util.*;
import com.ai.application.utils.*;
import java.io.*;
import com.ai.common.Tokenizer;

// reques.aspire.defaultObjects.ExceptionAnalyzer.className=com.ai.common.DExceptionAnalyzer
public class DExceptionAnalyzer implements ICreator, IExceptionAnalyzer
{

  public Object executeRequest(String requestName, Object args)
          throws RequestExecutionException
  {
      return this;
  }
   public DExceptionAnalyzer() 
   {
   }

   //If the pattern matches return true
   public boolean doYouMatch(Throwable t, final String pattern)
   {
      String message = getRootCause(t);
      if (message == null) return false;
      if (message.indexOf(pattern) == -1)
      {
         // no match
         return false;
      }
      return true;
   }
   
   // Return null if the exception message is null
  public String getRootCauseCode(Throwable t)
   {
         Throwable rootException = null;
         if (t instanceof CommonException)
         {
            CommonException cx = ((CommonException)t);
            rootException = cx.getRootException();
            
         }
         else
         {
            rootException = t;
         }         
         return getRootCauseCodeFromRootException(rootException);
   }
   public String getRootCause(Throwable t)
   {
         if (t instanceof CommonException)
         {
            return ((CommonException)t).getRootCause();
         }
         else
         {
            return t.getMessage();
         }         
   }

   // private
   // Input should be penultimate, no children
   private String getRootCauseCodeFromRootException(Throwable t)
   {
         String rootCauseMessage = t.getMessage();
         if (rootCauseMessage == null) return null;
         if (t instanceof CommonException)
         {
            Vector v =  Tokenizer.tokenize(rootCauseMessage,":");
            if (v.size() > 1)
            {
               return (String)v.elementAt(1);
            }
            else
            {
               return null;
            }
         }
         else
         {
            Vector v =  Tokenizer.tokenize(rootCauseMessage,":");
            return (String)v.elementAt(0);
         }         
   }
   
} 