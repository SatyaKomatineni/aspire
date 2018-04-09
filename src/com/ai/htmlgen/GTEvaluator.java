/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.htmlgen;
import com.ai.common.*;
import com.ai.application.defaultpkg.*;
import com.ai.application.interfaces.*;
import com.ai.application.utils.*;
import java.util.*;
             
public class GTEvaluator implements ICreator 
{

   public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
   {
      if (!(args instanceof Vector))
      {
         throw new RequestExecutionException("Invalid args");
      }
      Vector vArgs = (Vector)args;
      String arg1 = (String)vArgs.elementAt(0);
      String arg2 = (String)vArgs.elementAt(1);
      int iArg1 = Integer.parseInt(arg1);
      int iArg2 = Integer.parseInt(arg2);
      boolean b = iArg1 > iArg2;
      return new Boolean(b);
   }   
} 


 
