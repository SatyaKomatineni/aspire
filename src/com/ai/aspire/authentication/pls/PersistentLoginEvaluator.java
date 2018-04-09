/*
 *{
 * Copyright (C) 1996 - 2000 Active Intellect, Inc.
 * All Rights Reserved.
 *}
 */
package com.ai.aspire.authentication.pls;
import java.util.Vector;

import com.ai.application.interfaces.ICreator;
import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.aspire.authentication.DefaultAuthentication;
import com.ai.aspire.authentication.IAuthentication1;
import com.ai.aspire.authentication.IPersistentLoginSupport;

/**
 * Based on htmlgen/CommonBEEvaluator
 * See aspire.properties how this is specified
 * 
 * args is a vector
 * args[0] is the function name
 * args[1] userid
 * 
 * Example
 * request.Aspire.BooleanFunction.startswith.classname=com.ai.htmlgen.CommonBEEvaluator
 * request.Aspire.BooleanFunction.same.classname=com.ai.htmlgen.CommonBEEvaluator
 * request.Aspire.BooleanFunction.whitespace.classname=com.ai.htmlgen.CommonBEEvaluator
 * request.Aspire.BooleanFunction.persistentLoginEnabled.classname=com.ai.aspire.authentication.pls.PersistentLoginEvaluator
 *
 */
public class PersistentLoginEvaluator implements ICreator
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
      
      if (lFunction.equals("persistentloginenabled"))
      {
         return new Boolean(isPersistentLoginEnabled(requestName,vArgs));
      }
      else
      {
         AppObjects.error(this,"Error:Unsupported function name:" + function);
         return new Boolean(false);
      }

   }//eof-main-function
   
   private boolean isPersistentLoginEnabled(String requestName, Vector args)
   {
	   try
	   {
		   String userid = (String)args.elementAt(1);
		   AppObjects.info(this,"Evaluating persistent login for user %1s",userid);
		   IAuthentication1 auth = DefaultAuthentication.getAuthenticationObject();
		   IPersistentLoginSupport ipls = auth.getPersistentLoginSupport();
		   return ipls.isPersistentLoginRequested(userid);
	   }
	   catch(Exception x)
	   {
		   AppObjects.error(this,"Problem getting the persistent login support object",x);
		   return true;
	   }
   }

}//eof-class





