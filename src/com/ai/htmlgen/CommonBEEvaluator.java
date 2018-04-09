/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.htmlgen;
import java.util.Vector;

import com.ai.application.interfaces.ICreator;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;

public class CommonBEEvaluator implements ICreator
{

   public Object executeRequest(String requestName, Object args)
      throws RequestExecutionException
   {
      if (!(args instanceof Vector))
      {
         throw new RequestExecutionException("Invalid args");
      }
      Vector vArgs = (Vector)args;
      AppObjects.trace(this, "Passed in vector args are:%1s", vArgs);
      String function = (String)vArgs.elementAt(0);
      String lFunction = function.toLowerCase();
      if (lFunction.equals("stringequals"))
      {
         return new Boolean(stringEquals(requestName,vArgs));
      }
      else if (lFunction.equals("whitespace"))
      {
         return new Boolean(whiteSpace(requestName,vArgs));
      }
      else if (lFunction.equals("gt"))
      {
         return new Boolean(gt(requestName,vArgs));
      }
      else if (lFunction.equals("gte"))
      {
         return new Boolean(gte(requestName,vArgs));
      }
      else if (lFunction.equals("lt"))
      {
         return new Boolean(lt(requestName,vArgs));
      }
      else if (lFunction.equals("lte"))
      {
         return new Boolean(lte(requestName,vArgs));
      }
      else if (lFunction.equals("numberequals"))
      {
         return new Boolean(numberEquals(requestName,vArgs));
      }
      else if (lFunction.equals("same"))
      {
         return new Boolean(same(requestName,vArgs));
      }
      else if (lFunction.equals("startswith"))
      {
         return new Boolean(startsWith(requestName,vArgs));
      }
      else
      {
         AppObjects.error(this,"Error:Unsupported function name:" + function);
         return new Boolean(false);
      }

   }

   private boolean stringEquals(String requestName, Vector args)
   {
      String arg1=(String)args.elementAt(1);
      String arg2=(String)args.elementAt(2);
      return arg1.equals(arg2);
   }
   private boolean whiteSpace(String requestName, Vector args)
   {
      String arg1=(String)args.elementAt(1);
      if (arg1.trim().equals(""))
      {
         return true;
      }
      return false;
   }

   private boolean gt(String requestName, Vector args)
   {
      try
      {
        String arg1=(String)args.elementAt(1);
        String arg2=(String)args.elementAt(2);
        int iArg1 = Integer.parseInt(arg1);
        int iArg2 = Integer.parseInt(arg2);
        return iArg1 > iArg2;
      }
      catch(Throwable t)
      {
        AppObjects.log("Warn:function gt exception. returning false",t);
        return false;
      }
   }

   private boolean numberEquals(String requestName, Vector args)
   {
      try {
      String arg1=(String)args.elementAt(1);
      String arg2=(String)args.elementAt(2);
      int iArg1 = Integer.parseInt(arg1);
      int iArg2 = Integer.parseInt(arg2);
      return iArg1 == iArg2;
      }
      catch(Throwable t)
      {
        AppObjects.log("Warn:function numberEquals exception. returning false",t);
        return false;
      }
   }

   private boolean lt(String requestName, Vector args)
   {
      try {
      String arg1=(String)args.elementAt(1);
      String arg2=(String)args.elementAt(2);
      int iArg1 = Integer.parseInt(arg1);
      int iArg2 = Integer.parseInt(arg2);
      return iArg1 < iArg2;
      }
      catch(Throwable t)
      {
        AppObjects.log("Warn:function lt exception. returning false",t);
        return false;
      }
   }

   private boolean gte(String requestName, Vector args)
   {
      try {
      String arg1=(String)args.elementAt(1);
      String arg2=(String)args.elementAt(2);
      int iArg1 = Integer.parseInt(arg1);
      int iArg2 = Integer.parseInt(arg2);
      return iArg1 >= iArg2;
      }
      catch(Throwable t)
      {
        AppObjects.log("Warn:function gte exception. returning false",t);
        return false;
      }
   }

   private boolean lte(String requestName, Vector args)
   {
      try {
      String arg1=(String)args.elementAt(1);
      String arg2=(String)args.elementAt(2);
      int iArg1 = Integer.parseInt(arg1);
      int iArg2 = Integer.parseInt(arg2);
      return iArg1 <= iArg2;
      }
      catch(Throwable t)
      {
        AppObjects.log("Warn:function lte exception. returning false",t);
        return false;
      }
   }
   private boolean startsWith(String requestName, Vector args)
   {
      try 
      {
	      String arg1=(String)args.elementAt(1);
	      String arg2=(String)args.elementAt(2);
	      if (arg1.startsWith(arg2))
	      {
	    	  return true;
	      }
	      else
	      {
	    	  return false;
	      }
      }
      catch(Throwable t)
      {
        AppObjects.log("Warn:function startsWith exception. returning false",t);
        return false;
      }
   }
   private boolean same(String requestName, Vector args)
   {
      String arg1=(String)args.elementAt(1);
      int len = args.size();
      for (int i=2;i<len;i++)
      {
      	String argi = (String)args.elementAt(i);
      	if (!(argi.equalsIgnoreCase(arg1)))
      	{
      		//they are unequal
      		return false;
      	}
      }
      return true;
   }
}





