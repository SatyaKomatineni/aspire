package com.ai.parts;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ai.application.interfaces.RequestExecutionException;
import com.ai.application.utils.AppObjects;
import com.ai.aspire.authentication.AuthorizationException;
import com.ai.aspire.authentication.DefaultAuthentication;
import com.ai.aspire.authentication.IAuthentication1;
import com.ai.aspire.authentication.IPersistentLoginSupport;
import com.ai.servlets.AspireSession1;

/**
 * 
 * 5/30/2013
 * Request a persistent login for the logged in user.
 * 
 * @see IPeristentLoginSupport
 * @see RemovePersistentLoginSupport
 */
public class RequestPersistentLoginPart extends AHttpPart
{
   protected Object executeRequestForHttpPart(String requestName
         ,HttpServletRequest request
         ,HttpServletResponse response
         ,HttpSession session
         ,Map inArgs)
         throws RequestExecutionException
   {
	  AspireSession1 as = 
		  AspireSession1.getAspireSessionFromHttpSession(session, true);
	  if (as.getLoggedInStatus() == false)
	  {
		  throw new RequestExecutionException("You need to login to execute this call");
	  }
	  
	  try
	  {
		  //u are logged in
		  String userid = as.getLoggedInUserId();
		  AppObjects.info(this,"Requesting persistent login support for: %1s", userid);
		  IAuthentication1 auth = DefaultAuthentication.getAuthenticationObject();
		  IPersistentLoginSupport ipls = auth.getPersistentLoginSupport();
		  ipls.requestPersistentLoginFor(userid);
	      return new Boolean(true);
	  }
	  catch(AuthorizationException x)
	  {
		  throw new RequestExecutionException("Not able to get persistent login support object",x);
	  }
   }
}//eof-class